package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;


public final class UserUdentityTest {

    @Nested
    public class CreateUser{
        @Test
        public void newUserIdentity(){
            UserIdentityContract contract = new UserIdentityContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);

            User user = new User();
            user.setName("rafael");
            user.setCpf("123456");
            user.setCivilState("solteiro");
            user.setSex("M");
            user.setDateOfBirth("25-02-1991");

            String json = null;
            try {
                json = user.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                contract.createUserIdentity(context,"10001", user.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            verify(stub).putStringState("10001", json);


        }

        @Test
        public void updateObjMissing() {
            UserIdentityContract contract = new UserIdentityContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
           // when(stub.getState("1000")).thenReturn(new byte[] { 42 });

            //when(stub.getState("10001")).thenReturn(null);
            String json = "newUserData";
            Exception thrown = assertThrows(IOException.class, () -> {
                contract.createUserIdentity(ctx, "1000",json );
            });

            assertEquals(thrown.getMessage(), "Unrecognized token 'newUserData': was expecting \n" +
                    " at [Source: java.io.StringReader@7d6f7bf3; line: 1, column: 23]");
        }

        @Test
        public void readUserIdentity(){
            UserIdentityContract contract = new UserIdentityContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);

            User user = new User();
            user.setName("rafael");
            user.setCpf("123456");
            user.setCivilState("solteiro");
            user.setSex("M");

            user.setDateOfBirth("25-02-1991");
            String json = null;
            String key = "10001";
            try {
                json = user.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //when(stub.getStringState("10001")).thenReturn(json);
            when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));
            String returnedAsset = null;
            try {
                returnedAsset = contract.readUserIdentity(context, "10001").toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assertEquals(returnedAsset, user.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                System.out.println(returnedAsset + " = " + user.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Nested
    public class UpdateUserIdentity{

        @Test
        public void updateExisting(){

            UserIdentityContract contract = new UserIdentityContract();
            Context context = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(context.getStub()).thenReturn(stub);
            when(stub.getState("10001")).thenReturn(new byte[] { 42 });

            User user = new User();
            user.setName("rafael");
            user.setCpf("123456");
            user.setCivilState("casado");
            user.setSex("M");
            user.setDateOfBirth("25-02-1991");

            String json = null;
            try {
                json = user.toJSONString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                contract.updateUserIdentity(context,"10001", user.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            verify(stub).putState("10001", json.getBytes(UTF_8));


        }

        @Test
        public void updateMissing() {
            UserIdentityContract contract = new UserIdentityContract();
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
        public void updateObjMissing() {
            UserIdentityContract contract = new UserIdentityContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getState("1000")).thenReturn(new byte[] { 42 });

            //when(stub.getState("10001")).thenReturn(null);
            String json = "newUserData";
            Exception thrown = assertThrows(IOException.class, () -> {
                contract.updateUserIdentity(ctx, "1000",json );
            });

            assertEquals(thrown.getMessage(), "Unrecognized token 'newUserData': was expecting \n" +
                    " at [Source: java.io.StringReader@395731bc; line: 1, column: 23]");
        }

        @Test
        public void assetDelete() {
            UserIdentityContract contract = new UserIdentityContract();
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
