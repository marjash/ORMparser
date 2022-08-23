package com.knubisoft;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Main {
    private static final ORMInterface ORM = new ORM();
    public static void main(String[] args) throws Exception {
        Object objectToInsert = new Person("TEST3", 10, 1000,
                "DEVELOPER", LocalDate.parse("1990-10-20"));
        withConnection(new Function<Connection, Void>() {
            @SneakyThrows
            @Override
            public Void apply(Connection connection) {
                ModelSQLHelper helper = new ModelSQLHelper(collectMetaInformation(connection, objectToInsert));
                String sql = helper.buildSQL(objectToInsert);
                PreparedStatement ps = connection.prepareStatement(sql);
                helper.bindArguments(objectToInsert, ps);
                ps.execute();
                return null;
            }
            private List<String> collectMetaInformation(Connection connection,
                                                        Object objectToInsert) {
                // SELECT * FROM objectToInsert > annotation > person
                // ResultSet.getMetadata()
                return List.of("name","age","position","salary","dateOfBirth");
            }
            @SneakyThrows
            private void bindArguments(PreparedStatement ps, Object objectToInsert) {
                int index = 1;
                for (Field field: objectToInsert.getClass().getDeclaredFields()){
                    field.setAccessible(true);
                    ps.setObject(index, field.get(objectToInsert));
                    index++;
                }
            }
        });
    }

    @RequiredArgsConstructor
    static class ModelSQLHelper {
        private final List<String> availableFieldInDatabase;

        public String buildSQL(Object o){
            Class<? extends Object> cls = o.getClass();
            String tableName = getTableName(cls);
            String fields = getFields(cls);
            String arguments = getArguments(cls);
            return String.format("INSERT INTO %s (%s) VALUES (%s);",
                    tableName, fields, arguments);
        }

        private String getArguments(Class<?> cls) {
            List<Field> fields = Arrays.asList(cls.getDeclaredFields());
            List<String> listFieldNames = fields.stream().map(Field::getName)
                    .filter(availableFieldInDatabase::contains).map(field -> "?").
                    collect(Collectors.toList());
            return String.join(",", listFieldNames);
        }
        private String getFields(Class<?> cls) {
            List<Field> fields = Arrays.asList(cls.getDeclaredFields());
            List<String> listFieldNames = fields.stream().map(Field::getName)
                    .filter(availableFieldInDatabase::contains).
                    collect(Collectors.toList());
            return String.join(",", listFieldNames);
        }
        private String getTableName(Class<?> cls) {
            return cls.getAnnotation(TableName.class).name();
        }
        @SneakyThrows
        public void bindArguments(Object o, PreparedStatement ps){
            int index = 1;
            for (Field field: o.getClass().getDeclaredFields()){
                if (availableFieldInDatabase.contains(field.getName())) {
                    field.setAccessible(true);
                    ps.setObject(index, field.get(o));
                    index++;
                }
            }
        }
    }
    @SneakyThrows
    private static void process(Connection connection) {
        DataReadWriteSource<ResultSet> rw = new ConnectionReadWriteSource(connection, "person");
        List<Person> listFromDB = ORM.readAll(rw, Person.class);
        System.out.println(listFromDB);
    }
//        URL url = Main.class.getClassLoader().getResource("sample.xml");
//        Class<Person> person = Person.class;
//        String table = person.getAnnotation(TableName.class).name();
//
//        List<Person> resultSQL;
//        List<Person> resultFile;
//        FileReadWriteSource fileReadWriteSource = new FileReadWriteSource(new File(url.toURI()));
//        resultFile = ORM.readAll(fileReadWriteSource, Person.class);
//        DataReadWriteSource<ResultSet> rw = new ConnectionReadWriteSource(connection, table);
//        resultSQL = ORM.readAll(rw, Person.class);
//        ORM.writeAll(fileReadWriteSource, resultFile);


        // resultSQL.add(new Person("WRITE", BigInteger.ZERO, BigInteger.ZERO, "WRITE", LocalDate.now(), 0F));
        // ORM.writeAll(new ORMInterface.FileReadWriteSource(new File(url.toURI())), resultSQL);


//        resultSQL.add(new Person("WRITE", 0, 0, "WRITE", "date"));

        //ORM.writeAll(rw, resultSQL);



    @SneakyThrows
    private static void withConnection(Function<Connection, Void> function) {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:sample.db")) {
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS person " +
                        "(id INTEGER not NULL, " +
                        " name VARCHAR(255), " +
                        " position VARCHAR(255), " +
                        " age INTEGER, " +
                        " salary INTEGER, " +
                        " dateOfBirth DATE, " +
                        " PRIMARY KEY ( id ))");
//                stmt.executeUpdate("DELETE FROM person");
                for (int index = 0; index < 10; index++) {
                    stmt.executeUpdate("INSERT INTO person (name, position, age, dateOfBirth) VALUES ('1', '1', 2, '1992-10-10')");
                }
            }
            function.apply(c);
        }
    }
}