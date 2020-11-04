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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.shim.ResponseUtils.newSuccessResponse;

@org.hyperledger.fabric.contract.annotation.Contract(name = "DocumentsContract",
        info = @Info(title = "UserIdentity contract",
                description = "My Smart Contract",
                version = "0.0.1",
                license =
                @License(name = "Apache-2.0",
                        url = ""),
                contact = @Contact(email = "Tcc@example.com",
                        name = "Tcc",
                        url = "http://Tcc.me")))
@Default
public class DocumentsContract implements ContractInterface {
    public DocumentsContract() {
    }

    @Transaction()
    public boolean digitalSignExists(Context ctx, String digitalSignId) {
        byte[] buffer = ctx.getStub().getState(digitalSignId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public void createDigitalDoc(Context ctx, String digitalDocId, String digitalDocArgs) throws IOException {
        boolean exists = digitalSignExists(ctx, digitalDocId);
        if (exists) {
            throw new RuntimeException("The asset " + digitalDocId + " already exists");
        }

        DigitalDocument digitalDocument = DigitalDocument.fromJSONString(digitalDocArgs);
        ctx.getStub().putState(digitalDocId, digitalDocument.toJSONString().getBytes());
    }

    @Transaction()
    public DigitalDocument findDigitalDocById(Context ctx, String digitalDocId) throws IOException {
        boolean exists = digitalSignExists(ctx, digitalDocId);
        if (!exists) {
            throw new RuntimeException("The asset " + digitalDocId + " does not exist");
        }

        DigitalDocument digitalDocument = DigitalDocument.fromJSONString(
                new String(ctx.getStub().getState(digitalDocId), UTF_8));

        return digitalDocument;
    }

    @Transaction()
    public void updateDigitalDoc(Context ctx, String digitalDocId, String digitalDocArgs) throws IOException {
        boolean exists = digitalSignExists(ctx, digitalDocId);

        if (!exists) {
            throw new RuntimeException("The asset " + digitalDocId + " does not exist");
        }

        DigitalDocument _digitalDocArgs = DigitalDocument.fromJSONString(digitalDocArgs);
        ctx.getStub().putState(digitalDocId, _digitalDocArgs.toJSONString().getBytes(UTF_8));

    }

    @Transaction()
    public void deleteDigitalDoc(Context ctx, String digitalDocId) throws IOException {
        boolean exists = digitalSignExists(ctx, digitalDocId);
        if (!exists) {
            throw new RuntimeException("The asset " + digitalDocId + " does not exist");
        }

        ctx.getStub().delState(digitalDocId);
    }

    @Transaction
    public String queryDigitalDoc(Context context, String query) throws IOException {
        List<DigitalDocument> digitalDocumentList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<KeyValue> interator = context.getStub().getQueryResult(query).iterator();

        while (interator.hasNext()) {

            String key = interator.next().getKey();
            DigitalDocument digitalDocument = null;
            digitalDocument = findDigitalDocById(context, key);
            digitalDocumentList.add(digitalDocument);
        }
        try {
            return objectMapper.writeValueAsString(digitalDocumentList);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DigitalDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[]";

    }

    @Transaction
    public String getHistoryDigitalDoc(Context context, String key) {
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
            Logger.getLogger(DocumentsContract.class.getName()).log(Level.SEVERE, null, ex);
        }

        Chaincode.Response response = newSuccessResponse("Query Sucessful", payload.getBytes(UTF_8));
        return response.getStringPayload();
    }
}
