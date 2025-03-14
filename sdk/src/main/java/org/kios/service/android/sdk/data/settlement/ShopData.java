package org.kios.service.android.sdk.data.settlement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigInteger;

public class ShopData {
    public String shopId;
    public String name;
    public String currency;
    public String account;
    public String delegator;
    public BigInteger providedAmount;
    public BigInteger usedAmount;
    public BigInteger collectedAmount;
    public BigInteger refundedAmount;
    public BigInteger settledAmount;
    public int status;

    public ShopData(
            String shopId,
            String name,
            String currency,
            String account,
            String delegator,
            BigInteger providedAmount,
            BigInteger usedAmount,
            BigInteger collectedAmount,
            BigInteger refundedAmount,
            int status
    ) {
        this.shopId = shopId;
        this.name = name;
        this.currency = currency;
        this.account = account;
        this.delegator = delegator;
        this.providedAmount = providedAmount;
        this.usedAmount = usedAmount;
        this.collectedAmount = collectedAmount;
        this.refundedAmount = refundedAmount;
        this.status = status;
        this.settledAmount =
                collectedAmount.add(usedAmount).compareTo(providedAmount) > 0
                        ? collectedAmount.add(usedAmount).subtract(providedAmount)
                        : BigInteger.ZERO;
    }

    @NotNull
    @Contract("_ -> new")
    public static ShopData fromJSONObject(@NotNull JSONObject data) throws Exception {
        return new ShopData(
                data.getString("shopId"),
                data.getString("name"),
                data.getString("currency"),
                data.getString("account"),
                data.getString("delegator"),
                new BigInteger(data.getString("providedAmount"), 10),
                new BigInteger(data.getString("usedAmount"), 10),
                new BigInteger(data.getString("collectedAmount"), 10),
                new BigInteger(data.getString("refundedAmount"), 10),
                data.getInt("status")
        );
    }

}
