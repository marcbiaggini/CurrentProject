package com.rockspoon.rockpos.Ordering.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by greenfrvr
 */
public abstract class OrderViewHolder<T> extends RecyclerView.ViewHolder {

  public OrderViewHolder(View itemView) {
    super(itemView);
  }

  abstract void bind(T obj);
}
