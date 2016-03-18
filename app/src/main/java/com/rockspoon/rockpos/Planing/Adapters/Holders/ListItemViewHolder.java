package com.rockspoon.rockpos.Planing.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rockspoon.rockandui.Components.ExpandableHeightGridView;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanItemsGridAdapter;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.ListItemHolderWrapper;
import com.rockspoon.rockpos.R;

public class ListItemViewHolder extends RecyclerView.ViewHolder {
  private ExpandableHeightGridView gridView;

  public ListItemViewHolder(View itemView) {
    super(itemView);
    gridView = (ExpandableHeightGridView) itemView.findViewById(R.id.table_items);
  }

  public void bindData(ListItemHolderWrapper holderWrapper) {
    FloorPlanItemsGridAdapter adapter = new FloorPlanItemsGridAdapter(gridView.getContext(), holderWrapper.getItems());
    gridView.setAdapter(adapter);
    gridView.setExpanded(true);
  }
}