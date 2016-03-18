package com.rockspoon.rockpos.CustomBillSplit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rockspoon.rockandui.TestDataModel.CustomSplitData;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockpos.CustomBillSplit.Adapters.CustomBillSplitAdapter;
import com.rockspoon.rockpos.Payment.PaymentActivity;
import com.rockspoon.rockpos.Payment.PaymentActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yury Minich on 1/29/16.
 */
@EActivity(R.layout.custom_bill_split_activity)
public class CustomBillSplitActivity extends Activity implements CustomBillSplitAdapter.OnPayClickListener {
  public final static String EXTRA_SEATS_COUNT = "seats_count";

  private final DecimalFormat df = new DecimalFormat("0.00");

  @ViewById(R.id.page_title)
  TextView pageTitle;

  @ViewById(R.id.payers_count)
  TextView payersCountView;

  @ViewById(R.id.order_subtotal)
  TextView orderSubtotalView;

  @ViewById(R.id.order_tax)
  TextView orderTaxView;

  @ViewById(R.id.order_total)
  TextView orderTotalView;

  @ViewById(R.id.payers_list)
  ListView payersListView;

  @ViewById(R.id.root_layout)
  LinearLayout rootLayout;

  @Click(R.id.back_button)
  void backButtonClick() {
    onBackPressed();
  }

  private int seatsCount;
  private int payersCount;
  private OrderPaymentData orderData;
  private CustomBillSplitAdapter splitAdapter;
  private List<CustomSplitData> payersList;

  private BigDecimal billTotal;


  @Extra(EXTRA_SEATS_COUNT)
  void setSeatsCount(int count) {
    seatsCount = count;
  }

  @Extra(OrderPaymentData.EXTRA_ORDER_DATA)
  void setOrderExtraData(OrderPaymentData order) {
    orderData = order;
  }

  @Click(R.id.increment_payer)
  void incrementPayersCount() {
    BigDecimal paidAmount = getPaidAmount();
    if (payersCount < seatsCount && paidAmount.compareTo(orderData.getBillTotal()) == -1) {
      ++payersCount;
      payersCountView.setText(String.valueOf(payersCount));
      if (splitAdapter != null) {
        updatePayersList(payersCount, paidAmount);
        splitAdapter.notifyDataSetChanged();
      }
    }
  }

