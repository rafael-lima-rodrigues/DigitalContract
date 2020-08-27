package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@DataType
public class User {
    @Property
    private String name;
    @Property
    private String dateOfBirth;
    @Property
    private String cpf;
    @Property
    private String sex;
    @Property
    private String civilState;


   /* public User(String name, Date dateOfBirth, String cpf, String sex, String civilState) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.cpf = cpf;
        this.sex = sex;
        this.civilState = civilState;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth){
        this.dateOfBirth = dateOfBirth;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
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

    public String toJSONString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);

    }

    public static User fromJSONString(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        user = mapper.readValue(json, User.class);

        return user;
    }
}
