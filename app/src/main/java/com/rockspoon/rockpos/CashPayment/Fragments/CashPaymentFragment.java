package com.rockspoon.rockpos.CashPayment.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Components.TopBar;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockandui.utils.DynamicBillsHelper;
import com.rockspoon.rockandui.utils.ErrorPopupWindow;
import com.rockspoon.rockandui.utils.PopupUtil;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.List;

/**
 * CashPaymentFragment.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/28/16.
 */
@EFragment
public class CashPaymentFragment extends RSBaseFragment {

  @FragmentArg
  OrderPaymentData orderData;

  @ViewById(R.id.order_total_view)
  TextView orderTotalView;

  @ViewById(R.id.your_payment_view)
  EditText yourPaymentView;

  @ViewById(R.id.bill_buttons_container)
  RadioGroup billButtonsContainer;

  @ViewById(R.id.custom_bill_view)
  EditText customBillView;

  @ViewById(R.id.tip_buttons_container)
  RadioGroup tipButtonsContainer;

  @ViewById(R.id.tips_section_overlay)
  View tipsSectionOverlay;

  @ViewById(R.id.custom_tip_view)
  EditText customTipView;

  @ViewById(R.id.change_view_container)
  ViewGroup changeViewContainer;

  @ViewById(R.id.change_view)
  TextView changeView;

  @ViewById(R.id._first_dollars_bill_button)
  RadioButton firstButton;

  @ViewById(R.id._second_dollars_bill_button)
  RadioButton secondButton;

  @ViewById(R.id._third_dollars_bill_button)
  RadioButton thirdButton;

  @ViewById(R.id._fourth_dollars_bill_button)
  RadioButton fourthButton;

  private List<BigDecimal> billsValues;

  private BigDecimal selectedBillValue = BigDecimal.ZERO;

  private ErrorPopupWindow popupWindow = null;

  private InputMethodManager keyboard;

