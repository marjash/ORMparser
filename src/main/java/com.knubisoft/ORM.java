package com.knubisoft;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class ORM implements ORMInterface {

    @Override
    @SneakyThrows
    public <T> List<T> readAll(DataReadWriteSource<?> inputSource, Class<T> cls) {
        Table table = convertToTable(inputSource);
        return convertTableToListOfClasses(table, cls);
    }

    @SneakyThrows
    @Override
    public <T> void writeAll(DataReadWriteSource<?> source, List<T> objects) {
        if (source instanceof FileReadWriteSource) {
            ParsingStrategy<FileReadWriteSource> parsingStrategy = getStringParsingStrategy((FileReadWriteSource) source);
            parsingStrategy.write(source, objects);
        }
//        JSONParsingStrategy.write(source, objects);
    }

    private <T> List<T> convertTableToListOfClasses(Table table, Class<T> cls) {
        List<T> result = new ArrayList<>();
        for (int index = 0; index < table.size(); index++) {
            Map<String, String> row = table.getTableRowByIndex(index);
            T instance = reflectTableRowToClass(row, cls);
            result.add(instance);
        }
        return result;
    }

    @SneakyThrows
    private <T> T reflectTableRowToClass(Map<String, String> row, Class<T> cls) {
        T instance = cls.getDeclaredConstructor().newInstance();
        for (Field each : cls.getDeclaredFields()) {
            each.setAccessible(true);
            String value = row.get(each.getName());
            if (value != null) {
                each.set(instance, transformValueToFieldType(each, value));
            }
        }
        return instance;
    }

    private static Object transformValueToFieldType(Field field, String value) {
        Map<Class<?>, Function<String, Object>> typeToFunction = new LinkedHashMap<>();
        typeToFunction.put(String.class, s -> s);
        typeToFunction.put(int.class, Integer::parseInt);
        typeToFunction.put(Float.class, Float::parseFloat);
        typeToFunction.put(LocalDate.class, LocalDate::parse);
        typeToFunction.put(LocalDateTime.class, LocalDate::parse);
        typeToFunction.put(Long.class, Long::parseLong);
        typeToFunction.put(BigInteger.class, BigInteger::new);

        return typeToFunction.getOrDefault(field.getType(), type -> {
            throw new UnsupportedOperationException("Type is not supported by parser " + type);
        }).apply(value);
    }

    private Table convertToTable(DataReadWriteSource dataInputSource) {
        if (dataInputSource instanceof ConnectionReadWriteSource) {
            ConnectionReadWriteSource databaseSource = (ConnectionReadWriteSource) dataInputSource;
            return new DatabaseParsingStrategy().parseToTable(databaseSource);
        } else if (dataInputSource instanceof FileReadWriteSource) {
            FileReadWriteSource fileSource = (FileReadWriteSource) dataInputSource;
            return getStringParsingStrategy(fileSource).parseToTable(fileSource);
        } else {
            throw new UnsupportedOperationException("Unknown DataInputSource " + dataInputSource);
        }
    }

    private ParsingStrategy<FileReadWriteSource> getStringParsingStrategy(FileReadWriteSource inputSource) {
//        String content = inputSource.getContent();
//        char firstChar = content.charAt(0);
        String name = inputSource.getSource().getName();
        if (name.endsWith(".json"))
            return new JSONParsingStrategy();
        if (name.endsWith(".xml"))
            return new XMLParsingStrategy();
        if (name.endsWith(".csv"))
            return new CSVParsingStrategy();
        return null;
//        switch (name) {
//            case '{':
//            case '[':
//            case '<':
//                return new XMLParsingStrategy();
//            default:
//                return new CSVParsingStrategy();
//        }
    }

    @RequiredArgsConstructor
    static class Table {
        private final Map<Integer, Map<String, String>> table;
        int size() {
            return table.size();
        }

        Map<String, String> getTableRowByIndex(int row) {
            Map<String, String> result = table.get(row);
            return result == null ? null : new LinkedHashMap<>(result);
        }
    }
}
