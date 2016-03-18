package com.rockspoon.rockpos.Payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockandui.TestDataModel.OrderSubPaymentData;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.CashPayment.CashPaymentActivity_;
import com.rockspoon.rockpos.CreditCardPayment.CreditCardPaymentActivity_;
import com.rockspoon.rockpos.PaymentCompleted.PaymentCompletedActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.math.BigDecimal;

/**
 * PaymentActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/14/16.
 */
@EActivity(R.layout.payment_main)
public class PaymentActivity extends Activity {

  public static final int REQUEST_CODE_PAYED = 200;
  public static final int REQUEST_CODE_SHOW_COMPLETED_SCREEN = 201;

  private OrderPaymentData orderData;

  @ViewById(R.id.page_title)
  TextView pageTitleView;

  @ViewById(R.id.order_subtotal)
  TextView orderSubtotalView;

  @ViewById(R.id.order_tax)
  TextView orderTaxView;

  @ViewById(R.id.order_total)
  TextView orderTotalView;

  @ViewById(R.id.order_total_amount)
  EditText orderTotalAmountView;

  @ViewById(R.id.order_total_amount_error_text)
  TextView getOrderTotalErrorTextView;

  @ViewById(R.id.order_scroll_view)
  ScrollView orderScrollView;

  @ViewById(R.id.payment_activity_root_view)
  View rootView;

  @Extra(OrderPaymentData.EXTRA_ORDER_DATA)
  void setOrderExtraData(OrderPaymentData order) {
    orderData = order;
  }

  @StringRes(R.string.format_price_no_dollar_sign)
  String formatPrice;

  @AfterViews
  void initViews() {
    fillData();
    Tools.setupHideKeyboardListeners(rootView, this);
  }

  private void fillData() {
    if (orderData != null) {
      pageTitleView.setText(String.format(getString(R.string.format_payment_table), String.valueOf(orderData.getTableNumber())));
      orderSubtotalView.setText(String.format(formatPrice, orderData.getOrderSubtotal()));
      orderTaxView.setText(String.format(formatPrice, orderData.getOrderTax()));
      orderTotalView.setText(String.format(formatPrice, orderData.getOrderTotal()));
      orderTotalAmountView.setText(String.format(formatPrice, orderData.getOrderTotal()));
      getOrderTotalErrorTextView.setText(String.format(getString(R.string.message_wrong_amount), orderData.getOrderTotal()));
    }
  }

  @Click(R.id.back_button)
  void backButtonClicked() {
    Intent resultIntent = new Intent();
    resultIntent.putExtra(OrderPaymentData.EXTRA_ORDER_DATA, orderData);
    setResult(RESULT_CANCELED, resultIntent);
    finish();
  }

  @Click(R.id.cash_button)
  void cashButtonClicked() {
    setCashPaymentData();

    CashPaymentActivity_.intent(this)
        .extra(OrderPaymentData.EXTRA_ORDER_DATA, orderData)
        .startForResult(REQUEST_CODE_PAYED);
  }

  @Click(R.id.credit_card_button)
  void creditCardButtonClicked() {
    setCreditCartPaymentData();

    CreditCardPaymentActivity_.intent(this)
        .extra(OrderPaymentData.EXTRA_ORDER_DATA, orderData)
        .startForResult(REQUEST_CODE_PAYED);
  }

  @Click(R.id.debit_card_button)
  void debitCardButtonClicked() {

  }

  @Click(R.id.rockspoon_button)
  void rockspoonButtonClicked() {

  }

  @AfterTextChange(R.id.order_total_amount)
  void validateTotalAmount() {
    if (orderTotalAmountView.getText().toString().length() == 0) {
      //case for empty value
      return;
    }

    BigDecimal totalAmountVal = new BigDecimal(orderTotalAmountView.getText().toString());

    if (totalAmountVal.compareTo(orderData.getOrderTotal()) == 1) {
      getOrderTotalErrorTextView.setVisibility(View.VISIBLE);
      orderTotalAmountView.setTextColor(getResources().getColor(R.color.textcolor_black));
    } else {
      getOrderTotalErrorTextView.setVisibility(View.GONE);
      orderTotalAmountView.setTextColor(getResources().getColor(R.color.textcolor_lightblue));
    }
  }

  @FocusChange(R.id.order_total_amount)
  void onTotalAmountViewFocus() {
    new Handler().post(() -> orderScrollView.scrollTo(0, orderTotalAmountView.getBottom()));
  }

  private void setCreditCartPaymentData() {
    orderData.setCreditCardPayment(newOrderSubPaymentDataInstance());
  }

  private void setCashPaymentData() {
    orderData.setCashPayment(newOrderSubPaymentDataInstance());
  }

  private OrderSubPaymentData newOrderSubPaymentDataInstance() {
    String orderTotalAmountText = orderTotalAmountView.getText().toString();
    BigDecimal orderTotalAmount = orderTotalAmountText.length() == 0 ? BigDecimal.ZERO : new BigDecimal(orderTotalAmountText);
    return new OrderSubPaymentData(orderTotalAmount, BigDecimal.ZERO);
  }

  @OnActivityResult(REQUEST_CODE_PAYED)
  void onResultPayed(int resultCode, @OnActivityResult.Extra(value = OrderPaymentData.EXTRA_ORDER_DATA) OrderPaymentData order) {
    if (resultCode == RESULT_OK && order != null) {
      orderData = order;

      if (order.isPaid()) {
        PaymentCompletedActivity_.intent(this).startForResult(REQUEST_CODE_SHOW_COMPLETED_SCREEN);
      } else {
        fillData();
      }
    }
  }

  @OnActivityResult(REQUEST_CODE_SHOW_COMPLETED_SCREEN)
  void onResultCompletedScreen(int resultCode) {
    if (resultCode == RESULT_OK) {
      Intent resultIntent = new Intent();
      resultIntent.putExtra(OrderPaymentData.EXTRA_ORDER_DATA, orderData);
      setResult(RESULT_OK, resultIntent);
      finish();
    }
  }

}
