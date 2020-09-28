/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.DocumentsSigned.verifyIsOwner;
import static org.example.DocumentsSigned.verifyMemberId;
import static org.hyperledger.fabric.shim.ResponseUtils.newSuccessResponse;

@org.hyperledger.fabric.contract.annotation.Contract(name = "UserIdentityContract",
        info = @Info(title = "UserIdentity contract",
                description = "My Smart Contract2",
                version = "0.0.1",
                license =
                @License(name = "Apache-2.0",
                        url = ""),
                contact = @Contact(email = "Tcc@example.com",
                        name = "Tcc",
                        url = "http://Tcc.me")))
@Default
public class UserIdentityContract implements ContractInterface {
    public UserIdentityContract() {
    }

    /**
     * O metodo returna true se userSignId passado como parametro existe
     * e false caso nao exista
     *
     * @param ctx
     * @param userSignId
     * @return
     */
    @Transaction()
    public boolean userIdentityExists(Context ctx, String userSignId) {
        byte[] buffer = ctx.getStub().getState(userSignId);
        return (buffer != null && buffer.length > 0);
    }

    /**
     * metodo criada uma identidade de usuario para assinatura dos documentos.
     * Caso ja exista a identidade ja exista o metodo returna uma Exception.
     *
     * @param ctx
     * @param userSignId
     * @param userData
     */

    @Transaction()
    public void createUserIdentity(Context ctx, String userSignId, String userData) throws IOException {
        boolean exists = userIdentityExists(ctx, userSignId);
        if (exists) {
            throw new RuntimeException("The asset " + userSignId + " already exists");
        }
        UserIdentity userIdentity = UserIdentity.fromJSONString(userData);
        // ctx.getStub().putState(userSignId, userIdentity.toJSONString().getBytes(UTF_8));

        ctx.getStub().putStringState(userSignId, userIdentity.toJSONString());
    }

    /**
     * O metodo returna uma identidade de usuario a partir de uma key id
     *
     * @param ctx
     * @param userId
     * @return UserIdentity
     * @throws IOException
     */

    @Transaction()
    public UserIdentity readUserIdentity(Context ctx, String userId) throws IOException {
        boolean exists = userIdentityExists(ctx, userId);
        if (!exists) {
            throw new RuntimeException("The asset " + userId + " does not exist");
        }
        //UserIdentity userIdentity = UserIdentity.fromJSONString(new String(ctx.getStub().getStringState(userId));
        UserIdentity userIdentity = UserIdentity.fromJSONString(new String(ctx.getStub().getState(userId), UTF_8));
        return userIdentity;
    }