  private String priceRadioButtonFormat;

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    priceRadioButtonFormat = getString(R.string.format_price_string);
    if (orderData != null) {
      orderTotalView.setText(Tools.priceToString(getActivity(), orderData.getOrderTotal()));
      yourPaymentView.setText(Tools.priceToString(getActivity(), orderData.getCashPayment().getAmount()));
      billButtonsContainer.setOnCheckedChangeListener(onBillCheckedChangeListener);
      tipButtonsContainer.setOnCheckedChangeListener(onTipCheckedChangeListener);

      customBillView.setOnEditorActionListener(listener);
      yourPaymentView.setOnEditorActionListener(listener);
      customTipView.setOnEditorActionListener(listener);

      updateBillsValues();
      keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

      View rootView = getView();
      if (rootView != null) {
        rootView.setFocusable(false);
        rootView.setOnClickListener(v -> setKeyboardVisibility(false, v));
      }
    }
    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.cash_payment_fragment;
  }

  @Override
  public String getFragmentTitle() {
    String tableNumber = orderData != null ? String.valueOf(orderData.getTableNumber()) : "";
    return getString(R.string.format_cash_payment_table, tableNumber);
  }

  void onTextChangesOnYourPaymentView(CharSequence text) {
    orderData.getCashPayment().setAmount((text.length() > 0) ? new BigDecimal(text.toString()) : BigDecimal.ZERO);
    updateChangeView();
    updateBillsValues();
  }

  void onTextChangesOnCustomBillView() {
    if (customBillView.getText().toString().length() > 0) {
      setBillValue(new BigDecimal(customBillView.getText().toString()));
      updateChangeView();
      updateCustomTipView();
    }
  }

  void onTextChangesOnCustomTipView() {
    if (customTipView.getText().toString().length() > 0) {
      setTipValue(orderData.getCashPayment().getAmount()
          .divide(BigDecimal.valueOf(100.0), BigDecimal.ROUND_HALF_UP)
          .multiply(new BigDecimal(customTipView.getText().toString())));
      updateChangeView();
      updateCustomTipView();
    }
  }

  private void updateCustomTipView() {
    if (customTipView.getText().length() == 0) {
      dismissErrorPopupWindow();
      return;
    }

    BigDecimal change = selectedBillValue.subtract(orderData.getCashPayment().getAmount()
        .add(orderData.getCashPayment().getTips()));
    customTipView.setBackground(change.signum() >= 0 ? getResources().getDrawable(R.drawable.border_background_transparent) :
        getResources().getDrawable(R.drawable.border_red_background_transparent));

    if (change.compareTo(BigDecimal.ZERO) == -1) {
      if (popupWindow == null) {
        popupWindow = PopupUtil.errorPopupWindow(getActivity(), getString(R.string.message_wrong_tips));
      }
      if (!popupWindow.isShowing()) {
        popupWindow.showOnRightSide(customTipView);
      }
    } else {
      dismissErrorPopupWindow();
    }
  }

  private void dismissErrorPopupWindow() {
    if (popupWindow != null) {
      popupWindow.dismiss();
    }
  }

  private RadioGroup.OnCheckedChangeListener onBillCheckedChangeListener = (group, checkedId) -> {
    switch (checkedId) {
      case R.id._first_dollars_bill_button:
        setBillValue(billsValues.get(0));
        setCustomBill(false);
        break;
      case R.id._second_dollars_bill_button:
        setBillValue(billsValues.get(1));
        setCustomBill(false);
        break;
      case R.id._third_dollars_bill_button:
        setBillValue(billsValues.get(2));
        setCustomBill(false);
        break;
      case R.id._fourth_dollars_bill_button:
        setBillValue(billsValues.get(3));
        setCustomBill(false);
        break;
      case R.id.custom_bill_button:
        setCustomBill(true);
        break;
    }

    updateChangeView();
  };

  private RadioGroup.OnCheckedChangeListener onTipCheckedChangeListener = (group, checkedId) -> {
    switch (checkedId) {
      case R.id.no_tip_button:
        setTipValue(calculateTips(BigDecimal.ZERO));
        setCustomTip(false);
        break;
      case R.id._15_percent_tip_button:
        setTipValue(calculateTips(BigDecimal.valueOf(0.15)));
        setCustomTip(false);
        break;
      case R.id._20_percent_tip_button:
        setTipValue(calculateTips(BigDecimal.valueOf(0.2)));
        setCustomTip(false);
        break;
      case R.id.custom_tip_button:
        setCustomTip(true);
        break;
    }

    updateChangeView();
  };

  private TextView.OnEditorActionListener listener = (v, actionId, event) -> {

    switch (v.getId()) {
      case R.id.custom_bill_view:
        onTextChangesOnCustomBillView();
        break;
      case R.id.custom_tip_view:
        onTextChangesOnCustomTipView();
        break;
      case R.id.your_payment_view:
        onTextChangesOnYourPaymentView(yourPaymentView.getText().toString());
        break;
    }

    if (actionId == EditorInfo.IME_ACTION_DONE) {
      InputMethodManager imm = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
      imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    return true;
  };

  private void setBillValue(BigDecimal value) {
    selectedBillValue = value;
    tipsSectionOverlay.setVisibility(View.GONE);
    setTipValue(calculateTips(BigDecimal.valueOf(0.15)));
    updateChangeView();
  }

  private void setCustomBill(boolean isCustomBillOptChecked) {
    if (!isCustomBillOptChecked) {
      customBillView.setText("");
    }
    customBillView.setVisibility(isCustomBillOptChecked ? View.VISIBLE : View.GONE);
    setKeyboardVisibility(isCustomBillOptChecked, customBillView);
  }

  private void setTipValue(BigDecimal value) {
    orderData.getCashPayment().setTips(value);
    changeViewContainer.setVisibility(View.VISIBLE);
  }

  private void setCustomTip(boolean isCustomOptChecked) {
    if (!isCustomOptChecked) {
      customTipView.setText("");
    }
    updateCustomTipView();
    customTipView.setVisibility(isCustomOptChecked ? View.VISIBLE : View.GONE);
    setKeyboardVisibility(isCustomOptChecked, customTipView);
  }

  private void setKeyboardVisibility(boolean isVisible, View view) {
    view.requestFocus();
    if (isVisible) {
      keyboard.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    } else {
      keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private void updateChangeView() {
    BigDecimal change = selectedBillValue.subtract(orderData.getCashPayment().getAmount().add(orderData.getCashPayment().getTips()));
    changeView.setText(Tools.priceToString(getActivity(), change));
    boolean isPaymentReady = change.compareTo(BigDecimal.ZERO) >= 0;
    int changeColorId = isPaymentReady ? R.color.textcolor_greenprice : R.color.textcolor_red;
    changeView.setTextColor(ContextCompat.getColor(getActivity(), changeColorId));

    TopBar topBar = getTopBar();
    if (topBar != null) {
      topBar.extraButtonClickable(isPaymentReady);
    }
  }

  private BigDecimal calculateTips(BigDecimal percent) {
    return orderData.getCashPayment().getAmount().multiply(percent);
  }

  private void updateBillsValues() {
    billsValues = DynamicBillsHelper.populateBills(orderData.getCashPayment().getAmount());

    firstButton.setText(String.format(priceRadioButtonFormat, billsValues.get(0).toString()));
    secondButton.setText(String.format(priceRadioButtonFormat, billsValues.get(1).toString()));
    thirdButton.setText(String.format(priceRadioButtonFormat, billsValues.get(2).toString()));
    fourthButton.setText(String.format(priceRadioButtonFormat, billsValues.get(3).toString()));
  }

  @Override
  public void onExtraButtonClicked() {
    orderData.orderPartPayed(orderData.getCashPayment().getAmount());

    Intent resultIntent = new Intent();
    resultIntent.putExtra(OrderPaymentData.EXTRA_ORDER_DATA, orderData);
    getActivity().setResult(Activity.RESULT_OK, resultIntent);
    getActivity().finish();
  }
}
