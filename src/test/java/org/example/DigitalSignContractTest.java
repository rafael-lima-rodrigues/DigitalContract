/*
 * SPDX-License-Identifier: Apache License 2.0
 */

package org.example;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


public final class DigitalSignContractTest {

    @Nested
    class AssetExists {
        @Test
        public void noProperAsset() {

            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(new byte[] {});
            boolean result = contract.digitalSignExists(ctx,"10001");

            assertFalse(result);
        }

        @Test
        public void assetExists() {

            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(new byte[] {42});
            boolean result = contract.digitalSignExists(ctx,"10001");

            assertTrue(result);

        }

        @Test
        public void noKey() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10002")).thenReturn(null);
            boolean result = contract.digitalSignExists(ctx,"10002");

            assertFalse(result);

        }

    }

    @Nested
    class AssetCreates {

        @Test
        public void newAssetCreate() throws IOException {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            DocumentsSigned documentsSigned = new DocumentsSigned();
            documentsSigned.setId("100");
            documentsSigned.setUserIdOwner("1234");
            documentsSigned.setDados("hello world");
                List<String> lista = new ArrayList<>();
            lista.add("user1"); lista.add("user2"); lista.add("user3");
            documentsSigned.setListUserMembers(lista);

            contract.createDigitalSign(ctx, "10001",
                    "{\"id\":100,\"dados\":\"hello world\"," +
                            "\"userIdOwner\":\"1234\"," +
                            "\"listUserMembers\":[\"user1\",\"user2\",\"user3\"]}");

            verify(stub).putState("10001", documentsSigned.toJSONString().getBytes(UTF_8));
        }

        @Test
        public void alreadyExists() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10002")).thenReturn(new byte[] { 42 });

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.createDigitalSign(ctx, "10002", "TheDigitalSign");
            });

            assertEquals(thrown.getMessage(), "The asset 10002 already exists");

        }

    }

    @Test
    public void assetRead() throws IOException {
        DigitalSignContract contract = new  DigitalSignContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        DocumentsSigned documentsSigned = new DocumentsSigned();
        documentsSigned.setId("100");
        documentsSigned.setUserIdOwner("user1");
        documentsSigned.setDados("hello world");
        List<String> lista = new ArrayList<>();
        lista.add("user1"); lista.add("user2"); lista.add("user3");
        documentsSigned.setListUserMembers(lista);

        String json = documentsSigned.toJSONString();
        when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));

        String returnedAsset = contract.readDigitalSign(ctx, "10001", "user1").toJSONString();
        assertEquals(returnedAsset, documentsSigned.toJSONString());
        System.out.println("Leitura do contrato: "+ returnedAsset);
    }

    @Nested
    class AssetUpdates {
        @Test
        public void updateExisting() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getState("10001")).thenReturn(new byte[] { 42 });

            DocumentsSigned documentsSigned = new DocumentsSigned();
            documentsSigned.setId("100");
            documentsSigned.setUserIdOwner("user1");
            documentsSigned.setDados("hello world");
            List<String> lista = new ArrayList<>();
            lista.add("user1"); lista.add("user2"); lista.add("user3");
            documentsSigned.setListUserMembers(lista);

            try {
                contract.updateDigitalSign(ctx,"10001", "11100", documentsSigned.toJSONString());
                String json = documentsSigned.toJSONString();
                verify(stub).putState("10001", json.getBytes(UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Test
        public void updateMissing() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            DocumentsSigned documentsSigned = new DocumentsSigned();
            documentsSigned.setId("100");
            documentsSigned.setUserIdOwner("user1");
            documentsSigned.setDados("hello world");
            List<String> lista = new ArrayList<>();
            lista.add("user1"); lista.add("user2"); lista.add("user3");
            documentsSigned.setListUserMembers(lista);

            when(stub.getState("10001")).thenReturn(null);

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.updateDigitalSign(ctx, "10001", "100", documentsSigned.toJSONString());
            });
            assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
        }
    }

    @Test
    public void assetDelete() {
        DigitalSignContract contract = new  DigitalSignContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getState("10001")).thenReturn(null);

        Exception thrown = assertThrows(RuntimeException.class, () -> {
            contract.deleteDigitalSign(ctx,"100", "10001");
        });

        assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
    }

}
