package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.rockspoon.rockpos.R;

/**
 * Created by greenfrvr
 */
public class SelectAllHolder extends OrderViewHolder<String> {

  private CheckBox selectAll;

  public SelectAllHolder(View itemView) {
    super(itemView);
    selectAll = (CheckBox) itemView.findViewById(R.id.order_select_all);
  }

  public void setOnSelectAllListener(CompoundButton.OnCheckedChangeListener listener) {
    selectAll.setOnCheckedChangeListener(listener);
  }

  @Override
  void bind(String str) {
  }

}
