package org.example;

import java.util.Date;

public class User {

    private String name;
    private final Date  dateOfBirth;
    private final String cpf;
    private String sex;
    private String civilState;


    public User(String name, Date dateOfBirth, String cpf, String sex, String civilState) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.cpf = cpf;
        this.sex = sex;
        this.civilState = civilState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCivilState() {
        return civilState;
    }

    public void setCivilState(String civilState) {
        this.civilState = civilState;
    }
}
