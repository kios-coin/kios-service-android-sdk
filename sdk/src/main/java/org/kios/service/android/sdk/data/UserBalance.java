package org.kios.service.android.sdk.data;

import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Balance data for points and tokens
 */
public class UserBalance {
    /**
     * Balance of Point
     */
    public Balance point;
    /**
     * Balance of Token
     */
    public Balance token;

    /**
     * Constructor
     * @param point Balance of Point
     * @param token Balance of Token
     */
    public UserBalance (JSONObject point, JSONObject token) throws Exception {
        this.point = new Balance(new BigInteger(point.getString("balance"), 10), new BigInteger(point.getString("value"), 10));
        this.token = new Balance(new BigInteger(token.getString("balance"), 10), new BigInteger(token.getString("value"), 10));
    }
}
