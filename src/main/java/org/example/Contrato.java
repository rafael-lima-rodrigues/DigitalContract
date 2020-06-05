package org.example;

import com.google.gson.Gson;

import java.util.List;

public class Contrato{
    
    private  String id;
    private String dados;
    private  String userIdOwner;

    private List<String> listUserMembers;

    public Contrato(String id, String dados, String userIdOwner) {
        this.id = id;
        this.dados = dados;
        this.userIdOwner = userIdOwner;
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

    public static boolean verificarUserId(String userId, String value){
        Gson gson = new Gson();
        Contrato contrato = gson.fromJson(value, Contrato.class);
        if (userId.equals(contrato.getUserIdOwner())){
            return true;
        }
        return false;
    }
}