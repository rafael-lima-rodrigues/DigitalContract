package org.example;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            try {
                String json = user.toJSONString();
                contract.createUserIdentity(context,"1000", user.toJSONString());
                verify(stub).putStringState("1000", json);
            } catch (IOException e) {
                e.printStackTrace();
            }

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


            try {
                String key = "10001";
                json = user.toJSONString();
                //when(stub.getStringState("10001")).thenReturn(json);
                when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));
                String returnedAsset = contract.readUserIdentity(context, "10001").toJSONString();
                assertEquals(returnedAsset, user.toJSONString());
                System.out.println(returnedAsset + " = " + user.toJSONString());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
