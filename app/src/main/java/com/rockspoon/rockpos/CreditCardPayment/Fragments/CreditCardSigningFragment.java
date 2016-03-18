package com.rockspoon.rockpos.CreditCardPayment.Fragments;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Components.SignatureCaptureView;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CreditCardSigningFragment.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/19/16.
 */
@EFragment
public class CreditCardSigningFragment extends RSBaseFragment {

  @FragmentArg
  OrderPaymentData orderData;

  @ViewById(R.id.original_bill_view)
  TextView youPayment;

  @ViewById(R.id.tip_view)
  TextView tipView;

  @ViewById(R.id.new_total_view)
  TextView newTotalView;

  @ViewById(R.id.custom_tip_button)
  RadioButton customTipButton;

  @ViewById(R.id.tip_buttons_container)
  RadioGroup tipButtonsContainer;

  @ViewById(R.id.signature_input_view)
  SignatureCaptureView signatureInputView;

  @ViewById(R.id.custom_tip_view)
  EditText customTipView;

  @ViewById(R.id.confirm_button)
  Button confirmButton;

  @StringRes(R.string.format_price_no_dollar_sign)
  String formatPrice;

  private boolean isUserSign = false;
  private InputMethodManager keyboard;

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    keyboard = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));

    tipButtonsContainer.setOnCheckedChangeListener(onTipCheckedChangeListener);
    tipButtonsContainer.check(R.id._15_percent_tip_button);

    signatureInputView.setOnTouchListener((view, motionEvent) -> {
      switch (motionEvent.getAction()) {
        case MotionEvent.ACTION_DOWN:
//          getActivity().setEnableScrollingOnRoot(false);
          isUserSign = true;
          manageIsSendButtonAccessible();
          break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
//          getActivity().setEnableScrollingOnRoot(true);
          break;
      }

      return false;
    });

    if (orderData != null) {
      if (orderData.getCreditCard().isSigned()) {
        signatureInputView.setSignature(orderData.getCreditCard().getSignatureImage());
        isUserSign = true;
      }

      // set 15% as default tips
      BigDecimal defaultTips = orderData.getCreditCardPayment().getAmount().multiply(new BigDecimal("0.15"));
      orderData.getCreditCardPayment().setTips(defaultTips);
    }

    manageIsSendButtonAccessible();
    updateUI();

    customTipView.setOnEditorActionListener(onEditorActionListener);
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.credit_card_signing_fragment;
  }

  @Override
  public String getFragmentTitle() {
    String tableNumber = orderData != null ? String.valueOf(orderData.getTableNumber()) : "";
    return getString(R.string.format_credit_card_table, tableNumber);
  }

  private void updateUI() {
    if (orderData != null) {
      youPayment.setText(String.format(formatPrice, orderData.getCreditCardPayment().getAmount()));
      tipView.setText(String.format(formatPrice, orderData.getCreditCardPayment().getTips()));
      newTotalView.setText(String.format(formatPrice, orderData.getCreditCardPayment().getPaymentTotal()));
    }
  }

  private RadioGroup.OnCheckedChangeListener onTipCheckedChangeListener = (group, checkedId) -> {
    boolean isKeyboardVisible = false;

    switch (checkedId) {
      case R.id.no_tip_button:
        orderData.getCreditCardPayment().setTips(BigDecimal.ZERO);
        customTipView.setVisibility(View.GONE);
        break;
      case R.id._15_percent_tip_button:
        orderData.getCreditCardPayment().setTips(orderData.getCreditCardPayment().getAmount().multiply(new BigDecimal("0.15")));
        customTipView.setVisibility(View.GONE);
        break;
      case R.id._20_percent_tip_button:
        orderData.getCreditCardPayment().setTips(orderData.getCreditCardPayment().getAmount().multiply(new BigDecimal("0.2")));
        customTipView.setVisibility(View.GONE);
        break;
      case R.id.custom_tip_button:
        customTipView.setVisibility(View.VISIBLE);
        isKeyboardVisible = true;
        break;
    }

    setKeyboardVisibility(isKeyboardVisible, customTipView);
    manageIsSendButtonAccessible();
    updateUI();
  };

  @Click(R.id.confirm_button)
  void confirmButtonClicked() {
    orderData.getCreditCard().setSignatureImage(signatureInputView.getSignatureAsBitmap());
    PaymentReceiptFragment frag = PaymentReceiptFragment_.builder()
        .orderData(orderData)
        .build();
    replaceFragment(frag, false);
  }

  @Click(R.id.clear_signature_button)
  void clearSignatureButtonClicked() {
    isUserSign = false;
    signatureInputView.clearView();
    manageIsSendButtonAccessible();
  }

  private TextView.OnEditorActionListener onEditorActionListener = (v, actionId, event) -> {
    String percentStr = ((EditText) v).getText().toString();
    BigDecimal percent = percentStr.length() == 0 ? BigDecimal.ZERO : new BigDecimal(percentStr);
    orderData.getCreditCardPayment().setTips(orderData.getCreditCardPayment().getAmount()
        .divide(BigDecimal.valueOf(100.0), RoundingMode.HALF_UP).multiply(percent));
    updateUI();

    if (actionId == EditorInfo.IME_ACTION_DONE) {
      setKeyboardVisibility(false, v);
    }

    manageIsSendButtonAccessible();
    return true;
  };

  private void setKeyboardVisibility(boolean isVisible, View view) {
    if (keyboard != null) {
      view.requestFocus();
      if (isVisible) {
        keyboard.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
      } else {
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    }
  }

  private void manageIsSendButtonAccessible() {
    if (customTipButton.isChecked() && isUserSign) {
      confirmButton.setEnabled(customTipView.length() != 0);
    } else if (isUserSign) {
      confirmButton.setEnabled(true);
    } else {
      confirmButton.setEnabled(false);
    }
  }

}

