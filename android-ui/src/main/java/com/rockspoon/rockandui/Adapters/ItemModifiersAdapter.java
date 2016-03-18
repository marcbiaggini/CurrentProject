package com.rockspoon.rockandui.Adapters;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.models.venue.settingsprices.CookingModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifierType;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ListItemCheckable;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ListItemCountable;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ListItemMixed;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ListItemSelectable;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ListItemSelectableWithPrice;
import com.rockspoon.rockandui.Components.OrderingItemComponents.ModifierView;
import com.rockspoon.rockandui.Interfaces.ModifierListener;
import com.rockspoon.rockandui.Managers.ItemModifiersManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by Eugen K. on 3/4/16.
 */
public class ItemModifiersAdapter extends RecyclerView.Adapter<ItemModifiersAdapter.ViewHolder> implements ModifierListener, ListItemSelectableWithPrice.SizeChangeListener {

  @IntDef({PRICES_CELL, SELECTABLE_CELL, COUNTABLE_CELL, CHECKABLE_CELL, MIXED_CELL})
  @Retention(RetentionPolicy.SOURCE)
  @interface CellType {
  }

  public static final int PRICES_CELL = 0;
  public static final int SELECTABLE_CELL = 1;
  public static final int COUNTABLE_CELL = 2;
  public static final int CHECKABLE_CELL = 3;
  public static final int MIXED_CELL = 4;

  private ItemModifiersManager manager;

  public ItemModifiersAdapter(ItemModifiersManager manager) {
    this.manager = manager;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, @CellType int typeView) {
    ViewHolder viewHolder = null;
    switch (typeView) {
      case CHECKABLE_CELL:
        viewHolder = new ViewHolder(new ListItemCheckable(parent.getContext()));
        break;
      case PRICES_CELL:
        ListItemSelectableWithPrice view = new ListItemSelectableWithPrice(parent.getContext());
        view.setSizeChangeListener(this);
        viewHolder = new ViewHolder(view);
        break;
      case SELECTABLE_CELL:
        viewHolder = new ViewHolder(new ListItemSelectable(parent.getContext()));
        break;
      case COUNTABLE_CELL:
        viewHolder = new ViewHolder(new ListItemCountable(parent.getContext()));
        break;
      case MIXED_CELL:
        viewHolder = new ViewHolder(new ListItemMixed(parent.getContext()));
        break;
    }
    return viewHolder;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void onBindViewHolder(ViewHolder holder, int position) {
    Object o = manager.getItemById(position);
    holder.modifierView.setSize(manager.getItemSize());
    holder.modifierView.setViewData(o);
    holder.modifierView.setPosition(position);
    holder.modifierView.setModifierListener(this);
  }

  @Override
  public int getItemCount() {
    return manager.getItemsCount();
  }

  @CellType
  @Override
  public int getItemViewType(int position) {
    Object o = manager.getItemById(position);
    if (o instanceof ItemModifiersManager.ItemPrices) {
      return PRICES_CELL;
    } else if (o instanceof CookingModifier) {
      return SELECTABLE_CELL;
    } else if (o instanceof ItemModifier) {
      if (Objects.equals(((ItemModifier) o).getType(), ItemModifierType.ingredient)) {
        return COUNTABLE_CELL;
      } else {
        return MIXED_CELL;
      }
    } else {
      return CHECKABLE_CELL;
    }
  }

  @Override
  public void onSizeChanged(String size) {
    manager.setItemSize(size);
    notifyDataSetChanged();
  }

  @Override
  public void onModifierReady(Object o, int position) {
    manager.setModifier(o, position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    ModifierView modifierView;

    public ViewHolder(View itemView) {
      super(itemView);
      modifierView = (ModifierView) itemView;
    }
  }

}
