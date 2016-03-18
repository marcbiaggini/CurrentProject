package com.rockspoon.rockpos.CreditCardPayment.Fragments;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Dialogs.SimpleWaitingDialog;
import com.rockspoon.rockandui.TestDataModel.CreditCard;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

/**
 * CreditCardReadingFragment.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/19/16.
 */
@EFragment
public class CreditCardReadingFragment extends RSBaseFragment {

  @FragmentArg
  OrderPaymentData orderData;

  @ViewById(R.id.order_subtotal)
  TextView orderSubtotalView;

  @ViewById(R.id.order_tax)
  TextView orderTaxView;

  @ViewById(R.id.order_total)
  TextView orderTotalView;

  @ViewById(R.id.order_your_payment)
  TextView orderYourPayment;

  @ViewById(R.id.waiting_view)
  LinearLayout waitingView;

  @ViewById(R.id.dialog_simple_waiting_title)
  TextView waitingTitle;

  @StringRes(R.string.format_price_no_dollar_sign)
  String formatPrice;

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    if (orderData != null) {
      orderSubtotalView.setText(String.format(formatPrice, orderData.getOrderSubtotal()));
      orderTaxView.setText(String.format(formatPrice, orderData.getOrderTax()));
      orderTotalView.setText(String.format(formatPrice, orderData.getOrderTotal()));
      orderYourPayment.setText(String.format(formatPrice, orderData.getCreditCardPayment().getAmount()));
      waitingTitle.setText(getString(R.string.message_transaction_in_progress));
    }
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.credit_card_reading_fragment;
  }

  @Override
  public String getFragmentTitle() {
    String tableNumber = orderData != null ? String.valueOf(orderData.getTableNumber()) : "";
    return getString(R.string.format_credit_card_table, tableNumber);
  }

  @Override
  protected void showWaitingDialog(String title, String message, String label) {
    dialog = SimpleWaitingDialog.newInstance(null).setTitle(title);
    dialog.show(getFragmentManager(), label);
  }

  @Click(R.id.swipe_card_button)
  void swipeCardButtonClicked() {
    // TODO: Just simple credit card reader interaction simulation. Should be replaced with real API/framework calls
    simulateCreditCardReading();
  }

  private void simulateCreditCardReading() {
    waitingView.setVisibility(View.VISIBLE);
    onCreditCardRead();
  }

  @UiThread(delay = 3000)
  void onCreditCardRead() {
    closeWaitingDialog();
    // send test credit card data
    CreditCard creditCard = new CreditCard();
    creditCard.setCardNumber("1111 2222 3333 4444");
    orderData.setCreditCard(creditCard);

    CreditCardSigningFragment frag = CreditCardSigningFragment_.builder()
        .orderData(orderData)
        .build();
    replaceFragment(frag, false, false);
    waitingView.setVisibility(View.GONE);
  }

}
