/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import com.google.gson.Gson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.example.Contrato.verificarUserId;

@Contract(name = "DigitalSignContract",
    info = @Info(title = "DigitalSign contract",
                description = "My Smart Contract",
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
    public void createDigitalSign(Context ctx, String digitalSignId, String value) {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (exists) {
            throw new RuntimeException("The asset "+digitalSignId+" already exists");
        }
        Gson gson = new Gson();
        Contrato contrato = gson.fromJson(value, Contrato.class);
        DigitalSign asset = new DigitalSign();
        asset.setValue(value);
        ctx.getStub().putState(digitalSignId, asset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public DigitalSign readDigitalSign(Context ctx, String digitalSignId, String userId) {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DigitalSign newAsset = DigitalSign.fromJSONString(new String(ctx.getStub().getState(digitalSignId),UTF_8));
        boolean userExists = verificarUserId(userId,newAsset.getValue());
        if(!userExists){
            throw new RuntimeException("The user " + userId + "has not permission");
        }
        //Gson gson = new Gson();
        //Contrato contrato = gson.fromJson(newAsset.getValue(), Contrato.class);
        //contrato.getId();
        return newAsset;
    }

    @Transaction()
    public void updateDigitalSign(Context ctx, String digitalSignId, String newContrato) {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DigitalSign asset = new DigitalSign();
        asset.setValue(newContrato);

        ctx.getStub().putState(digitalSignId, asset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public void deleteDigitalSign(Context ctx, String digitalSignId) {
        boolean exists = digitalSignExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        ctx.getStub().delState(digitalSignId);
    }

}
