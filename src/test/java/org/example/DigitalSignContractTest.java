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

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
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
        public void newAssetCreate() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            String json = "{\"value\":\"{id:001, dados:teste, userIdOwner:100}\"}";

            contract.createDigitalSign(ctx, "10001", "{id:001, dados:teste, userIdOwner:100}");

            verify(stub).putState("10001", json.getBytes(UTF_8));
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
    public void assetRead() {
        DigitalSignContract contract = new  DigitalSignContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        DigitalSign asset = new  DigitalSign();
        asset.setValue("{id:001, dados:teste, userIdOwner:100}");
        //Gson gson = new Gson();
        //Contrato contrato = new Contrato();
        //contrato = gson.fromJson(asset.getValue(), Contrato.class);
        //System.out.println(contrato.getDados());

        String json = asset.toJSONString();
        when(stub.getState("10001")).thenReturn(json.getBytes(StandardCharsets.UTF_8));

        String returnedAsset = contract.readDigitalSign(ctx, "10001", "100").toJSONString();
        assertEquals(returnedAsset, "{\"value\":\"{id:001, dados:teste, userIdOwner:100}\"}");
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

            contract.updateDigitalSign(ctx, "10001", "updates");

            String json = "{\"value\":\"updates\"}";
            verify(stub).putState("10001", json.getBytes(UTF_8));
        }

        @Test
        public void updateMissing() {
            DigitalSignContract contract = new  DigitalSignContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);

            when(stub.getState("10001")).thenReturn(null);

            Exception thrown = assertThrows(RuntimeException.class, () -> {
                contract.updateDigitalSign(ctx, "10001", "TheDigitalSign");
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
            contract.deleteDigitalSign(ctx, "10001");
        });

        assertEquals(thrown.getMessage(), "The asset 10001 does not exist");
    }

}
