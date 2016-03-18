package com.rockspoon.rockpos.Planing.Adapters.Wrappers;

import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanHeaderAdapter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderHolderWrapper extends HolderWrapper {
  private VenueFloorPlan venuePlan;

  public HeaderHolderWrapper(VenueFloorPlan venuePlan) {
    this.venuePlan = venuePlan;
  }

  @Override
  public int getType() {
    return FloorPlanHeaderAdapter.ITEM_TYPE_HEADER;
  }
}
