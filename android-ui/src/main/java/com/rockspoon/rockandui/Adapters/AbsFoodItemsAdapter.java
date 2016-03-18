package com.rockspoon.rockandui.Adapters;

import android.support.v7.widget.RecyclerView;

import com.rockspoon.rockandui.Interfaces.FoodListClickListener;

/**
 * Created by greenfrvr
 */
public abstract class AbsFoodItemsAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

  protected FoodListClickListener listener;

  public void setFoodItemClickListener(FoodListClickListener listener) {
    this.listener = listener;
  }

}
