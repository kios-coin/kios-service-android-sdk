package org.kios.service.android.sdk.event;

import org.kios.service.android.sdk.data.payment.PaymentTaskItem;
import org.kios.service.android.sdk.data.payment.ShopTaskItem;

public interface ITaskEventListener {
    void onNewPaymentEvent(
            String type,
            int code,
            String message,
            long sequence,
            PaymentTaskItem paymentTaskItem
    );

    void onNewShopEvent(
            String type,
            int code,
            String message,
            long sequence,
            ShopTaskItem shopTaskItem
    );
}

