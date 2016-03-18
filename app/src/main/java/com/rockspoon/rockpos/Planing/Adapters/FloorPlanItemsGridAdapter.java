package com.rockspoon.rockpos.Planing.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rockspoon.rockandui.Objects.FloorPlanItemData;

import java.util.ArrayList;

public class FloorPlanItemsGridAdapter extends BaseAdapter {
  private final Context context;
  private ArrayList<FloorPlanItemData> items;

  public FloorPlanItemsGridAdapter(final Context ctx, ArrayList<FloorPlanItemData> items) {
    this.context = ctx;
    this.items = items;
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public FloorPlanItemData getItem(int i) {
    return items.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final FloorPlanItemData item = getItem(position);
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = inflater.inflate(com.rockspoon.rockandui.R.layout.floor_plan_grid_item, null);
      new ViewHolder(convertView);
    }

    viewHolder = (ViewHolder) convertView.getTag();
    viewHolder.mIcon.setImageDrawable(ContextCompat.getDrawable(convertView.getContext(), item.resId));
    viewHolder.mIcon.setOnTouchListener(mDragListener);
    viewHolder.mIcon.setTag(item);

    return convertView;
  }


  static class ViewHolder {
    ImageView mIcon;

    public ViewHolder(View v) {
      mIcon = (ImageView) v.findViewById(com.rockspoon.rockandui.R.id.item_image);
      v.setTag(this);
    }
  }

  View.OnTouchListener mDragListener = (view, motionEvent) -> {
    // start move on a touch event
    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
      ClipData data = ClipData.newPlainText("", "");
      View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
      view.startDrag(data, shadowBuilder, view, 0);
      return true;
    }
    return false;

  };
}