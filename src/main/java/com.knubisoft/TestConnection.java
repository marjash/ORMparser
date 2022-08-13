package com.knubisoft;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestConnection {

    String url = "jdbc:mysql://localhost:3306/newdb";
    String userName = "root";
    String password = "root";
    String query = "select * from person";

    public List<Person> getResult() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<Person> person = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, userName, password)) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                    person.add(new Person(resultSet.getString(2),
                            Integer.parseInt(resultSet.getString(3)),
                            Integer.parseInt(resultSet.getString(4)),
                            resultSet.getString(5),
                            resultSet.getString(6)));
            }
        }
        return person;
    }
}
