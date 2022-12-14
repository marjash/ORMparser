package com.knubisoft;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TableName(name = "person")
public class Person {

    private String name;
    private int age;
    private int salary;
    private String position;
    private String dateOfBirth;
}
