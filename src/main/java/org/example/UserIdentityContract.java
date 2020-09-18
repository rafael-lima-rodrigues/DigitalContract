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
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
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
    public String query(Context context, String query) throws IOException {
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
}
