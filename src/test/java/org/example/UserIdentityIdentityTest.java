package org.example;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;


public final class UserIdentityIdentityTest {

    @Nested
    public class CreateUserIdentity {
        @Test
        public void newUserIdentity(){
            DocumentsContract contract = new DocumentsContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);

            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setName("rafael");
            userIdentity.setCpf("123456");
            userIdentity.setPassword("solteiro");

            String json = null;
            try {
                json = userIdentity.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                contract.createUserIdentity(context,"10001", userIdentity.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            verify(stub).putStringState("10001", json);


        }

        @Test
        public void readUserIdentity(){
            DocumentsContract contract = new DocumentsContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);

            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setName("rafael");
            userIdentity.setPassword("123456");
            userIdentity.setCpf("12313213");


            String json = null;
            String key = "10001";
            try {
                json = userIdentity.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //when(stub.getStringState("10001")).thenReturn(json);
            when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));
            String returnedAsset = null;
            try {
                returnedAsset = contract.readUserIdentity(context, "10001").toJSONString();
                assertEquals(returnedAsset, userIdentity.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Nested
    public class UpdateUserIdentityIdentity {

        @Test
        public void updateExisting(){

            DocumentsContract contract = new DocumentsContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);
            when(stub.getState("10001")).thenReturn(new byte[] { 42 });

            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setName("rafael");
            userIdentity.setCpf("123456");
            userIdentity.setPassword("casado");


            String json = null;
            try {
                json = userIdentity.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                contract.updateUserIdentity(context,"10001", userIdentity.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            verify(stub).putState("10001", json.getBytes(UTF_8));


        }

        @Test
        public void updateMissing() {
            DocumentsContract contract = new DocumentsContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(null);

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.updateUserIdentity(ctx, "10001", "newUserData");
            });

            assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
        }

        @Test
        public void assetDelete() {
            DocumentsContract contract = new DocumentsContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getState("10001")).thenReturn(null);

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.deleteUserIdentity(ctx, "10001");
            });

            assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
        }
    }
}
