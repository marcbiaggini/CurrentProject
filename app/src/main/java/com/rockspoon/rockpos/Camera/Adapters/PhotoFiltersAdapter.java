package com.rockspoon.rockpos.Camera.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.rockpos.Camera.PhotoFilter;
import com.rockspoon.rockpos.R;

import java.util.ArrayList;
import java.util.List;

/**
 * PhotoFiltersAdapter.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 2/17/16.
 */
public class PhotoFiltersAdapter extends RecyclerView.Adapter<PhotoFiltersAdapter.PhotoFiltersViewHolder> {

  private final Context ctx;
  private List<PhotoFilter> filters;

  public PhotoFiltersAdapter(Context ctx, List<PhotoFilter> filters) {
    this.ctx = ctx;
    this.filters = filters != null ? filters : new ArrayList<>();
  }

  public void setFilters(List<PhotoFilter> filters) {
    this.filters = filters;
    notifyDataSetChanged();
  }

  @Override
  public PhotoFiltersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(ctx).inflate(R.layout.photo_filter_item, parent, false);
    return new PhotoFiltersViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PhotoFiltersViewHolder holder, int position) {
    PhotoFilter filter = filters.get(position);
    holder.filterNameView.setText(filter.getName());
    holder.filterPreview.setImageBitmap(filter.getPreviewImage());
  }

  @Override
  public int getItemCount() {
    return filters.size();
  }

  public static class PhotoFiltersViewHolder extends RecyclerView.ViewHolder {
    final ImageView filterPreview;
    final TextView filterNameView;

    public PhotoFiltersViewHolder(View itemView) {
      super(itemView);

      this.filterPreview = (ImageView) itemView.findViewById(R.id.filterPreview);
      this.filterNameView = (TextView) itemView.findViewById(R.id.filterName);
    }
  }
}
