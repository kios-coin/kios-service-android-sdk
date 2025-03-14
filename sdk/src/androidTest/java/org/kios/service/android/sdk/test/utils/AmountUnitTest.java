package org.kios.service.android.sdk.test.utils;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.math.BigInteger;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.kios.service.android.sdk.utils.Amount;

@RunWith(AndroidJUnit4.class)
public class AmountUnitTest {
    @Test
    public void make() {
        try {
            Amount amount = Amount.make("1");
            assertEquals(new BigInteger("1000000000000000000", 10), amount.getValue());
            assertEquals("1.000000000000000000", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }

        try {
            Amount amount = Amount.make("1.45");
            assertEquals(new BigInteger("1450000000000000000", 10), amount.getValue());
            assertEquals("1.450000000000000000", amount.toAmountString());
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.5678");
            assertEquals(new BigInteger("1234567800000000000000", 10), amount.getValue());
            assertEquals("1234.567800000000000000", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.05678");
            assertEquals(new BigInteger("1234056780000000000000", 10), amount.getValue());
            assertEquals("1234.056780000000000000", amount.toAmountString());
        } catch (Exception e) {
            assertEquals("some exception message...", e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.0000000000000005678");
            assertEquals(new BigInteger("1234000000000000000567", 10), amount.getValue());
            assertEquals("1234.000000000000000567", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }

        try {
            Amount amount = Amount.make("1234.0000000000000000005678");
            assertEquals(new BigInteger("1234000000000000000000", 10), amount.getValue());
            assertEquals("1234.000000000000000000", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }

        try {
            Amount amount = Amount.make("1_234.5678");
            assertEquals(new BigInteger("1234567800000000000000", 10), amount.getValue());
            assertEquals("1234.567800000000000000", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }

        try {
            Amount amount = Amount.make("1,234.5678");
            assertEquals(new BigInteger("1234567800000000000000", 10), amount.getValue());
            assertEquals("1234.567800000000000000", amount.toAmountString());
        } catch (Exception e) {
            fail("some exception message..." + e.getMessage());
        }
    }
}
