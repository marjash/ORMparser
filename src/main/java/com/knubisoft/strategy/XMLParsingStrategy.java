package com.knubisoft.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.knubisoft.*;
import lombok.SneakyThrows;

import java.io.File;
import java.util.List;
import java.util.Map;

public class XMLParsingStrategy implements ParsingStrategy<FileReadWriteSource> {
    @SneakyThrows
    @Override
    public ORM.Table parseToTable(FileReadWriteSource content) {
        XmlMapper mapper = new XmlMapper();
        JsonNode tree = mapper.readTree(content.getContent()).get("person");
        Map<Integer, Map<String, String>> result = JSONParsingStrategy.buildTable(tree);
        return new ORM.Table(result);
    }

    @SneakyThrows
    public <T> void write(DataReadWriteSource<?> source, List<T> objects) {
        XmlMapper xmlMapper = new XmlMapper();
        File file = ((FileReadWriteSource) source).getSource();
        xmlMapper.writeValue(file, objects);
    }
}
