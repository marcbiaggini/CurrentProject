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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * ChefOrdersAdapter.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/1/16.
 */
public class ChefOrdersAdapter extends RecyclerView.Adapter<ChefOrdersAdapter.ChefOrderViewHolder> {
  public enum OrdersViewMode {
    INBOX,
    IN_PROGRESS
  }

  private List<ItemsFired> orders;
  private List<ChefOrderItemsAdapter> itemsAdapters;
  private List<ItemsFired> selectedOrders;
  private Context ctx;
  private OnSelectOrderItemListener listener;
  private OrdersViewMode viewMode;

  public ChefOrdersAdapter(Context ctx, List<ItemsFired> orders, OnSelectOrderItemListener listener) {
    this.ctx = ctx;
    this.listener = listener;
    this.selectedOrders = new ArrayList<>();
    setOrders(orders);
    setViewMode(OrdersViewMode.INBOX);
  }

  public void setOrders(List<ItemsFired> orders) {
    this.orders = orders;

    Collections.sort(this.orders, (lhs, rhs) -> {
      // recent orders should display first
      int result = rhs.getFireTime().compareTo(lhs.getFireTime());

      // if fire time is the same, compare by id (NOTE: maybe this will be enhanced in future)
      if (result == 0) {
        result = lhs.getId().compareTo(rhs.getId());
      }

      return result;
    });

    itemsAdapters = new ArrayList<>();
    for (ItemsFired order : orders) {
      ChefOrderItemsAdapter adapter = new ChefOrderItemsAdapter(ctx, order.getItemsInstances(), (selectedAdapter, selectedItems) -> {
        int orderIndex = itemsAdapters.indexOf(selectedAdapter);

        ItemsFired foundSelectedOrder = null;
        for (ItemsFired selectedOrder : this.selectedOrders) {
          if (selectedOrder.getId().equals(this.orders.get(orderIndex).getId())) {
            foundSelectedOrder = selectedOrder;
            break;
          }
        }

        if (foundSelectedOrder != null) {
          this.selectedOrders.remove(foundSelectedOrder);
        }

        if (selectedItems.size() > 0) {
          this.selectedOrders.add(this.orders.get(orderIndex).withItemsInstances(new HashSet<>(selectedItems)));
        }

        if (listener != null) {
          listener.onOrderItemSelectionChanged(this.selectedOrders);
        }
      });
      adapter.setViewMode(viewMode);

      itemsAdapters.add(adapter);
    }

    notifyDataSetChanged();
  }

  public void setViewMode(OrdersViewMode viewMode) {
    this.viewMode = viewMode;
    for (ChefOrderItemsAdapter adapter : itemsAdapters) {
      adapter.setViewMode(viewMode);
    }

    notifyDataSetChanged();
  }

  public List<ItemsFired> getSelectedOrders() {
    return selectedOrders;
  }

  public void clearAllSelections() {
    selectedOrders.clear();
    for (ChefOrderItemsAdapter adapter : itemsAdapters) {
      adapter.clearAllSelections();
    }

    notifyDataSetChanged();
  }

  @Override
  public ChefOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(ctx).inflate(R.layout.kitchen_chef_order_item, parent, false);
    return new ChefOrderViewHolder(ctx, view);
  }

  @Override
  public void onBindViewHolder(ChefOrderViewHolder holder, int position) {
    final ItemsFired order = orders.get(position);
    final ChefOrderItemsAdapter adapter = itemsAdapters.get(position);

    holder.tableNumberView.setText("Order #" + String.valueOf(order.getId()));
    holder.statusView.setText(order.getTypeAsString());
    holder.timeOrderedView.setText(Tools.timeMillisToString(System.currentTimeMillis() - order.getFireTime().getTime()));
    holder.itemsListView.setAdapter(adapter);
  }

  @Override
  public int getItemCount() {
    return orders.size();
  }

  public class ChefOrderViewHolder extends RecyclerView.ViewHolder {
    TextView tableNumberView;
    TextView statusView;
    TextView timeOrderedView;
    RecyclerView itemsListView;

    public ChefOrderViewHolder(Context ctx, View itemView) {
      super(itemView);

      tableNumberView = (TextView) itemView.findViewById(R.id.table_number);
      statusView = (TextView) itemView.findViewById(R.id.status);
      timeOrderedView = (TextView) itemView.findViewById(R.id.time_ordered);

      itemsListView = (RecyclerView) itemView.findViewById(R.id.items_list);
      itemsListView.setLayoutManager(new AutoMeasureLinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));
      DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDividerHeightOffset(Tools.dpToPx(ctx, 20));
      itemsListView.addItemDecoration(dividerItemDecoration);
    }
  }

  public interface OnSelectOrderItemListener {
    void onOrderItemSelectionChanged(List<ItemsFired> selectedOrders);
  }
}
