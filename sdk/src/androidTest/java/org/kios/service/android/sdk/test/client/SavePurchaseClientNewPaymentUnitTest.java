package org.kios.service.android.sdk.test.client;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.kios.service.android.sdk.client.SavePurchaseClient;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.UserBalance;
import org.kios.service.android.sdk.data.purchase.PurchaseDetail;
import org.kios.service.android.sdk.data.purchase.ResponseSavePurchase;
import org.kios.service.android.sdk.utils.Amount;
import org.kios.service.android.sdk.utils.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SavePurchaseClientNewPaymentUnitTest {
    private static Map<NetWorkType, String> keysOfCollector;
    private static Map<NetWorkType, String> addressOfAsset;

    //---------------------------------------------------------------------------------------
    // 키오스크의 상점 아이디
    private static String shopId = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";

    // 테스트를 할 네트워크, 상점아이디에 따른 네트워크를 선택한다
    private static NetWorkType network = CommonUtils.getNetWorkType(shopId);

    // 키오스크에 표시되었던 환률 심벌
    private static String currency = CommonUtils.getDefaultCurrencySymbol(network);

    // 구매 아이디
    private static String purchaseId;

    // 구매 발생 timestamp
    private static long timestamp;

    // 사용자앱에서 키오스크로 전달받은 지갑주소 (처음에는 임시주소이나, 서버에 정보요청 후 정상주소로 변환해야 함)
    // 입력되지 않았다면 ""
    private static String userAccount;

    // 사용자가 전화번호를 입력했을 때 사용되며, 입력되지 않았다면 ""
    private static String userPhone;

    // 전체 결제 금액
    private static String totalAmount;

    // 포인트 사용금액
    private static String cacheAmount;

    // 포인트 지급까지의 대기시간, 단위는 초이다.
    // 0이면 블록생성과 기타 작업등으로 인해 테스트넷은 10초내외 메인넷은 30초 정도 후에 포인트가 제공된다.
    private static long waiting;

    private static SavePurchaseClient savePurchaseClient;

    private static UserBalance balance1;
    private static UserBalance balance2;
    private static UserBalance balance3;


    @BeforeClass  // 각 테스트 메서드 실행 전에 실행됨
    public static void setupClass() {
        // 구매정보 저장을 위해 필요한 키
        // 메인넷의 키는 담당자에게 직접요청하여야 함
        //---------------------------------------------------------------------------------------
        keysOfCollector = new HashMap<>();
        keysOfCollector.put(NetWorkType.kios_testnet, "0xa0dcffca22f13363ab5d109f3a51ca99754cff4ce4c71dccc0c5df7f6492beee");
        keysOfCollector.put(NetWorkType.acc_testnet, "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276");
        //---------------------------------------------------------------------------------------

        // 포인트를 자산을 소유한 주소
        //---------------------------------------------------------------------------------------
        addressOfAsset = new HashMap<>();
        addressOfAsset.put(NetWorkType.acc_testnet, "0x85EeBb1289c0d0C17eFCbadB40AeF0a1c3b46714");
        addressOfAsset.put(NetWorkType.acc_mainnet, "0xCB2e8ebBF4013164161d7F2297be25d4A9dC6b17");
        addressOfAsset.put(NetWorkType.kios_testnet, "0x153f2340807370855092D04E0e0abe4f2b634240");
        addressOfAsset.put(NetWorkType.kios_mainnet, "0xf077c9CfFa387E35de72b68448ceD5382CbC5D7D");
        //---------------------------------------------------------------------------------------

        //---------------------------------------------------------------------------------------
        // 키오스크의 상점 아이디
        shopId = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";

        // 테스트를 할 네트워크, 상점아이디에 따른 네트워크를 선택한다
        network = CommonUtils.getNetWorkType(shopId);

        // 키오스크에 표시되었던 환률 심벌
        currency = CommonUtils.getDefaultCurrencySymbol(network);

        // 구매 아이디
        purchaseId = CommonUtils.getSamplePurchaseId();

        // 구매 발생 timestamp
        timestamp = CommonUtils.getTimeStamp();

        // 사용자앱에서 키오스크로 전달받은 지갑주소 (처음에는 임시주소이나, 서버에 정보요청 후 정상주소로 변환해야 함)
        // 입력되지 않았다면 ""
        userAccount = "0x64D111eA9763c93a003cef491941A011B8df5a49";

        // 사용자가 전화번호를 입력했을 때 사용되며, 입력되지 않았다면 ""
        userPhone = "";

        // 전체 결제 금액
        totalAmount = "10000";

        // 포인트 사용금액
        cacheAmount = "10000";

        // 포인트 지급까지의 대기시간, 단위는 초이다.
        // 0이면 블록생성과 기타 작업등으로 인해 테스트넷은 10초내외 메인넷은 30초 정도 후에 포인트가 제공된다.
        waiting = 0;
        //---------------------------------------------------------------------------------------


        // 구매데이터를 전송하는 클라이언트를 생성한다
        //---------------------------------------------------------------------------------------
        savePurchaseClient = new SavePurchaseClient(network, keysOfCollector.get(network), addressOfAsset.get(network));
        //---------------------------------------------------------------------------------------
    }

    @Test
    public void test01_CheckBalance() throws Exception {
        try {
            // 초기 잔고를 화인한다. 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
            //---------------------------------------------------------------------------------------
            balance1 = savePurchaseClient.getBalanceAccount(userAccount).get(30, TimeUnit.SECONDS);
            //---------------------------------------------------------------------------------------
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test02_SaveNewPurchase() throws Exception {
        try {
            // 신규 결제에 대한 구매 데이터 전송 입니다.
            //---------------------------------------------------------------------------------------
            ResponseSavePurchase res1 = savePurchaseClient.saveNewPurchase(
                    purchaseId,
                    timestamp,
                    waiting,
                    totalAmount,
                    cacheAmount,
                    currency,
                    shopId,
                    userAccount,
                    userPhone,
                    new PurchaseDetail[]{new PurchaseDetail("2020051310000000", "10000", 10)}
            ).get(30, TimeUnit.SECONDS);
            System.out.printf("  - type: %d, sequence: %s, purchaseId: %s\n", res1.type, res1.sequence, res1.purchaseId);
            //---------------------------------------------------------------------------------------
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test03_Waiting() throws Exception {
        try {
            // 구매정보가 전송된 후 포인트가 지급될 때 까지 대기합니다.
            //---------------------------------------------------------------------------------------
            System.out.println("[ Waiting for providing... ]");
            long t1 = CommonUtils.getTimeStamp();
            while (true) {
                UserBalance balance2 = savePurchaseClient.getBalanceAccount(userAccount).get(30, TimeUnit.SECONDS);
                if (balance2.point.balance.equals(balance1.point.balance.add(Amount.make("1000").getValue()))) {
                    break;
                } else if (CommonUtils.getTimeStamp() - t1 > 120) {
                    System.out.println("Time out for providing... ");
                    break;
                }
                Thread.sleep(1000);
            }
            //---------------------------------------------------------------------------------------
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void test04_CheckBalance() throws Exception {
        try {
            // 이전 잔고와 비교하여 증가된것을 확인 할 수 있습니다.
            //---------------------------------------------------------------------------------------
            System.out.println("[ Check Balance ]");
            UserBalance balance3 = savePurchaseClient.getBalanceAccount(userAccount).get(30, TimeUnit.SECONDS);
            assertEquals(balance3.point.balance, balance1.point.balance.add(Amount.make("1000").getValue()));
            System.out.printf("  - Balance: %s\n", new Amount(balance3.point.balance).toAmountString());
            //---------------------------------------------------------------------------------------
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
}
