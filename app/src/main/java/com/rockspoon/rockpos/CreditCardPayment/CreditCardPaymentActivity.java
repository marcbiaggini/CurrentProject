package com.rockspoon.rockpos.CreditCardPayment;

import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.CreditCardPayment.Fragments.CreditCardReadingFragment;
import com.rockspoon.rockpos.CreditCardPayment.Fragments.CreditCardReadingFragment_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * CreditCardPaymentActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/18/16.
 */
@EActivity(R.layout.credit_card_payment_activity)
public class CreditCardPaymentActivity extends RSBaseActivity {
  private OrderPaymentData orderData;

  @Extra(OrderPaymentData.EXTRA_ORDER_DATA)
  void setOrderExtraData(OrderPaymentData order) {
    orderData = order;
  }

  @AfterViews
  void initViews() {
    CreditCardReadingFragment frag = CreditCardReadingFragment_.builder()
        .orderData(orderData)
        .build();
    replaceFragment(frag, false, false);
  }
}
