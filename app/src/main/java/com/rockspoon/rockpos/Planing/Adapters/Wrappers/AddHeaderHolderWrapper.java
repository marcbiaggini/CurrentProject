package com.rockspoon.rockpos.Planing.Adapters.Wrappers;

import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanHeaderAdapter;

public class AddHeaderHolderWrapper extends HolderWrapper {
  public AddHeaderHolderWrapper() {
  }

  public int getType() {
    return FloorPlanHeaderAdapter.ITEM_TYPE_ADD_HEADER;
  }
}
