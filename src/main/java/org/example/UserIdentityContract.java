/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import com.google.gson.Gson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;

import java.io.IOException;

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
     *
     */

    @Transaction()
    public void createUserIdentity(Context ctx, String userSignId, String userData) throws IOException {
        boolean exists = userIdentityExists(ctx,userSignId);
        if (exists) {
            throw new RuntimeException("The asset "+userSignId+" already exists");
        }
        User user = User.fromJSONString(userData);
        ctx.getStub().putStringState(userSignId, user.toJSONString());
    }


    @Transaction()
    public User readUserIdentity(Context ctx, String userId) throws IOException {
        boolean exists = userIdentityExists(ctx,userId);
        if (!exists) {
            throw new RuntimeException("The asset "+ userId +" does not exist");
        }
        User user = User.fromJSONString(new String(ctx.getStub().getStringState(userId)));
        return user;
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


}
