package com.knubisoft;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {
    private static final ORMInterface ORM = new ORM();

    @SneakyThrows
    @Test
    public void ormTest(){
        URL urlJson = Main.class.getClassLoader().getResource("sample.json");
        URL urlXml = Main.class.getClassLoader().getResource("sample.xml");
//        URL urlCsv = Main.class.getClassLoader().getResource("sample.csv");

        List<Person> result;
        result = ORM.readAll(new FileReadWriteSource(new File(urlJson.toURI())), Person.class);
//        result.add(new Person("OLeh", 25, 1500, "junior", "2022-10-10"));
        ORM.writeAll(new FileReadWriteSource(new File("src/main/resources/sample2.json")), result);

        List<Person> result2;
        result2 = ORM.readAll(new FileReadWriteSource(new File(urlXml.toURI())), Person.class);
        result2.add(new Person("OLeh", 25, 1500, "junior", "2022-10-10"));
        ORM.writeAll(new FileReadWriteSource(new File("src/main/resources/sample2.xml")), result2);
        URL urlJson2 = Main.class.getClassLoader().getResource("sample2.json");
        assertEquals(IOUtils.toString(urlJson, StandardCharsets.UTF_8).length(), IOUtils.toString(urlJson2, StandardCharsets.UTF_8).length());
    }
}