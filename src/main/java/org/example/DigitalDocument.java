package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@DataType
public class DigitalDocument {
    @Property
    private String id;
    @Property
    private String description;
    @Property
    private String data;
    @Property
    private String userIdOwner;
    @Property
    private final String typeDoc = "DocsCreated";
    @Property
    private Map<String, Boolean> signature = new HashMap<>();

    public DigitalDocument() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserIdOwner() {
        return userIdOwner;
    }

    public void setUserIdOwner(String userIdOwner) {
        this.userIdOwner = userIdOwner;
    }

    public String getTypeDoc() {
        return typeDoc;
    }

    public Map<String, Boolean> getSignature() {
        return signature;
    }

    public void setSignature(Map<String, Boolean> signature) {
        this.signature = signature;
    }

    public String toJSONString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public static DigitalDocument fromJSONString(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DigitalDocument digitalDocument;
        digitalDocument = mapper.readValue(json, DigitalDocument.class);

        return digitalDocument;
    }

}