package com.rockspoon.rockpos.CreditCardPayment.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.TestDataModel.CreditCard;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

/**
 * PaymentReceiptFragment.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/19/16.
 */
@EFragment
public class PaymentReceiptFragment extends RSBaseFragment {

  @FragmentArg
  OrderPaymentData orderData;

  @ViewById(R.id.order_total_view)
  TextView orderTotalView;

  @ViewById(R.id.credit_card_info_view)
  TextView creditCardInfoView;

  @ViewById(R.id.credit_card_paid_view)
  TextView creditCardPaidView;

  @ViewById(R.id.print_receipt_button)
  Button printReceiptView;

  @ViewById(R.id.send_receipt_by_sms_button)
  Button sendReceiptByMailView;

  @ViewById(R.id.send_receipt_by_email_button)
  Button sendReceiptByEmailView;

  @ViewById(R.id.no_receipt_button)
  Button noReceiptButton;

  @StringRes(R.string.format_price_no_dollar_sign)
  String formatPrice;

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    if (orderData != null) {
      orderTotalView.setText(String.format(formatPrice, orderData.getOrderTotal()));
      creditCardPaidView.setText(String.format(formatPrice, orderData.getCreditCardPayment().getPaymentTotal()));

      CreditCard creditCard = orderData.getCreditCard();
      if (creditCard != null) {
        creditCardInfoView.setText(String.format(getString(R.string.format_paid_credit_card_info),
            creditCard.getLastFourDigits()));
      }
    }
    printReceiptView.setOnClickListener(onClickListener);
    sendReceiptByMailView.setOnClickListener(onClickListener);
    sendReceiptByEmailView.setOnClickListener(onClickListener);
    noReceiptButton.setOnClickListener(onClickListener);
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.payment_receipt;
  }

  @Override
  public String getFragmentTitle() {
    String tableNumber = orderData != null ? String.valueOf(orderData.getTableNumber()) : "";
    return getString(R.string.format_payment_table, tableNumber);
  }

  private View.OnClickListener onClickListener = v -> {
    orderData.orderPartPayed(orderData.getCreditCardPayment().getAmount());

    Intent resultIntent = new Intent();
    resultIntent.putExtra(OrderPaymentData.EXTRA_ORDER_DATA, orderData);
    getActivity().setResult(Activity.RESULT_OK, resultIntent);
    getActivity().finish();
  };

}