  @Click(R.id.decrement_payer)
  void decrementPayersCount() {
    BigDecimal paidAmount = getPaidAmount();
    if (payersCount > 1 && billTotal.compareTo(paidAmount) == 1) {
      if (checkPaidAmount(payersCount - 1)) {
        --payersCount;
        payersCountView.setText(String.valueOf(payersCount));
        if (splitAdapter != null) {
          updatePayersList(payersCount, paidAmount);
          splitAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  @AfterViews
  void initAfterViews() {
    pageTitle.setText(String.format(getString(R.string.format_payment_table), orderData.getTableNumber()));

    billTotal = orderData.getBillTotal();

    payersList = new ArrayList<>();
    payersCount = seatsCount;
    updatePayersList(payersCount, BigDecimal.ZERO);
    payersCountView.setText(String.valueOf(payersCount));

    orderSubtotalView.setText(df.format(orderData.getOrderSubtotal()));
    orderTaxView.setText(df.format(orderData.getOrderTax()));
    orderTotalView.setText(df.format(billTotal));

    splitAdapter = new CustomBillSplitAdapter(this, payersList, billTotal, this);
    payersListView.setAdapter(splitAdapter);

    setupHideKeyboardListeners(rootLayout);

  }

  private void updatePayersList(int payersCount, BigDecimal paidAmount) {
    int paidCount = getPaidCount();
    if (payersList.isEmpty()) {
      recountPayersList(payersCount);
    } else if (paidCount == 0) {
      payersList.clear();
      recountPayersList(payersCount);
    } else if (paidCount != 0) {
      int nonPaidSeats = payersCount - paidCount;
      BigDecimal nonPaidAmount = billTotal.subtract(paidAmount);
      int itemToRemove = -1;
      for (int i = 0; i < payersList.size(); i++) {
        CustomSplitData item = payersList.get(i);
        if (!item.isPayed()) {
          itemToRemove = i;
          item.setPayAmount(nonPaidAmount.divide(new BigDecimal(nonPaidSeats), OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP));
          item.setCustomAmount(false);
        }
      }
      if (payersCount < payersList.size() && itemToRemove != -1) {
        payersList.remove(itemToRemove);
      } else if (payersCount > payersList.size()) {
        payersList.add(new CustomSplitData(payersList.size() + 1, nonPaidAmount.divide(new BigDecimal(nonPaidSeats), OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP), nonPaidAmount.multiply(OrderPaymentData.TAX_PERCENT)));
      }
    }
  }

  private void recountPayersList(int payersCount) {
    payersList.clear();
    BigDecimal amount = orderData.getBillTotal().divide(new BigDecimal(payersCount), OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal tax = orderData.getOrderTax().divide(new BigDecimal(payersCount), OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
    for (int i = 0; i < payersCount; i++) {
      payersList.add(new CustomSplitData(i + 1, amount, tax));
    }
  }

  private int getPaidCount() {
    int paidCount = 0;
    for (CustomSplitData item : payersList) {
      if (item.isPayed()) {
        paidCount++;
      }
    }
    return paidCount;
  }

  private BigDecimal getPaidAmount() {
    BigDecimal paidAmount = BigDecimal.ZERO;
    for (CustomSplitData item : payersList) {
      if (item.isPayed()) {
        paidAmount = paidAmount.add(item.getPayAmount());
      }
    }
    return paidAmount;
  }

  private BigDecimal getNonPaidAmount() {
    BigDecimal nonPaidAmount = BigDecimal.ZERO;
    for (CustomSplitData item : payersList) {
      if (!item.isPayed()) {
        nonPaidAmount = nonPaidAmount.add(item.getPayAmount());
      }
    }
    return nonPaidAmount;
  }

  private boolean checkPaidAmount(int payersCount) {
    int paidCount = getPaidCount();
    if (paidCount == payersCount && billTotal.compareTo(getPaidAmount()) == 1) {
      return false;
    }
    return true;
  }

  @Override
  public void onPayClick(CustomSplitData data) {
    OrderPaymentData orderPaymentData = new OrderPaymentData(data.getPayerNumber(), orderData.getTableNumber(), getNonPaidAmount(), data.getPayAmount().subtract(data.getTax()));
    PaymentActivity_.intent(CustomBillSplitActivity.this).extra(OrderPaymentData.EXTRA_ORDER_DATA, orderPaymentData).startForResult(PaymentActivity.REQUEST_CODE_PAYED);
  }

  public void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    if (splitAdapter != null) {
      splitAdapter.notifyDataSetChanged();
    }
  }

  public void setupHideKeyboardListeners(View view) {
    //Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener((v, event) -> {
        hideSoftKeyboard(CustomBillSplitActivity.this);
        return false;
      });
    }
    //If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        setupHideKeyboardListeners(innerView);
      }
    }
  }

  @OnActivityResult(PaymentActivity.REQUEST_CODE_PAYED)
  void onResult(int resultCode, Intent data) {
    if (resultCode == PaymentActivity.REQUEST_CODE_PAYED) {
      OrderPaymentData order = (OrderPaymentData) data.getSerializableExtra(OrderPaymentData.EXTRA_ORDER_DATA);
      if (order != null) {
        for (CustomSplitData payer : payersList) {
          if (payer.getPayerNumber() == order.getPayerId()) {
            payer.setPayed(true);
            break;
          }
        }
        if (splitAdapter != null) {
          splitAdapter.notifyDataSetChanged();
        }
      }
    }
  }

}
