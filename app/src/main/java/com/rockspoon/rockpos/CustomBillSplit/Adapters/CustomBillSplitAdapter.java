package com.rockspoon.rockpos.CustomBillSplit.Adapters;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.TestDataModel.CustomSplitData;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Yury Minich on 1/29/16.
 */
public class CustomBillSplitAdapter extends BaseAdapter {

  private final Context ctx;
  private BigDecimal totalAmount;
  private String payFormat;
  private List<CustomSplitData> payersList;

  private BigDecimal paidAmount = new BigDecimal("0");
  private BigDecimal customEnteredAmount = new BigDecimal("0");
  private int nonCustomEnteredFields = 0;

  private OnPayClickListener onPayClickListener;

  public CustomBillSplitAdapter(Context context, List<CustomSplitData> payers, BigDecimal total, OnPayClickListener onPayListener) {
    ctx = context;
    payFormat = ctx.getString(R.string.format_price_only);
    totalAmount = total;
    payersList = payers;
    onPayClickListener = onPayListener;
  }

  public void setPayersList(List<CustomSplitData> payers) {
    payersList = payers;
  }

  @Override
  public int getCount() {
    return payersList.size();
  }

  @Override
  public CustomSplitData getItem(int position) {
    return payersList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = newView(parent);
    }
    bindView(convertView, position);

    return convertView;
  }

  private View newView(ViewGroup parent) {
    ViewHolder holder = new ViewHolder();
    View view = LayoutInflater.from(ctx).inflate(R.layout.custom_bill_split_item, parent, false);
    holder.payerNumber = (TextView) view.findViewById(R.id.payer_number);
    holder.payerAmount = (EditText) view.findViewById(R.id.pay_amount);
    holder.payButton = (Button) view.findViewById(R.id.pay_button);
    holder.labelPaid = (TextView) view.findViewById(R.id.label_paid);
    view.setTag(holder);

    return view;
  }

  private void bindView(View v, final int position) {
    final CustomSplitData payer = getItem(position);
    ViewHolder holder = (ViewHolder) v.getTag();
    holder.payerNumber.setText(ctx.getString(R.string.format_payer_n, payer.getPayerNumber()));
    holder.payerAmount.setText(String.format(payFormat, payer.getPayAmount().doubleValue()));
    if (!payer.isPayed()) {
      holder.payButton.setVisibility(View.VISIBLE);
      holder.labelPaid.setVisibility(View.GONE);
      holder.payerAmount.setEnabled(true);
      holder.payerAmount.setOnEditorActionListener((view, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          recountAmount(view, position);
        }
        return false;
      });
      if (payer.getPayAmount().signum() == 1) {
        holder.payButton.setOnClickListener(v1 -> {
//          payer.setPayed(true);
          if (onPayClickListener != null) {
            onPayClickListener.onPayClick(payer);
          }
        });
        holder.payButton.setEnabled(true);
      } else {
        holder.payButton.setEnabled(false);
      }

    } else {
      holder.payButton.setVisibility(View.GONE);
      holder.labelPaid.setVisibility(View.VISIBLE);
      holder.payerAmount.setEnabled(false);
    }

  }


  static class ViewHolder {
    public TextView payerNumber;
    public EditText payerAmount;
    public Button payButton;
    public TextView labelPaid;
  }

  private void recountAmount(TextView view, int position) {
    Editable newAmount = view.getEditableText();
    if (newAmount.length() > 0) {
      BigDecimal amount = new BigDecimal(newAmount.toString());
      BigDecimal oldAmount = payersList.get(position).getPayAmount();
      payersList.get(position).setCustomAmount(true);
      payersList.get(position).setPayAmount(amount);
      if (checkSum(amount)) {
        adjustAmount();
      } else {
        payersList.get(position).setCustomAmount(false);
        payersList.get(position).setPayAmount(oldAmount);
      }
      notifyDataSetChanged();
    }
  }

  private boolean checkSum(BigDecimal amount) {
    if (amount.compareTo(totalAmount) == 1) {
      return false;
    }
    updatePaymentFields();
    BigDecimal checkSum = (totalAmount.subtract(paidAmount)).subtract(customEnteredAmount);
    if (checkSum.signum() == -1) {
      return false;
    }
    if (nonCustomEnteredFields == 0 && checkSum.signum() != 0) {
      return false;
    }
    return true;
  }

  private void adjustAmount() {
    BigDecimal checkSum = (totalAmount.subtract(paidAmount)).subtract(customEnteredAmount);
    BigDecimal payersAmount = checkSum.divide(new BigDecimal(nonCustomEnteredFields), OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
    for (CustomSplitData item : payersList) {
      if (!item.isCustomAmount() && !item.isPayed()) {
        item.setPayAmount(payersAmount);
      }
    }
  }

  private void updatePaymentFields() {
    paidAmount = BigDecimal.ZERO;
    customEnteredAmount = BigDecimal.ZERO;
    nonCustomEnteredFields = 0;
    for (CustomSplitData item : payersList) {
      if (item.isPayed()) {
        paidAmount = paidAmount.add(item.getPayAmount());
      }
      if (!item.isPayed() && item.isCustomAmount()) {
        customEnteredAmount = customEnteredAmount.add(item.getPayAmount());
      }
      if (!item.isPayed() && !item.isCustomAmount()) {
        nonCustomEnteredFields++;
      }
    }
  }

  public interface OnPayClickListener {
    void onPayClick(CustomSplitData data);
  }

}
