package org.kios.service.android.sdk.data.settlement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.math.BigInteger;

public class ChainNetwork {
    public String name;
    public long chainId;
    public String ensAddress;
    public BigInteger chainTransferFee;
    public BigInteger chainBridgeFee;
    public BigInteger loyaltyTransferFee;
    public BigInteger loyaltyBridgeFee;


    public ChainNetwork(
            String name,
            long chainId,
            String ensAddress,
            BigInteger chainTransferFee,
            BigInteger chainBridgeFee,
            BigInteger loyaltyTransferFee,
            BigInteger loyaltyBridgeFee
    ) {
        this.name = name;
        this.chainId = chainId;
        this.ensAddress = ensAddress;
        this.chainTransferFee = chainTransferFee;
        this.chainBridgeFee = chainBridgeFee;
        this.loyaltyTransferFee = loyaltyTransferFee;
        this.loyaltyBridgeFee = loyaltyBridgeFee;
    }


    @NotNull
    @Contract("_ -> new")
    public static ChainNetwork fromJSONObject(JSONObject data) throws Exception {
        return new ChainNetwork(
                data.getString("name"),
                data.getInt("chainId"),
                data.getString("ensAddress"),
                new BigInteger(data.getString("chainTransferFee"), 10),
                new BigInteger(data.getString("chainBridgeFee"), 10),
                new BigInteger(data.getString("loyaltyTransferFee"), 10),
                new BigInteger(data.getString("loyaltyBridgeFee"), 10)
        );
    }

    public ChainNetwork cloneTaskItem() {
        return new ChainNetwork(
                this.name,
                this.chainId,
                this.ensAddress,
                this.chainTransferFee,
                this.chainBridgeFee,
                this.loyaltyTransferFee,
                this.loyaltyBridgeFee
        );
    }
}