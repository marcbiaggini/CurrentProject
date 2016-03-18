package com.rockspoon.rockpos.Planing.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.rockandui.Adapters.BaseRecyclerAdapter;
import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.Holders.AddHeaderViewHolder;
import com.rockspoon.rockpos.Planing.Adapters.Holders.HeaderViewHolder;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.AddHeaderHolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.HeaderHolderWrapper;
import com.rockspoon.rockpos.R;

import java.util.ArrayList;

public class FloorPlanHeaderAdapter extends BaseRecyclerAdapter {
  public static final int ITEM_TYPE_HEADER = 100;
  public static final int ITEM_TYPE_ADD_HEADER = 101;

  private final Context context;

  private HolderWrapper currentSelectedItem;
  private FloorPlansTabsListener floorPlansTabsListener;
  private Boolean isVisible = false;
  private final RecyclerView recyclerView;

  public FloorPlanHeaderAdapter(Context context, ArrayList<VenueFloorPlan> plans, RecyclerView recyclerView) {
    this.context = context;
    this.recyclerView = recyclerView;

    for (VenueFloorPlan plan : plans) {
      holderWrappers.add(new HeaderHolderWrapper(plan));
    }
    currentSelectedItem = holderWrappers.get(0);
    holderWrappers.add(new AddHeaderHolderWrapper());
  }

  public void addItemAtPosition(HolderWrapper holderClicked) {
    int position = holderWrappers.indexOf(holderClicked);

    addNewItem(holderClicked, createNewHeader(position));
  }

  public void addNewItem(HolderWrapper itemClicked, HolderWrapper newItem) {
    int position = holderWrappers.indexOf(itemClicked);
    holderWrappers.add(position, newItem);
    if (position != 0) {
      notifyItemInserted(position);
    } else {
      notifyDataSetChanged();
    }
  }

  public void removeItem(HolderWrapper item) {
    int position = holderWrappers.indexOf(item);
    holderWrappers.remove(position);
    if (item == currentSelectedItem) {
      currentSelectedItem = null;
    }
    if (position != 0) {
      notifyItemRemoved(position);
    } else {
      if (holderWrappers.size() == 1) {
        createNewHeader(position);
      }

      notifyDataSetChanged();
    }
  }

  private HeaderHolderWrapper createNewHeader(int position) {
    String venuePlanName = String.format(context.getResources().getString(R.string.section_placeholder), position + 1);
    VenueFloorPlan newVenueFloorPlan = VenueFloorPlan.newInstance(venuePlanName);

    return new HeaderHolderWrapper(newVenueFloorPlan);
  }

  public void selectItem(HolderWrapper item) {
    int position = holderWrappers.indexOf(item);
    if (position < holderWrappers.size()) {
      currentSelectedItem = item;
      notifyDataSetChanged();
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;

    switch (viewType) {
      case ITEM_TYPE_HEADER:
        view = LayoutInflater.from(context).inflate(R.layout.floor_plan_header_item, parent, false);
        return new HeaderViewHolder(view);
      case ITEM_TYPE_ADD_HEADER:
        view = LayoutInflater.from(context).inflate(R.layout.floor_plan_add_item, parent, false);
        return new AddHeaderViewHolder(view);
      default:
        return null;
    }
  }

  public void setAddHeaderItemVisibility(Boolean isVisible) {
    if (this.isVisible != isVisible) {
      this.isVisible = isVisible;

      if (!recyclerView.isComputingLayout()) {
        notifyItemChanged(holderWrappers.size() - 1);
      }
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (holderWrappers.get(position).getType()) {
      case ITEM_TYPE_HEADER:
        HeaderViewHolder profileHeaderHolder = (HeaderViewHolder) holder;
        HeaderHolderWrapper profileHeaderHolderWrapper = (HeaderHolderWrapper) holderWrappers.get(position);
        profileHeaderHolder.bindData(currentSelectedItem, profileHeaderHolderWrapper, floorPlansTabsListener, this);
        break;
      case ITEM_TYPE_ADD_HEADER:
        AddHeaderViewHolder addHeaderViewHolder = (AddHeaderViewHolder) holder;
        AddHeaderHolderWrapper addFloorPlanHeaderHolderWrapper = (AddHeaderHolderWrapper) holderWrappers.get(position);
        addHeaderViewHolder.bindData(addFloorPlanHeaderHolderWrapper, floorPlansTabsListener, isVisible);
        break;
      default:
        break;
    }
  }

  public void setListener(FloorPlansTabsListener floorPlansTabsListener) {
    this.floorPlansTabsListener = floorPlansTabsListener;
    notifyDataSetChanged();
  }

  public int indexOfFloorPlanHeader(HolderWrapper item) {
    return holderWrappers.indexOf(item);
  }

  @Override
  public int getItemCount() {
    return holderWrappers.size();
  }

  public interface FloorPlansTabsListener {
    void onItemPressed(HolderWrapper item);

    void onItemLongPressed(HolderWrapper item);

    void onAddItemPressed(HolderWrapper item);

    void onCloseItemPressed(HolderWrapper item);
  }
}