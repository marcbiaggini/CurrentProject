package com.rockspoon.rockpos.OpenTabs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rockspoon.rockpos.OpenTabs.OpenTabsActivity.OpenTabItem;
import com.rockspoon.rockpos.OrderBilling.OrderBillingActivity;
import com.rockspoon.rockpos.OrderBilling.OrderBillingActivity_;
import com.rockspoon.rockpos.R;

import java.util.List;

/**
 * Created by Yury Minich on 2/8/16.
 */
public class OpenTabsAdapter extends BaseAdapter {

  private final Context ctx;
  private List<OpenTabItem> openTabsList;
  private String tableFormat;
  private String totalAmountFormat;

  public OpenTabsAdapter(Context context, List<OpenTabItem> list) {
    ctx = context;
    openTabsList = list;
    tableFormat = ctx.getString(R.string.format_table_n);
    totalAmountFormat = ctx.getString(R.string.format_price);
  }

  @Override
  public int getCount() {
    return openTabsList.size();
  }

  @Override
  public OpenTabItem getItem(int position) {
    return openTabsList.get(position);
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
    View view = LayoutInflater.from(ctx).inflate(com.rockspoon.rockandui.R.layout.open_tab_item, parent, false);
    holder.tableNumber = (TextView) view.findViewById(R.id.table_number);
    holder.orderAmount = (TextView) view.findViewById(R.id.table_total_amount);
    view.setTag(holder);

    return view;
  }

  private void bindView(View v, final int position) {
    final OpenTabItem openTab = getItem(position);
    ViewHolder holder = (ViewHolder) v.getTag();
    holder.tableNumber.setText(String.format(tableFormat, openTab.tableNumber));
    holder.orderAmount.setText(String.format(totalAmountFormat, openTab.orderAmount));
    v.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        OrderBillingActivity_.intent(ctx).extra(OrderBillingActivity.EXTRA_TABLE_NUMBER, openTab.tableNumber).start();
      }
    });
  }

  static class ViewHolder {
    public TextView tableNumber;
    public TextView orderAmount;
  }

}
