package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by greenfrvr
 */
public class FoodItemsGridAdapter extends AbsFoodItemsAdapter<FoodItemsGridAdapter.ItemViewHolder> {

  private final Context ctx;
  private List<Item> items;

  public FoodItemsGridAdapter(Context context, List<Item> items) {
    this.ctx = context;
    this.items = items;
  }

  @Override
  public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(ctx).inflate(R.layout.food_grid_item, parent, false);
    return new ItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ItemViewHolder holder, int position) {
    holder.bind(items.get(position));
    holder.itemView.setOnClickListener(v -> listener.onFoodItemClicked(items.get(position)));
  }

  @Override
  public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView nameView;

    public ItemViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.food_item_name);
    }

    public void bind(Item item) {
      nameView.setText(item.getTitle());
    }
  }

}
