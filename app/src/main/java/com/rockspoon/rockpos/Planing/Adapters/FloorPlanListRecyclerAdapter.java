package com.rockspoon.rockpos.Planing.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.models.venue.layout.SpotShape;
import com.rockspoon.rockandui.Adapters.BaseRecyclerAdapter;
import com.rockspoon.rockandui.Objects.FloorPlanItemData;
import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.Holders.ListSectionViewHolder;
import com.rockspoon.rockpos.Planing.Adapters.Holders.ListItemViewHolder;
import com.rockspoon.rockandui.RecyclerTools.ExpandableBaseHolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.ListSectionHolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.ListItemHolderWrapper;
import com.rockspoon.rockpos.R;

import java.util.ArrayList;
import java.util.HashMap;

public class FloorPlanListRecyclerAdapter extends BaseRecyclerAdapter {
  public static final int ITEM_TYPE_HEADER = 200;
  public static final int ITEM_TYPE_TABLES = 201;

  HashMap<Integer, ExpandableBaseHolderWrapper> expandableWrappers = new HashMap<>();

  public FloorPlanListRecyclerAdapter(Context context) {
    HolderWrapper tableHeader = new ListSectionHolderWrapper(context.getString(R.string.tables), new OnHeaderClickListener(ITEM_TYPE_TABLES));
    holderWrappers.add(tableHeader);

    ArrayList<FloorPlanItemData> items = new ArrayList<>();
    items.add(new FloorPlanItemData(R.drawable.vec_table_first, SpotShape.circular));
    items.add(new FloorPlanItemData(R.drawable.vec_table_second, SpotShape.counter));
    items.add(new FloorPlanItemData(R.drawable.vec_table_third, SpotShape.rectangular));
    items.add(new FloorPlanItemData(R.drawable.vec_table_fouth, SpotShape.oval));
    expandableWrappers.put(ITEM_TYPE_TABLES, new ListItemHolderWrapper(ITEM_TYPE_TABLES, items, tableHeader));
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

    switch (viewType) {
      case ITEM_TYPE_HEADER:
        view = layoutInflater.inflate(R.layout.floor_plan_section, parent, false);
        return new ListSectionViewHolder(view);
      case ITEM_TYPE_TABLES:
        view = layoutInflater.inflate(R.layout.floor_plan_list_item, parent, false);
        return new ListItemViewHolder(view);
      default:
        break;
    }
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (holderWrappers.get(position).getType()) {
      case ITEM_TYPE_HEADER:
        ListSectionViewHolder listSectionViewHolder = (ListSectionViewHolder) holder;
        ListSectionHolderWrapper listSectionHolderWrapper = (ListSectionHolderWrapper) holderWrappers.get(position);

        listSectionViewHolder.bindData(listSectionHolderWrapper);
        break;
      case ITEM_TYPE_TABLES:
        ListItemViewHolder floorPlanListItemViewHolder = (ListItemViewHolder) holder;
        ListItemHolderWrapper listItemHolderWrapper = (ListItemHolderWrapper) holderWrappers.get(position);

        floorPlanListItemViewHolder.bindData(listItemHolderWrapper);
        break;
      default:
        break;
    }
  }

  private void expandItemOnIndex(Integer type) {
    int index = holderWrappers.indexOf(expandableWrappers.get(type));
    int headerIndex = holderWrappers.indexOf(expandableWrappers.get(type).getHeaderView());
    if (index != -1) {
      holderWrappers.remove(index);
      notifyItemRemoved(index);
    } else {
      holderWrappers.add(headerIndex + 1, expandableWrappers.get(type));
      notifyItemInserted(headerIndex + 1);
    }
  }

  public class OnHeaderClickListener implements View.OnClickListener {
    Integer itemType;

    public OnHeaderClickListener(Integer itemType) {
      this.itemType = itemType;
    }

    @Override
    public void onClick(View view) {
      expandItemOnIndex(itemType);
    }
  }
}
