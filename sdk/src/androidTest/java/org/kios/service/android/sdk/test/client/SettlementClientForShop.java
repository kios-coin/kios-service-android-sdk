package org.kios.service.android.sdk.test.client;


import org.json.JSONObject;
import org.kios.service.android.sdk.client.SettlementClient;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.utils.CommonUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SettlementClientForShop extends SettlementClient {
    protected String shopId;

    public SettlementClientForShop(NetWorkType network, String privateKey, String shopId) {
        super(network, privateKey, shopId);

        this.shopId = shopId;
    }

    public CompletableFuture<String> getSettlementManager() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/shop/settlement/manager/get/" + getShopId());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("managerId");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> setSettlementManager(String managerId) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] message = CommonUtils.getSetSettlementManagerMessage(
                        getShopId(),
                        managerId,
                        getShopNonceOf(getAddress()).get(30, TimeUnit.SECONDS),
                        getChainId().get(30, TimeUnit.SECONDS)
                );
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/shop/settlement/manager/set", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("shopId", getShopId());
                body.put("account", getAddress());
                body.put("managerId", managerId);
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

    public CompletableFuture<String> removeSettlementManager() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] message = CommonUtils.getRemoveSettlementManagerMessage(
                        getShopId(),
                        this.getShopNonceOf(getAddress()).get(30, TimeUnit.SECONDS),
                        this.getChainId().get(30, TimeUnit.SECONDS)
                );
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/shop/settlement/manager/remove", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("shopId", getShopId());
                body.put("account", getAddress());
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

    public CompletableFuture<String> getAgentOfRefund() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/agent/refund/" + getShopId());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("agent");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> setAgentOfRefund(String agent) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] message = CommonUtils.getRegisterAgentMessage(
                        getAddress(),
                        agent,
                        this.getLedgerNonceOf(getAddress()).get(30, TimeUnit.SECONDS),
                        this.getChainId().get(30, TimeUnit.SECONDS)
                );
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/agent/refund", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("account", getAddress());
                body.put("agent", agent);
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

    public CompletableFuture<String> getAgentOfWithdrawal() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/agent/withdrawal/" + getShopId());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return data.getString("agent");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> setAgentOfWithdrawal(String agent) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] message = CommonUtils.getRegisterAgentMessage(
                        getAddress(),
                        agent,
                        this.getLedgerNonceOf(getAddress()).get(30, TimeUnit.SECONDS),
                        this.getChainId().get(30, TimeUnit.SECONDS)
                );
                String signature = CommonUtils.signMessage(this.credentials.getEcKeyPair(), message);

                URL url = new URL(String.format("%s/v1/agent/withdrawal", relayEndpoint));
                HttpURLConnection conn = getHttpURLConnection(url, "POST");

                JSONObject body = new JSONObject();
                body.put("account", getAddress());
                body.put("agent", agent);
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
