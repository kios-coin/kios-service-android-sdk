package org.kios.service.android.sdk.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kios.service.android.sdk.data.ClientKey;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.Security;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.kios.service.android.sdk.utils.CommonUtils;

@RunWith(AndroidJUnit4.class)
public class CommonUtilsUnitTest {
    static {
        Security.removeProvider("BC"); // 기존에 등록된 BC 제거 (중복 방지)
        Security.addProvider(new BouncyCastleProvider()); // BC Provider 추가
    }

    @Test
    public void getRegisterAgentMessage() {
        try {
            byte[] message = CommonUtils.getRegisterAgentMessage(
                    "0x64D111eA9763c93a003cef491941A011B8df5a49",
                    "0x3FE8D00143bd0eAd2397D48ba0E31E5E1268dBfb",
                    45,
                    215115);
            assertEquals("0xd89c684325d02709927db1d839a58f05aa54a8ed21ec4da84fee82427e8286e6", Numeric.toHexString(message));
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }
    }

    @Test
    public void signMessage() {
        try {
            ECKeyPair keyPair = ECKeyPair.create(new BigInteger("70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c", 16));
            byte[] message = CommonUtils.getRegisterAgentMessage(
                    "0x64D111eA9763c93a003cef491941A011B8df5a49",
                    "0x3FE8D00143bd0eAd2397D48ba0E31E5E1268dBfb",
                    45,
                    215115);

            String signature = CommonUtils.signMessage(keyPair, message);
            assertEquals("0x16c3db108967fef995b7e6a9439338af06886c568653ee849f5c6511ede9faa2351cd6c89f53f2d400810e07ab8fafeb5d5c19c4b917837b94de2a445ea518281c", signature);
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }
    }

    @Test
    public void GetPhoneHash() {
        try {
            String message = CommonUtils.getPhoneHash("+82 10-9000-5000");

            assertEquals("0x8f01f960fa3bacb03c4217e254a031bd005b1685002a1826141a90f1692ca2c4", message);
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }
    }

    @Test
    public void CreateRandomKey() {
        try {
            ClientKey clientKey = CommonUtils.createRandomKey();
            System.out.printf("ClientKey.address: %s\n", clientKey.address);
            System.out.printf("ClientKey.privateKey: %s\n", clientKey.privateKey);
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }
    }
}
