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
import org.hyperledger.fabric.protos.common.Common;
import org.hyperledger.fabric.protos.peer.ProposalResponsePackage;
import org.hyperledger.fabric.protos.peer.TransactionPackage;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.shim.ResponseUtils.newSuccessResponse;

@org.hyperledger.fabric.contract.annotation.Contract(name = "DocumentsContract",
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
public class DocumentsContract implements ContractInterface {
    public DocumentsContract() {
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
        DocumentsContract idContract = new DocumentsContract();

        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(documents);

        ctx.getStub().putState(digitalSignId,documentsSigned.toJSONString().getBytes());
    }

    @Transaction()
    public DocumentsSigned readDigitalSign(Context ctx, String digitalSignId) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }

        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));

        return documentsSigned;
    }

    @Transaction()
    public void updateDigitalSign(Context ctx, String digitalSignId, String newDocument) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));


       // if(!documentsSigned.getListUserMembers().containsKey(memberId)){
           // throw new RuntimeException("The user " + memberId + "is not a member");
      //  }else {
            DocumentsSigned documentToUpdate = DocumentsSigned.fromJSONString(newDocument);
            ctx.getStub().putState(digitalSignId, documentToUpdate.toJSONString().getBytes(UTF_8));
       // }
    }

    @Transaction()
    public void deleteDigitalSign(Context ctx, String digitalSignId) throws IOException {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(
                new String(ctx.getStub().getState(digitalSignId),UTF_8));

        ctx.getStub().delState(digitalSignId);
    }
    @Transaction
    public String queryDS(Context context, String query) throws IOException {
        List<DocumentsSigned> documentsSignedListList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Iterator<KeyValue> interator = context.getStub().getQueryResult(query).iterator();

        while (interator.hasNext()) {

            String key = interator.next().getKey();
            DocumentsSigned documentsSigned = null;
            documentsSigned = readDigitalSign(context, key);
            documentsSignedListList.add(documentsSigned);
        }
        try {
            return objectMapper.writeValueAsString(documentsSignedListList);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DocumentsSigned.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DocumentsContract.class.getName()).log(Level.SEVERE, null, ex);
        }

        Chaincode.Response response = newSuccessResponse("Query Sucessful", payload.getBytes(UTF_8));
        return response.getStringPayload();
    }
}
