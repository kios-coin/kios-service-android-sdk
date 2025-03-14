package org.kios.service.android.sdk.client;

import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.UserBalance;
import org.kios.service.android.sdk.data.settlement.ChainInfo;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The client class of decentralized loyalty services
 */
public class Client {
    /**
     * The endpoint of the relay API server
     */
    protected final String relayEndpoint;
    /**
     * The endpoint of the save purchase API server
     */
    protected final String saveEndpoint;
    /**
     * The Chain ID of side chain
     */
    protected int chainId;

    /**
     * Constructor
     * @param network Type of network (acc_mainnet, acc_testnet, kios_testnet, kios_mainnet, localhost)
     */
    public Client(NetWorkType network) {
        if (network == NetWorkType.localhost) {
            relayEndpoint = "http://127.0.0.1:7070";
            saveEndpoint = "http://127.0.0.1:3030";
        } else if (network == NetWorkType.acc_mainnet) {
            relayEndpoint = "https://relay.main.acccoin.io";
            saveEndpoint = "https://save.main.acccoin.io";
        } else if (network == NetWorkType.acc_testnet) {
            relayEndpoint = "https://relay.test.acccoin.io";
            saveEndpoint = "https://save.test.acccoin.io";
        } else if (network == NetWorkType.kios_mainnet) {
            relayEndpoint = "https://relay.main.kioscoin.io";
            saveEndpoint = "https://save.main.kioscoin.io";
        } else if (network == NetWorkType.kios_testnet) {
            relayEndpoint = "https://relay.test.kioscoin.io";
            saveEndpoint = "https://save.test.kioscoin.io";
        } else {
            relayEndpoint = "https://relay.main.kioscoin.io";
            saveEndpoint = "https://save.main.kioscoin.io";
        }
        chainId = 0;

    }

