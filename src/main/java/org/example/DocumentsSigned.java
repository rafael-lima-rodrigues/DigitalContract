package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DataType
public class DocumentsSigned {
    @Property
    private String id;
    @Property
    private String descricao;
    @Property
    private String dados;
    @Property
    private String userIdOwner;
    @Property
    private final String typeDoc = "DocsCreated";
    @Property
    private Map<String, Boolean> sign = new HashMap<>();

    public DocumentsSigned() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDados() {
        return dados;
    }

    public void setDados(String dados) {
        this.dados = dados;
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

    public Map<String, Boolean> getSign() {
        return sign;
    }

    public void setSign(Map<String, Boolean> sign) {
        this.sign = sign;
    }

    public String toJSONString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);

    }

    public static DocumentsSigned fromJSONString(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DocumentsSigned documentsSigned;
        documentsSigned = mapper.readValue(json, DocumentsSigned.class);

        return documentsSigned;
    }

    public static boolean verifyMemberId(String memberId, List memberslist) {
        //DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(value);
        if (memberslist.contains(memberId)) {
            return true;
        }
        return false;
    }

    public static boolean verifyIsOwner(String memberId, String ownerId) {
        //DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(value);
        if (ownerId.equals(memberId)) {
            return true;
        }
        return false;
    }
}