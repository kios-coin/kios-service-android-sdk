package org.kios.service.android.sdk.client;

import org.json.JSONArray;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.settlement.ChainInfo;
import org.kios.service.android.sdk.data.settlement.ShopData;
import org.kios.service.android.sdk.data.settlement.ShopRefundableData;
import org.kios.service.android.sdk.utils.CommonUtils;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SettlementClient extends Client {
    /**
     * Message repeater's wallet for providing
     */
    protected final Credentials credentials;

    protected String shopId;

    public SettlementClient(NetWorkType network, String privateKey, String shopId) {
        super(network);
        this.credentials = Credentials.create(ECKeyPair.create(new BigInteger(Numeric.cleanHexPrefix(privateKey), 16)));
        this.shopId = shopId;
    }

    public String getAddress() {
        return this.credentials.getAddress();
    }

    public String getShopId() {
        return this.shopId;
    }

    public CompletableFuture<Long> getSettlementClientLength() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/shop/settlement/client/length/" + getShopId());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return Long.valueOf(data.getInt("length"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<ArrayList<String>> getSettlementClientList(long startIndex, long endIndex) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/shop/settlement/client/list/" + getShopId() + "?startIndex=" + startIndex + "&endIndex=" + endIndex);
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                JSONArray clients = data.getJSONArray("clients");
                ArrayList<String> clientList = new ArrayList<String>();
                for (int i = 0; i < clients.length(); i++) {
                    clientList.add(clients.getString(i));
                }
                return clientList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> collectSettlementAmountMultiClient(ArrayList<String> clientShopIdList) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] message = CommonUtils.getCollectSettlementAmountMultiClientMessage(
                        getShopId(),
                        clientShopIdList,
                        getShopNonceOf(getAddress()).get(),
                        getChainId().get()
                );
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/shop/settlement/collect", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("shopId", getShopId());
                body.put("account", getAddress());
                body.put("clients", String.join(",", clientShopIdList));
                body.put("signature", signature);

                try (OutputStream output = conn.getOutputStream()) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("txHash");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<ShopData> getShopInfo() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(String.format("%s/v1/shop/info/%s", relayEndpoint, getShopId()));
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return ShopData.fromJSONObject(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> getAccountOfShopOwner() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ShopData info = getShopInfo().get(30, TimeUnit.SECONDS);
                return info.account;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<ShopRefundableData> getRefundable() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(String.format("%s/v1/shop/refundable/%s", relayEndpoint, getShopId()));
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return ShopRefundableData.fromJSONObject(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> refund(BigInteger amount) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                BigInteger adjustedAmount = CommonUtils.zeroGWEI(amount);
                long nonce = this.getShopNonceOf(this.credentials.getAddress()).get();
                long chainId = this.getChainId().get();
                byte[] message = CommonUtils.getShopRefundMessage(getShopId(), adjustedAmount, nonce, chainId);
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/shop/refund", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("shopId", getShopId());
                body.put("account", getAddress());
                body.put("amount", adjustedAmount.toString());
                body.put("signature", signature);

                try (OutputStream output = conn.getOutputStream()) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("txHash");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> withdraw(BigInteger amount) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChainInfo chainInfo = getChainInfoOfSideChain().get();
                BigInteger adjustedAmount = CommonUtils.zeroGWEI(amount);
                long expiry = CommonUtils.getTimeStamp() + 1800;
                long nonce = this.getLedgerNonceOf(getAddress()).get();
                byte[] message = CommonUtils.getTransferMessage(
                        chainInfo.network.chainId,
                        chainInfo.contract.token,
                        getAddress(),
                        chainInfo.contract.loyaltyBridge,
                        adjustedAmount,
                        nonce,
                        expiry);
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/ledger/withdraw_via_bridge", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("account", getAddress());
                body.put("amount", adjustedAmount.toString());
                body.put("expiry", expiry);
                body.put("signature", signature);

                try (OutputStream output = conn.getOutputStream()) {
                    output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("txHash");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
