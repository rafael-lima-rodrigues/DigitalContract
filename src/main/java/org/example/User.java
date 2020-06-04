package org.example;

import java.util.Date;

public class User {

    private String name;
    private Date  dateOfBirth;
    private final String cpf;


    public User(String name, Date dateOfBirth, String cpf) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.cpf = cpf;
    }
}
