package org.kios.service.android.sdk.test.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kios.service.android.sdk.client.PaymentClient;
import org.kios.service.android.sdk.data.NetWorkType;
import org.kios.service.android.sdk.data.payment.PaymentInfo;
import org.kios.service.android.sdk.data.payment.PaymentTaskItem;
import org.kios.service.android.sdk.data.payment.ShopTaskItem;
import org.kios.service.android.sdk.event.ITaskEventListener;
import org.kios.service.android.sdk.event.TaskEventCollector;
import org.kios.service.android.sdk.utils.Amount;
import org.kios.service.android.sdk.utils.CommonUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentClientUnitTest {
    private static Map<NetWorkType, String> keysOfPayment;
    private static String userPrivateKey;
    private static String shopPrivateKey;
    private static String shopId;
    private static NetWorkType network;
    private static String terminalID;
    private static BigInteger amount;
    private static String currency;

    private static PaymentClient client;
    private static TaskEventListener listener;
    private static TaskEventCollector collector;
    private static PaymentClientForUser userClient;
    private static PaymentClientForShop shopClient;
    private static String temporaryAccount;
    private static PaymentInfo info;
    private static PaymentTaskItem paymentItem;

    @BeforeClass  // 각 테스트 메서드 실행 전에 실행됨
    public static void setupClass() {
        // 포인트를 사용하여 구매를 하기 위해 필요한 키 네트워크 별로 가지고 있어야 한다
        // 메인넷의 키는 담당자에게 직접요청하여야 함
        //---------------------------------------------------------------------------------------
        keysOfPayment = new HashMap<>();
        keysOfPayment.put(NetWorkType.kios_testnet, "0xa0dcffca22f13363ab5d109f3a51ca99754cff4ce4c71dccc0c5df7f6492beee");
        keysOfPayment.put(NetWorkType.kios_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000");
        keysOfPayment.put(NetWorkType.acc_testnet, "0x8acceea5937a8e4bb07abc93a1374264dd9bd2fc384c979717936efe63367276");
        keysOfPayment.put(NetWorkType.acc_mainnet, "0x0000000000000000000000000000000000000000000000000000000000000000");
        //---------------------------------------------------------------------------------------

        // 사용자앱의 정보
        // 이것은 사용자 모바일앱을 대신해서 테스트 코드에서 신규 결제 승인을 하기 위해 필요한 것임
        // 키오스크 서버에는 구현할 필요 없음
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        //---------------------------------------------------------------------------------------
        // 사용자 앱의 지갑의 비밀키
        userPrivateKey = "0x70438bc3ed02b5e4b76d496625cb7c06d6b7bf4362295b16fdfe91a046d4586c";
        //---------------------------------------------------------------------------------------

        // 상점앱의 정보
        // 이것은 사용자 모바일앱을 대신해서 테스트 코드에서 신규 결제 승인을 하기 위해 필요한 것임
        // 키오스크 서버에는 구현할 필요 없음
        // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
        //---------------------------------------------------------------------------------------
        // 상정앱의 지갑의 비밀키
        shopPrivateKey = "0xa237d68cbb66fd5f76e7b321156c46882546ad87d662dec8b82703ac31efbf0a";
        // 상정앱의 상점아이디
        shopId = "0x0003be96d74202df38fd21462ffcef10dfe0fcbd7caa3947689a3903e8b6b874";
        //---------------------------------------------------------------------------------------


        // 테스트를 할 네트워크
        network = CommonUtils.getNetWorkType(shopId);

        // 키오스크의 고유번호 (옵션, ""로 처리되어도 괜찮음)
        terminalID = "POS001";
        // 키오스크에 표시되었던 포인트 구매금액
        amount = Amount.make("1_000").getValue();
        // 키오스크에 표시되었던 환률 심벌
        currency = CommonUtils.getDefaultCurrencySymbol(network);

        client = new PaymentClient(network, keysOfPayment.get(network));
        listener = new TaskEventListener();
        collector = new TaskEventCollector(client, listener);
        userClient = new PaymentClientForUser(network, userPrivateKey);
        shopClient = new PaymentClientForShop(network, shopPrivateKey, shopId);
    }

    @Test
    public void test01_Create() throws Exception {
        assertNotNull(client);
        assertNotNull(collector);
        assertNotNull(userClient);
        assertNotNull(shopClient);
    }

    @Test
    public void test02_CreateEventCollector() throws Exception {
    }

    @Test
    public void test03_StartEventCollector() throws Exception {
        assertNotNull(collector);
        collector.start();
    }

    @Test
    public void test04_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Create Temporary Account
    // 사용자 모바일 앱에서 실행되는 내용임
    // 키오스크 서버에는 구현할 필요 없음
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test07_CreateTemporaryAccount() throws Exception {
        assertNotNull(userClient);
        temporaryAccount = userClient.getTemporaryAccount().get(30, TimeUnit.SECONDS);
        System.out.printf("  - Temporary Account: %s\n", temporaryAccount);
    }

    // Get Payment Info
    // 키오스크가 사용자앱에서 QR 코드를 입력받은 후 사용자의 포인트잔고를 확인하는 과정, 결제금액에 해당하는 포인트와 수수료등을 계산하여
    // 총사용량을 계산해준다.
    @Test
    public void test08_GetPaymentInfo() throws Exception {
        assertNotNull(client);
        info = client.getPaymentInfoAsync(temporaryAccount, amount, currency).get(30, TimeUnit.SECONDS);
        assertEquals(info.account.toLowerCase(), userClient.getAddress());
    }

    // Open New Payment (키오스크 시스템에서 실행해야 한다)
    // 결제를 오픈한다.
    // 키오스크 서버에서 실행해야 한다
    @Test
    public void test09_OpenNewPayment() throws Exception {
        paymentItem = client.openNewPayment(
                CommonUtils.getSamplePurchaseId(),          // 구매아이디는 서버에서 생성한다.
                temporaryAccount,                           // 사용자의 임시주소는 사용자용 앱의 QR 코드를 키오스크를 통해 전달한다.
                amount,                                     // 키오스크에 표시되었던 포인트 구매금액
                currency,                                   // 해당 상점의 키오스크 결제에 사용되는 환률심벌
                shopClient.getShopId(),                     // 키오스크와 매핑된 상점 아이디
                terminalID                                  // 키오스크의 고유번호
        ).get(30, TimeUnit.SECONDS);
    }

    // Waiting...
    // 결제가 오픈 될 때까지 잠시 대기한다
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test10_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Approval New Payment (사용자 앱에서 실행되기 때문 실제에는 필요없음)
    // 실제는 사용자가 푸쉬메세지를 받고 사용자용 앱에서 승인을 하나,
    // 이 코드에서는 사용자앱이 없기 때문에 테스트를 위해 수동을 승인한다
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test11_ApprovalNewPayment() throws Exception {
        userClient.approveNewPayment(
                paymentItem.paymentId,
                paymentItem.purchaseId,
                paymentItem.amount,
                paymentItem.currency,
                paymentItem.shopId,
                true
        ).get(30, TimeUnit.SECONDS);
    }

    // Waiting...
    // 결제가 승인 완료 될 때까지 잠시 대기한다
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test12_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Close New Payment (키오스크 시스템에서 실행해야 한다)
    // 결제를 닫는다
    @Test
    public void test13_CloseNewPayment() throws Exception {
        client.closeNewPayment(paymentItem.paymentId, true).get(30, TimeUnit.SECONDS);
    }

    // Waiting...
    // 직전 프로세스가 완료되기 까지 대기
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test14_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Open Cancel Payment (키오스크 시스템에서 실행해야 한다)
    // 결제를 취소를 오픈한다
    // 키오스크 서버에서 실행해야 한다
    @Test
    public void test15_OpenCancelPayment() throws Exception {
        client.openCancelPayment(paymentItem.paymentId, terminalID).get(30, TimeUnit.SECONDS);
    }

    // Waiting...
    // 직전 프로세스가 완료되기 까지 대기 (테스트를 위한 용도)
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test16_Waiting() throws Exception {
        Thread.sleep(3000);
    }


    // Approval Cancel Payment
    // 실제는 상점주가 푸쉬메세지를 받고 상점용 앱에서 승인을 하나,
    // 이 코드에서는 상점용 앱이 없기 때문에 테스트를 위해 수동을 승인한다
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test17_ApprovalCancelPayment() throws Exception {
        shopClient.approveCancelPayment(
                paymentItem.paymentId,
                paymentItem.purchaseId,
                true
        ).get(30, TimeUnit.SECONDS);
    }

    // Waiting...
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test18_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Close Cancel Payment (키오스크 시스템에서 실행해야 한다)
    // 결제를 취소를 닫는다
    // 키오스크 서버에서 실행해야 한다
    @Test
    public void test19_CloseCancelPayment() throws Exception {
        client.closeCancelPayment(paymentItem.paymentId, true).get(30, TimeUnit.SECONDS);
    }


    // Check...
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test20_Check() throws Exception {
        PaymentTaskItem paymentTaskItem = client.getPaymentItem(paymentItem.paymentId).get(30, TimeUnit.SECONDS);
        assertEquals(paymentTaskItem.paymentId, paymentItem.paymentId);
    }

    // Waiting...
    // 테스트 목적입니다. 실제코드에는 제거해도 됩니다.
    @Test
    public void test21_Waiting() throws Exception {
        Thread.sleep(3000);
    }

    // Stop Event Collector...
    @Test
    public void test22_StopEventCollector() throws Exception {
        collector.stop();
    }
}

class TaskEventListener implements ITaskEventListener {
    public void onNewPaymentEvent(
            String type,
            int code,
            String message,
            long sequence,
            PaymentTaskItem paymentTaskItem
    ) {
        System.out.printf("  -> onNewPaymentEvent %s - %d - %s - %d\n", type, code, message, sequence);
    }

    public void onNewShopEvent(
            String type,
            int code,
            String message,
            long sequence,
            ShopTaskItem shopTaskItem
    ) {
        System.out.printf("  -> onNewShopEvent %s - %d - %s - %d\n", type, code, message, sequence);
    }
}