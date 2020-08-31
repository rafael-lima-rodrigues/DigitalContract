/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.DocumentsSigned.verifyIsOwner;
import static org.example.DocumentsSigned.verifyMemberId;


@org.hyperledger.fabric.contract.annotation.Contract(name = "DigitalSignContract",
    info = @Info(title = "DigitalSign contract",
                description = "My Smart DocumentsSigned",
                version = "0.0.1",
                license =
                        @License(name = "Apache-2.0",
                                url = ""),
                                contact =  @Contact(email = "Tcc@example.com",
                                                name = "Tcc",
                                                url = "http://Tcc.me")))
@Default
public class DigitalSignContract implements ContractInterface {
    public  DigitalSignContract() {

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
        DocumentsSigned documentsSigned = DocumentsSigned.fromJSONString(documents);
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

        boolean userPermission = verifyMemberId(userId, documentsSigned.getListUserMembers());
        if(!userPermission){
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

        boolean userPermission = verifyMemberId(userId, documentsSigned.getListUserMembers());
        if(!userPermission){
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

        boolean userPermission = verifyIsOwner(userId,documentsSigned.getUserIdOwner());
        if(!userPermission){
            throw new RuntimeException("The user " + userId + "has not permission");
        }
        ctx.getStub().delState(digitalSignId);
    }

}
