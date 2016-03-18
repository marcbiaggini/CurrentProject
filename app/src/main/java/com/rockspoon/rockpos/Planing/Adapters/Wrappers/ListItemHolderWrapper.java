package com.rockspoon.rockpos.Planing.Adapters.Wrappers;

import com.rockspoon.rockandui.RecyclerTools.ExpandableBaseHolderWrapper;
import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockandui.Objects.FloorPlanItemData;

import java.util.ArrayList;

public class ListItemHolderWrapper extends ExpandableBaseHolderWrapper {
  private int type;
  private ArrayList<FloorPlanItemData> items;

  public ListItemHolderWrapper(int type, ArrayList<FloorPlanItemData> items, HolderWrapper wrapper) {
    super(wrapper);

    this.type = type;
    this.items = items;
  }

  @Override
  public int getType() {
    return type;
  }

  public ArrayList<FloorPlanItemData> getItems() {
    return items;
  }
}
