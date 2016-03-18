package com.rockspoon.kitchentablet.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.item.ItemInstanceStatus;
import com.rockspoon.models.venue.ordering.item.ItemSummaryForKitchen;
import com.rockspoon.rockpos.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/2/16.
 */
public class ChefOrderItemsAdapter extends RecyclerView.Adapter<ChefOrderItemsAdapter.ChefOrderItemViewHolder> {
  public enum ItemViewSize {
    STANDARD,
    SMALL
  }

  private static final float ITEM_DONE_ALPHA = 0.3f;

  private Context ctx;
  private List<ItemSummaryForKitchen> items;
  private List<ItemSummaryForKitchen> selectedItems;
  private OnSelectItemListener listener;
  private int focusedItem = 0;
  private ChefOrdersAdapter.OrdersViewMode viewMode;
  private boolean allowSelection;
  private ItemViewSize itemViewSize;

  public ChefOrderItemsAdapter(Context ctx, Set<ItemSummaryForKitchen> items, OnSelectItemListener listener) {
    this.ctx = ctx;
    this.listener = listener;
    this.selectedItems = new ArrayList<>();
    this.allowSelection = true;
    this.itemViewSize = ItemViewSize.STANDARD;
    setViewMode(ChefOrdersAdapter.OrdersViewMode.INBOX);
    setItems(items);
  }

  public void setItems(Set<ItemSummaryForKitchen> items) {
    this.items = new ArrayList<>(items);

    Collections.sort(this.items, (lhs, rhs) -> {
      // sort by id (NOTE: maybe this will be enhanced in future
      return lhs.getItemInstanceId().compareTo(rhs.getItemInstanceId());
    });

    notifyDataSetChanged();
  }

  public List<ItemSummaryForKitchen> getSelectedItems() {
    return selectedItems;
  }

  public void clearAllSelections() {
    selectedItems.clear();
    notifyDataSetChanged();
  }

  public void setViewMode(ChefOrdersAdapter.OrdersViewMode viewMode) {
    this.viewMode = viewMode;
    notifyDataSetChanged();
  }

  public void setAllowSelection(boolean allowSelection) {
    this.allowSelection = allowSelection;
    notifyDataSetChanged();
  }

  public void setItemViewSize(ItemViewSize itemViewSize) {
    this.itemViewSize = itemViewSize;
    notifyDataSetChanged();
  }

  @Override
  public ChefOrderItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int subItemResId = itemViewSize == ItemViewSize.STANDARD ? R.layout.kitchen_chef_order_subitem : R.layout.kitchen_chef_order_subitem_small;
    View view = LayoutInflater.from(ctx).inflate(subItemResId, parent, false);
    return new ChefOrderItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ChefOrderItemViewHolder holder, int position) {
    final ItemSummaryForKitchen item = items.get(position);

    holder.titleView.setText(item.getName() != null ? item.getName() : "No title");
    boolean hasDetails = item.getModifiersDescription() != null;
    holder.observationView.setText(hasDetails ? item.getModifiersDescription() : "");

    if (item.getStatus() == ItemInstanceStatus.in_progress && viewMode == ChefOrdersAdapter.OrdersViewMode.INBOX) {
      holder.itemView.setSelected(true);
      holder.observationView.setVisibility(View.GONE);
      holder.itemView.setAlpha(1.0f);
      holder.itemView.setEnabled(true);
      holder.fireIcon.setVisibility(View.VISIBLE);
    } else if (item.getStatus() == ItemInstanceStatus.ready_for_delivery && viewMode == ChefOrdersAdapter.OrdersViewMode.IN_PROGRESS) {
      holder.itemView.setSelected(false);
      holder.itemView.setAlpha(ITEM_DONE_ALPHA);
      holder.itemView.setEnabled(false);
      holder.observationView.setVisibility(hasDetails ? View.VISIBLE : View.GONE);
      holder.fireIcon.setVisibility(View.GONE);
    } else {
      holder.itemView.setSelected(selectedItems.contains(item));
      holder.itemView.setAlpha(1.0f);
      holder.itemView.setEnabled(allowSelection);
      holder.observationView.setVisibility(hasDetails ? View.VISIBLE : View.GONE);
      holder.fireIcon.setVisibility(View.GONE);
    }
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);

    // Handle key up and key down and attempt to move selection
    recyclerView.setOnKeyListener((v, keyCode, event) -> {
      RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

      // Return false if scrolled to the bounds and allow focus to move off the list
      if (event.getAction() == KeyEvent.ACTION_DOWN) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
          return tryMoveSelection(lm, 1);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
          return tryMoveSelection(lm, -1);
        }
      }

      return false;
    });
  }

  private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
    final int tryFocusItem = focusedItem + direction;

    // If still within valid bounds, move the selection, notify to redraw, and scroll
    if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
      notifyItemChanged(focusedItem);
      focusedItem = tryFocusItem;
      notifyItemChanged(focusedItem);
      lm.scrollToPosition(focusedItem);
      return true;
    }

    return false;
  }

  private void toggleListItem(ItemSummaryForKitchen item) {
    if (item.getStatus() == ItemInstanceStatus.in_progress &&
        viewMode == ChefOrdersAdapter.OrdersViewMode.INBOX) {
      return;
    }

    if (item.getStatus() == ItemInstanceStatus.ready_for_delivery &&
        viewMode == ChefOrdersAdapter.OrdersViewMode.IN_PROGRESS) {
      return;
    }

    if (selectedItems.contains(item)) {
      selectedItems.remove(item);
    } else {
      selectedItems.add(item);
    }

    if (listener != null) {
      listener.onItemSelectionChanged(this, selectedItems);
    }
  }

  public class ChefOrderItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView titleView;
    TextView observationView;
    ImageView fireIcon;

    public ChefOrderItemViewHolder(View itemView) {
      super(itemView);

      itemView.setOnClickListener(this);
      titleView = (TextView) itemView.findViewById(R.id.item_ordered);
      observationView = (TextView) itemView.findViewById(R.id.item_observation);
      fireIcon = (ImageView) itemView.findViewById(R.id.fire_icon);
    }

    @Override
    public void onClick(View v) {
      // Redraw the old selection and the new
      notifyItemChanged(focusedItem);
      focusedItem = getLayoutPosition();
      toggleListItem(items.get(getLayoutPosition()));
      notifyItemChanged(focusedItem);
    }
  }

  public interface OnSelectItemListener {
    void onItemSelectionChanged(ChefOrderItemsAdapter adapter, List<ItemSummaryForKitchen> selectedItems);
  }
}
