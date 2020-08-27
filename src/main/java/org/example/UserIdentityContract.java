/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public UserIdentityContract() {}

    /**
     * O metodo returna true se userSignId passado como parametro existe
     * e false caso nao exista
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
     *
     */

    @Transaction()
    public void createUserIdentity(Context ctx, String userSignId, String userData) throws IOException {
        boolean exists = userIdentityExists(ctx,userSignId);
        if (exists) {
            throw new RuntimeException("The asset "+userSignId+" already exists");
        }
        User user = User.fromJSONString(userData);
       // ctx.getStub().putState(userSignId, user.toJSONString().getBytes(UTF_8));

        ctx.getStub().putStringState(userSignId, user.toJSONString());
    }

    /**
     * O metedo returna uma identidade de usuario a partir de uma key id
     *
     * @param ctx
     * @param userId
     * @return User
     * @throws IOException
     */

    @Transaction()
    public User readUserIdentity(Context ctx, String userId) throws IOException {
        boolean exists = userIdentityExists(ctx,userId);
        if (!exists) {
            throw new RuntimeException("The asset "+ userId +" does not exist");
        }
        //User user = User.fromJSONString(new String(ctx.getStub().getStringState(userId));
        User user = User.fromJSONString(new String(ctx.getStub().getState(userId),UTF_8));
        return user;
    }

    @Transaction()
    public void updateUserIdentity(Context ctx, String userSignId, String newValue) throws IOException {
        boolean exists = userIdentityExists(ctx,userSignId);
        if (!exists) {
            throw new RuntimeException("The asset " + userSignId + " does not exist");
        }
        User user = User.fromJSONString(newValue);

        ctx.getStub().putState(userSignId, user.toJSONString().getBytes(UTF_8));
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
