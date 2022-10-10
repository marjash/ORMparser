package com.knubisoft.strategy;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.knubisoft.DataReadWriteSource;
import com.knubisoft.FileReadWriteSource;
import com.knubisoft.ORM;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONParsingStrategy implements ParsingStrategy<FileReadWriteSource> {

    @SneakyThrows
    @Override
    public ORM.Table parseToTable(FileReadWriteSource content) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(content.getContent());
        Map<Integer, Map<String, String>> result = buildTable(tree);
        return new ORM.Table(result);
    }

    public static Map<Integer, Map<String, String>> buildTable(JsonNode tree) {
        Map<Integer, Map<String, String>> map = new LinkedHashMap<>();
        int index = 0;
        for (JsonNode each : tree) {
            Map<String, String> item = buildRow(each);
            map.put(index, item);
            index++;
        }
        return map;
    }

    private static Map<String, String> buildRow(JsonNode each) {
        Map<String, String> item = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> itr = each.fields();
        while (itr.hasNext()) {
            Map.Entry<String, JsonNode> next = itr.next();
            item.put(next.getKey(), next.getValue().textValue());
        }
        return item;
    }

    @SneakyThrows
    public <T> void write(DataReadWriteSource<?> source, List<T> objects) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode jsonNode = mapper.valueToTree(objects);
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        File file = ((FileReadWriteSource) source).getSource();
        writer.writeValue(file, jsonNode);
    }
}

