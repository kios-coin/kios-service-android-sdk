package org.kios.service.android.sdk.test.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kios.service.android.sdk.client.ProviderClient;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.UserBalance;
import org.kios.service.android.sdk.utils.Amount;
import org.web3j.abi.datatypes.Address;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProviderClientUnitTest {
    private static ProviderClient providerClient = new ProviderClient(NetWorkType.kios_testnet, "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c");
    private static ProviderClient agentClient = new ProviderClient(NetWorkType.kios_testnet, "0x44868157d6d3524beb64c6ae41ee6c879d03c19a357dadb038fefea30e23cbab");

    @Test
    public void test01_IsProvider() throws Exception {
        try {
            boolean value = providerClient.isProvider(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            assertTrue(value);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test02_GetBalanceAccount() throws Exception {
        try {
            UserBalance value = providerClient.getBalanceAccount(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            assertTrue(value.point.balance.compareTo(BigInteger.ZERO) > 0);
            assertTrue(value.token.balance.compareTo(BigInteger.ZERO) > 0);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test03_GetBalancePhone() throws Exception {
        try {
            UserBalance value = providerClient.getBalancePhone("+82 10-1000-2000").get(30, TimeUnit.SECONDS);
            assertTrue(value.point.balance.compareTo(BigInteger.ZERO) > 0);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test04_ClearAgent() throws Exception {
        try {
            providerClient.setAgent(Address.DEFAULT.toString()).get(30, TimeUnit.SECONDS);
            assertEquals(providerClient.getAgent().get(30, TimeUnit.SECONDS), Address.DEFAULT.toString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test05_ProviderToAddress() throws Exception {
        try {
            System.out.println("[ ProviderToAddress ]");
            Boolean isProvider = providerClient.isProvider(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            if (isProvider) {
                String receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
                UserBalance res1 = providerClient.getBalanceAccount(receiver).get(30, TimeUnit.SECONDS);
                BigInteger oldBalance = res1.point.balance;

                BigInteger amount = Amount.make("100").getValue();
                providerClient.provideToAddress(providerClient.getAddress(), receiver, amount).get(30, TimeUnit.SECONDS);
                UserBalance res2 = providerClient.getBalanceAccount(receiver).get(30, TimeUnit.SECONDS);

                assertEquals(res2.point.balance, oldBalance.add(amount));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test06_ProviderToPhone() throws Exception {
        try {
            System.out.println("[ ProviderToPhone ]");
            Boolean isProvider = providerClient.isProvider(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            if (isProvider) {
                String phoneNumber = "+82 10-9000-5000";
                UserBalance res1 = providerClient.getBalancePhone(phoneNumber).get(30, TimeUnit.SECONDS);
                BigInteger oldBalance = res1.point.balance;

                BigInteger amount = Amount.make("100").getValue();
                providerClient.provideToPhone(providerClient.getAddress(), phoneNumber, amount).get(30, TimeUnit.SECONDS);
                UserBalance res2 = providerClient.getBalancePhone(phoneNumber).get(30, TimeUnit.SECONDS);

                assertEquals(res2.point.balance, oldBalance.add(amount));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test07_SetAgent() throws Exception {
        try {
            providerClient.setAgent(agentClient.getAddress()).get(30, TimeUnit.SECONDS);
            assertEquals(providerClient.getAgent().get(30, TimeUnit.SECONDS).toLowerCase(), agentClient.getAddress().toLowerCase());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test08_ProviderToAddressByAgent() throws Exception {
        try {
            Boolean isProvider = providerClient.isProvider(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            if (isProvider) {
                String receiver = "0xB6f69F0e9e70034ba0578C542476cC13eF739269";
                UserBalance res1 = providerClient.getBalanceAccount(receiver).get(30, TimeUnit.SECONDS);
                BigInteger oldBalance = res1.point.balance;

                BigInteger amount = Amount.make("100").getValue();
                agentClient.provideToAddress(providerClient.getAddress(), receiver, amount).get(30, TimeUnit.SECONDS);
                UserBalance res2 = providerClient.getBalanceAccount(receiver).get(30, TimeUnit.SECONDS);

                assertEquals(res2.point.balance, oldBalance.add(amount));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test09_ProviderToPhoneByAgent() throws Exception {
        try {
            Boolean isProvider = providerClient.isProvider(providerClient.getAddress()).get(30, TimeUnit.SECONDS);
            if (isProvider) {
                String phoneNumber = "+82 10-9000-5000";
                UserBalance res1 = providerClient.getBalancePhone(phoneNumber).get(30, TimeUnit.SECONDS);
                BigInteger oldBalance = res1.point.balance;

                BigInteger amount = Amount.make("100").getValue();
                agentClient.provideToPhone(providerClient.getAddress(), phoneNumber, amount).get(30, TimeUnit.SECONDS);
                UserBalance res2 = providerClient.getBalancePhone(phoneNumber).get(30, TimeUnit.SECONDS);

                assertEquals(res2.point.balance, oldBalance.add(amount));
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
}