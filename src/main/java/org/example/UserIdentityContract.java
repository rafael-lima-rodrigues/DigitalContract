/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import com.google.gson.Gson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;

import static java.nio.charset.StandardCharsets.UTF_8;

@Contract(name = "UserIdentityContract",
    info = @Info(title = "UserIdentity contract",
                description = "My Smart Contract2",
                version = "0.0.1",
                license =
                        @License(name = "Apache-2.0",
                                url = ""),
                                contact =  @Contact(email = "Tcc@example.com",
                                                name = "Tcc",
                                                url = "http://Tcc.me")))
@Default
public class UserIdentityContract implements ContractInterface {
    public UserIdentityContract() {

    }
    @Transaction()
    public boolean userIdentityExists(Context ctx, String digitalSignId) {
        byte[] buffer = ctx.getStub().getState(digitalSignId);
        return (buffer != null && buffer.length > 0);
    }

    @Transaction()
    public void createUserIdentity(Context ctx, String digitalSignId, String value) {
        boolean exists = userIdentityExists(ctx,digitalSignId);
        if (exists) {
            throw new RuntimeException("The asset "+digitalSignId+" already exists");
        }
        DigitalSign asset = new DigitalSign();
        asset.setValue(value);
        ctx.getStub().putState(digitalSignId, asset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public DigitalSign readUserIdentity(Context ctx, String digitalSignId, String userId) {
        boolean exists = userIdentityExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DigitalSign newAsset = DigitalSign.fromJSONString(new String(ctx.getStub().getState(digitalSignId),UTF_8));
        return newAsset;
    }

    @Transaction()
    public void updateUserIdentity(Context ctx, String digitalSignId, String newValue) {
        boolean exists = userIdentityExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        DigitalSign asset = new DigitalSign();
        asset.setValue(newValue);

        ctx.getStub().putState(digitalSignId, asset.toJSONString().getBytes(UTF_8));
    }

    @Transaction()
    public void deleteUserIdentity(Context ctx, String digitalSignId) {
        boolean exists = userIdentityExists(ctx,digitalSignId);
        if (!exists) {
            throw new RuntimeException("The asset "+digitalSignId+" does not exist");
        }
        ctx.getStub().delState(digitalSignId);
    }
    public boolean verificarUserId(String userId, String value){
        Gson gson = new Gson();
        Contrato contrato = gson.fromJson(value, Contrato.class);
        if (userId.equals(contrato.getUserid())){
            return true;
        }
        return false;
    }

}
