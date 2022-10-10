package com.knubisoft;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;


public class Main {
    private static final ORMInterface ORM = new ORM();

    public static void main(String[] args) throws Exception {
        withConnection(connection -> {
            process(connection);
            return null;
        });
    }

    @SneakyThrows
    private static void process(Connection connection) {
        URL urlJson = Main.class.getClassLoader().getResource("sample.json");
        URL urlXml = Main.class.getClassLoader().getResource("sample.xml");
        URL urlCsv = Main.class.getClassLoader().getResource("sample.csv");

        List<Person> result;
        result = ORM.readAll(new FileReadWriteSource(new File(urlJson.toURI())), Person.class);
        result.add(new Person("OLeh", 25, 1500, "junior", "2022-10-10"));
        ORM.writeAll(new FileReadWriteSource(new File("src/main/resources/sample2.json")), result);


//            DataReadWriteSource<ResultSet> rw = new ConnectionReadWriteSource(connection, "person");
//            result = ORM.readAll(rw, Person.class);
//            result.add(new Person("OLeh", 25, 1500, "junior", "2022-10-10"));
//            ORM.writeAll(rw, result);
    }


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
                        " dateOfBirth VARCHAR(50), " +
                        " PRIMARY KEY ( id ))");
                for (int index = 0; index < 10; index++) {
                    stmt.executeUpdate("INSERT INTO person (name, position, age, dateOfBirth) VALUES ('1', '1', 2, '1992-10-10')");
                }
            }
            function.apply(c);
        }
    }
}