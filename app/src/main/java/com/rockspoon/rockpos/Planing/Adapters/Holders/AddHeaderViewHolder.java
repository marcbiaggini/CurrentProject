package com.rockspoon.rockpos.Planing.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rockspoon.rockpos.Planing.Adapters.FloorPlanHeaderAdapter;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.AddHeaderHolderWrapper;
import com.rockspoon.rockpos.R;

public class AddHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private FloorPlanHeaderAdapter.FloorPlansTabsListener delegate;
  private AddHeaderHolderWrapper currentItem;
  private View itemView;

  public AddHeaderViewHolder(View itemView) {
    super(itemView);
    this.itemView = itemView;
    this.itemView.findViewById(R.id.floor_plan_add_header).setOnClickListener(this);
  }

  public void bindData(AddHeaderHolderWrapper item, FloorPlanHeaderAdapter.FloorPlansTabsListener delegate, Boolean isVisible) {
    this.delegate = delegate;
    currentItem = item;
    itemView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

  @Override
  public void onClick(View view) {
    if (delegate != null) {
      delegate.onAddItemPressed(currentItem);
    }
  }
}