    @Transaction()
    public void updateUserIdentity(Context ctx, String userSignId, String newValue) throws IOException {
        boolean exists = userIdentityExists(ctx, userSignId);
        if (!exists) {
            throw new RuntimeException("The asset " + userSignId + " does not exist");
        }
        UserIdentity userIdentity = UserIdentity.fromJSONString(newValue);

        ctx.getStub().putState(userSignId, userIdentity.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public void deleteUserIdentity(Context ctx, String digitalSignId) {
        boolean exists = userIdentityExists(ctx, digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset " + digitalSignId + " does not exist");
        }
        ctx.getStub().delState(digitalSignId);
    }

    @Transaction
    public String queryUserID(Context context, String query) throws IOException {
        List<UserIdentity> userList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<KeyValue> interator = context.getStub().getQueryResult(query).iterator();
        while (interator.hasNext()) {

            String key = interator.next().getKey();
            UserIdentity identity = null;
            identity = readUserIdentity(context, key);
            userList.add(identity);
        }
        try {
            return objectMapper.writeValueAsString(userList);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(UserIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";

    }

    @Transaction
    public String getUserHistory(Context context, String key) {
        String payload = "";
        List<Map<String, Object>> historylist = new ArrayList<>();

        Iterator<KeyModification> iterator = context.getStub().getHistoryForKey(key).iterator();
        if (!iterator.hasNext()) {
            return "[]";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule());
        while (iterator.hasNext()) {
            HashMap<String, Object> history = new HashMap<>();
            KeyModification modification = iterator.next();
            history.put("asset", modification.getStringValue());
            history.put("transactionId", modification.getTxId());
            history.put("timeStamp", modification.getTimestamp());
            historylist.add(history);

        }
        try {
            payload = objectMapper.writeValueAsString(historylist);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(UserIdentityContract.class.getName()).log(Level.SEVERE, null, ex);
        }

        Chaincode.Response response = newSuccessResponse("Query Sucessful", payload.getBytes(UTF_8));
        return response.getStringPayload();
    }

    @Transaction()
    public boolean digitalSignExists(Context ctx, String digitalSignId) {
        byte[] buffer = ctx.getStub().getState(digitalSignId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public void createDigitalSign(Context ctx, String digitalSignId, String documents) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (exists) {
            throw new RuntimeException("The asset "+digitalSignId+" already exists");
        }
        UserIdentityContract idContract = new UserIdentityContract();

        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(documents);

        boolean userExists = idContract.userIdentityExists(ctx,documentsSigned.getUserIdOwner());
        if (!userExists){
            throw new RemoteException("User: " + documentsSigned.getUserIdOwner() + " doesn't exists");
        }
        ctx.getStub().putState(digitalSignId,documentsSigned.toJSONString().getBytes());
    }

    @Transaction()
    public DocumentsSigned readDigitalSign(Context ctx, String digitalSignId, String userId) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }

        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));

       // boolean userPermission = verifyMemberId(userId, documentsSigned.getListUserMembers());
        if(!documentsSigned.getListUserMembers().contains(userId)){
            throw new RuntimeException("The user " + userId + "has not permission");
        }

        return documentsSigned;
    }

    @Transaction()
    public void updateDigitalSign(Context ctx, String digitalSignId, String userId, String newDocument) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));

       // boolean userPermission = verifyMemberId(userId, documentsSigned.getListUserMembers());
        if(!documentsSigned.getUserIdOwner().equals(userId)){
            throw new RuntimeException("The user " + userId + "has not permission");
        }else {
            DocumentsSigned documentToUpdate = DocumentsSigned.fromJSONString(newDocument);
            ctx.getStub().putState(digitalSignId, documentToUpdate.toJSONString().getBytes(UTF_8));
        }
    }

    @Transaction()
    public void deleteDigitalSign(Context ctx, String userId, String digitalSignId) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));

       // boolean userPermission = verifyIsOwner(userId,documentsSigned.getUserIdOwner());
        if(!documentsSigned.getUserIdOwner().equals(userId)){
            throw new RuntimeException("The user " + userId + "has not permission");
        }
        ctx.getStub().delState(digitalSignId);
    }
    @Transaction
    public String queryDS(Context context, String query, String userId) throws IOException {
        List<DocumentsSigned> userList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<KeyValue> interator = context.getStub().getQueryResult(query).iterator();
        while (interator.hasNext()) {

            String key = interator.next().getKey();
            DocumentsSigned documentsSigned = null;
            documentsSigned = readDigitalSign(context, key, userId);
            userList.add(documentsSigned);
        }
        try {
            return objectMapper.writeValueAsString(userList);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(UserIdentity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";

    }

    @Transaction
    public String getDSHistory(Context context, String key) {
        String payload = "";
        List<Map<String, Object>> historylist = new ArrayList<>();

        Iterator<KeyModification> iterator = context.getStub().getHistoryForKey(key).iterator();
        if (!iterator.hasNext()) {
            return "[]";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(new JavaTimeModule());
        while (iterator.hasNext()) {
            HashMap<String, Object> history = new HashMap<>();
            KeyModification modification = iterator.next();
            history.put("asset", modification.getStringValue());
            history.put("transactionId", modification.getTxId());
            history.put("timeStamp", modification.getTimestamp());
            historylist.add(history);

        }
        try {
            payload = objectMapper.writeValueAsString(historylist);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(UserIdentityContract.class.getName()).log(Level.SEVERE, null, ex);
        }

        Chaincode.Response response = newSuccessResponse("Query Sucessful", payload.getBytes(UTF_8));
        return response.getStringPayload();
    }
}
