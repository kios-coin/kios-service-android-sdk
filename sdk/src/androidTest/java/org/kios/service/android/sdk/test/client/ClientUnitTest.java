package org.kios.service.android.sdk.test.client;

import org.kios.service.android.sdk.client.*;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.UserBalance;
import org.kios.service.android.sdk.utils.CommonUtils;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ClientUnitTest {

    @Test
    public void getChainId() throws Exception {
        Client client = new Client(NetWorkType.kios_testnet);
        try {
            Long response = client.getChainId().get(30, TimeUnit.SECONDS); // 타임아웃 30초 설정
            System.out.println("Chain ID Response: " + response);
            assertEquals(215125L, response.longValue());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void getBalancePhone() throws Exception {
        Client client = new Client(NetWorkType.kios_testnet);
        try {
            UserBalance balance = client.getBalancePhone("+82 10-1000-2099").get(30, TimeUnit.SECONDS); // 타임아웃 30초 설정
            assertEquals("5000000000000000000000000", balance.point.balance.toString());
            assertEquals("0", balance.token.balance.toString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void getBalancePhoneHash() throws Exception {
        Client client = new Client(NetWorkType.kios_testnet);
        try {
            UserBalance balance = client.getBalancePhoneHash("0x6e2f492102956a83a350152070be450b44fa19c08455c74b3aa79cc74195d3ba").get(30, TimeUnit.SECONDS);
            assertEquals("5000000000000000000000000", balance.point.balance.toString());
            assertEquals("0", balance.token.balance.toString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void getBalanceAccount() throws Exception {
        Client client = new Client(NetWorkType.kios_testnet);
        try {
            UserBalance balance = client.getBalanceAccount("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563").get(30, TimeUnit.SECONDS);
                    assertEquals("5000000000000000000000000", balance.point.balance.toString());
                    assertEquals("100000000000000000000000", balance.token.balance.toString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void getLedgerNonceOf() throws Exception {
        Client client = new Client(NetWorkType.kios_testnet);
        try {
            client.getLedgerNonceOf("0x20eB9941Df5b95b1b1AfAc1193c6a075B6191563").thenApply(nonce -> {
                try {
                    assertTrue(nonce >= 0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            });
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void getInternationalPhoneNumber() throws Exception {
        try {
            String res = CommonUtils.getInternationalPhoneNumber("+82 010 1000 2099");
            assertEquals("+82 10-1000-2099", res);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
}
