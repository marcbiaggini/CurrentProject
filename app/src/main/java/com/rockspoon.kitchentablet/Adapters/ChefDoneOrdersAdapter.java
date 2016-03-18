package com.rockspoon.kitchentablet.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.item.ItemsFired;
import com.rockspoon.rockandui.RecyclerTools.AutoMeasureLinearLayoutManager;
import com.rockspoon.rockandui.RecyclerTools.DividerItemDecoration;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/7/16.
 */
public class ChefDoneOrdersAdapter extends RecyclerView.Adapter<ChefDoneOrdersAdapter.DoneOrdersViewHolder> {

  private List<ItemsFired> orders;
  private List<ChefOrderItemsAdapter> itemsAdapters;
  private Context ctx;

  public ChefDoneOrdersAdapter(Context ctx, List<ItemsFired> orders) {
    this.ctx = ctx;
    setOrders(orders);
  }

  public void setOrders(List<ItemsFired> orders) {
    this.orders = orders;

    itemsAdapters = new ArrayList<>();
    for (ItemsFired order : orders) {
      ChefOrderItemsAdapter adapter = new ChefOrderItemsAdapter(ctx, order.getItemsInstances(), null);
      adapter.setAllowSelection(false);
      adapter.setItemViewSize(ChefOrderItemsAdapter.ItemViewSize.SMALL);
      itemsAdapters.add(adapter);
    }

    notifyDataSetChanged();
  }

  @Override
  public DoneOrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(ctx).inflate(R.layout.kitchen_chef_done_order_item, parent, false);
    return new DoneOrdersViewHolder(ctx, view);
  }

  @Override
  public void onBindViewHolder(DoneOrdersViewHolder holder, int position) {
    final ItemsFired order = orders.get(position);
    final ChefOrderItemsAdapter adapter = itemsAdapters.get(position);

    holder.tableNumberView.setText("Order #" + String.valueOf(order.getId()));
    holder.statusView.setText(order.getTypeAsString());
    holder.itemsListView.setAdapter(adapter);
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public class DoneOrdersViewHolder extends RecyclerView.ViewHolder {
    TextView tableNumberView;
    TextView statusView;
    RecyclerView itemsListView;

    public DoneOrdersViewHolder(Context ctx, View itemView) {
      super(itemView);

      tableNumberView = (TextView) itemView.findViewById(R.id.table_number);
      statusView = (TextView) itemView.findViewById(R.id.status);

      itemsListView = (RecyclerView) itemView.findViewById(R.id.items_list);
      itemsListView.setLayoutManager(new AutoMeasureLinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
      DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDividerHeightOffset(Tools.dpToPx(ctx, 20));
      itemsListView.addItemDecoration(dividerItemDecoration);
    }
  }
}
