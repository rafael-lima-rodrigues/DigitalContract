package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@DataType
public class DocumentsSigned {
    @Property
    private String id;
    @Property
    private String dados;
    @Property
    private String userIdOwner;
    @Property
    private final String typeDoc = "DocsCreated";
    @Property
    private List<String> listUserMembers = new ArrayList<>();

    public DocumentsSigned() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getListUserMembers() {
        return listUserMembers;
    }

    public void setListUserMembers(List<String> listUserMembers) {
        this.listUserMembers = listUserMembers;
    }

    public String getId() {
        return id;
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