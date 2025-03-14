package org.kios.service.android.sdk.data.settlement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigInteger;

public class ShopRefundableData {
    public BigInteger refundableAmount;
    public BigInteger refundableToken;

    public ShopRefundableData(
            BigInteger refundableAmount,
            BigInteger refundableToken
    ) {
        this.refundableAmount = refundableAmount;
        this.refundableToken = refundableToken;
    }

    @NotNull
    @Contract("_ -> new")
    public static ShopRefundableData fromJSONObject(@NotNull JSONObject data) throws Exception {
        return new ShopRefundableData(
                new BigInteger(data.getString("refundableAmount"), 10),
                new BigInteger(data.getString("refundableToken"), 10)
        );
    }

}