    /**
     * Translate HTTP response data into JSON objects and deliver
     * @param conn HttpURLConnection
     * @return JSON objects
     * @throws Exception Error during HTTP communication
     */
    @NotNull
    protected static JSONObject getResponse(@NotNull HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();

        try {
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

                String inputLine;
                StringBuilder sb = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                br.close();

                JSONObject jObject = new JSONObject(sb.toString());
                int code = jObject.getInt("code");
                if (code != 0) {
                    String errorMessage;
                    try {
                        JSONObject error = jObject.getJSONObject("error");
                        errorMessage = String.format("%s (%d)", error.getString("message"), jObject.getInt("code"));
                    } catch (Exception e) {
                        errorMessage = String.format("%d", jObject.getInt("code"));
                    }
                    throw new Exception("Internal Error : " + errorMessage);
                }
                return jObject;
            } else {
                BufferedReader err = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();

                while ((errorLine = err.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                err.close();

                String errorBody = errorResponse.toString();
                System.err.println("Error Response: " + errorBody);

                throw new Exception("HTTP error code : " + responseCode + "\nResponse: " + errorBody);
            }
        } catch (Exception e) {
            System.err.println("Error in getResponse: " + e.getMessage());
            throw e;
        } finally {
            conn.disconnect();
        }
    }

    /**
     * The data inside the JSON object that was responded is extracted as an object.
     * @param conn HttpURLConnection
     * @return JSON objects
     * @throws Exception Error during HTTP communication
     */
    protected static JSONObject getJSONObjectResponse(@NotNull HttpURLConnection conn) throws Exception {
        return getResponse(conn).getJSONObject("data");
    }

    /**
     * The data inside the JSON object that was responded is extracted as an array.
     * @param conn HttpURLConnection
     * @return JSON objects
     * @throws Exception Error during HTTP communication
     */
    protected static JSONArray getJSONArrayResponse(@NotNull HttpURLConnection conn) throws Exception {
        return getResponse(conn).getJSONArray("data");
    }

    /**
     * Create an HTTP connection
     * @param url URL
     * @param method GET or POST
     * @return HttpURLConnection
     * @throws Exception Exception while creating HTTP connection
     */
    protected HttpURLConnection getHttpURLConnection(@NotNull URL url, String method) throws Exception  {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return conn;
    }

    /**
     * Provide the ID of the chain
     * @return chain ID
     *
     * @throws Exception Error during HTTP communication
     */
    public CompletableFuture<Long> getChainId() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (chainId != 0) {
                    return Long.valueOf(chainId);
                }
                URL url = new URL(relayEndpoint + "/v1/chain/side/id");
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                chainId = data.getInt("chainId");
                return Long.valueOf(chainId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * Provide the user's points and token balance information
     * @param phoneNumber User's phone number
     * @return UserBalance
     *
     * @throws Exception Error during HTTP communication
     */
    public CompletableFuture<UserBalance> getBalancePhone(@NotNull String phoneNumber) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/ledger/balance/phone/" + phoneNumber.trim().replace(" ", "%20"));
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Provide the user's points and token balance information
     * @param phoneHash User's phone number hash
     * @return UserBalance
     *
     * @throws Exception Error during HTTP communication
     */
    public CompletableFuture<UserBalance> getBalancePhoneHash(@NotNull String phoneHash) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/ledger/balance/phoneHash/" + phoneHash.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Provide the user's points and token balance information
     * @param account User's wallet address
     * @return UserBalance
     *
     * @throws Exception Error durlng HTTP communication
     */
    public CompletableFuture<UserBalance> getBalanceAccount(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/ledger/balance/account/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return new UserBalance(data.getJSONObject("point"), data.getJSONObject("token"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Provide a nonce corresponding to the user's wallet address. It provides a nonce corresponding to the user's wallet address.
     * This ensures that the same signature is not repeated. And this value is recorded in Contract and automatically increases by 1.
     * @param account User's wallet address
     * @return the nonce
     *
     * @throws Exception Error durlng HTTP communication
     */
    public CompletableFuture<Long> getLedgerNonceOf(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/ledger/nonce/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return Long.valueOf(data.getInt("nonce"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ChainInfo _mainChainInfo;
    private ChainInfo _sideChainInfo;

    public CompletableFuture<Long> getShopNonceOf(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/shop/nonce/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return Long.valueOf(data.getInt("nonce"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<ChainInfo> getChainInfoOfMainChain() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (_mainChainInfo != null) return _mainChainInfo;
                URL url = new URL(relayEndpoint + "/v1/chain/main/info/");
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                _mainChainInfo = ChainInfo.fromJSONObject(data);
                return _mainChainInfo;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Long> getChainIdOfMainChain() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChainInfo info = getChainInfoOfMainChain().get(30, TimeUnit.SECONDS);
                return info.network.chainId;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Long> getNonceOfMainChainToken(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/token/main/nonce/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return Long.valueOf(data.getInt("nonce"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<BigInteger> getBalanceOfMainChainToken(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/token/main/balance/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return new BigInteger(data.getString("balance"), 10);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<ChainInfo> getChainInfoOfSideChain() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (_sideChainInfo != null) return _sideChainInfo;
                URL url = new URL(relayEndpoint + "/v1/chain/side/info/");
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                _sideChainInfo = ChainInfo.fromJSONObject(data);
                return _sideChainInfo;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Long> getChainIdOfSideChain() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChainInfo info = getChainInfoOfSideChain().get(30, TimeUnit.SECONDS);
                return info.network.chainId;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Long> getNonceOfSideChainToken(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/token/side/nonce/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return Long.valueOf(data.getInt("nonce"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<BigInteger> getBalanceOfSideChainToken(@NotNull String account) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(relayEndpoint + "/v1/token/side/balance/" + account.trim());
                HttpURLConnection conn = getHttpURLConnection(url, "GET");
                JSONObject data = getJSONObjectResponse(conn);
                return new BigInteger(data.getString("balance"), 10);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
