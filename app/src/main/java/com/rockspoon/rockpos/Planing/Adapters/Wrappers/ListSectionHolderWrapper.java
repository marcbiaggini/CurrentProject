package com.rockspoon.rockpos.Planing.Adapters.Wrappers;

import android.view.View;

import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanListRecyclerAdapter;

public class ListSectionHolderWrapper extends HolderWrapper {
  private String title;
  public View.OnClickListener listener;
  public Boolean isExpanded = false;

  public ListSectionHolderWrapper(String title, View.OnClickListener listener) {
    this.title = title;
    this.listener = listener;
  }

  @Override
  public int getType() {
    return FloorPlanListRecyclerAdapter.ITEM_TYPE_HEADER;
  }

  public String getTitle() {
    return title;
  }
}
