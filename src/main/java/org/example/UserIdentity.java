package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.io.IOException;

@DataType
public class UserIdentity {

    @Property
    private String id;
    @Property
    private String name;
    @Property
    private String password;
    @Property
    private String cpf;
    @Property
    private final String typeDoc = "usersDoc";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTypeDoc() {
        return typeDoc;
    }


    public String toJSONString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);

    }

    public static UserIdentity fromJSONString(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        UserIdentity userIdentity = null;
        userIdentity = mapper.readValue(json, UserIdentity.class);

        return userIdentity;
    }
}
