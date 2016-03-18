package com.rockspoon.rockpos.CashPayment;

import android.os.Bundle;

import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.CashPayment.Fragments.CashPaymentFragment;
import com.rockspoon.rockpos.CashPayment.Fragments.CashPaymentFragment_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * CashPaymentActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/28/16.
 */
@EActivity(R.layout.cash_payment_activity)
public class CashPaymentActivity extends RSBaseActivity {
  private OrderPaymentData orderData;

  @Extra(OrderPaymentData.EXTRA_ORDER_DATA)
  void setOrderExtraData(OrderPaymentData order) {
    orderData = order;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CashPaymentFragment frag = CashPaymentFragment_.builder()
        .orderData(orderData)
        .build();
    addRootFragment(frag);
  }

}
