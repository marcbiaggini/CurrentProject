package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FoodItemsAdapter extends AbsFoodItemsAdapter<FoodItemsAdapter.ItemViewHolder> {

  private final Context ctx;
  private List<Item> items;
  private Boolean showLogo = false;

  public FoodItemsAdapter(Context context, List<Item> items) {
    this.ctx = context;
    this.items = items;
  }

  public FoodItemsAdapter(Context context) {
    this.ctx = context;
    this.items = new ArrayList<>();
  }

  @Override
  public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(ctx).inflate(R.layout.food_item, parent, false);
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
    RatingBar ratingView;
    ImageView arrowButton;
    ImageView logoView;
    TextView priceView;
    TextView itemsView;
    TextView tagsView;
    TextView unavailableView;

    public ItemViewHolder(View itemView) {
      super(itemView);
      nameView = (TextView) itemView.findViewById(R.id.food_item_name);
      ratingView = (RatingBar) itemView.findViewById(R.id.food_item_rating);
      arrowButton = (ImageView) itemView.findViewById(R.id.food_item_arrowbtn);
      priceView = (TextView) itemView.findViewById(R.id.food_item_price);
      itemsView = (TextView) itemView.findViewById(R.id.food_item_items);
      tagsView = (TextView) itemView.findViewById(R.id.food_item_tags);
      unavailableView = (TextView) itemView.findViewById(R.id.food_item_unavailable_message);
      logoView = (ImageView) itemView.findViewById(R.id.item_logo);
    }

    public void bind(Item item) {
      nameView.setText(item.getTitle());
      ratingView.setVisibility(View.GONE); // TODO: Get rating from DB. We don't have any ratings yet.

      String price = "";
      if (item.getPrices() != null) {
        price = ctx.getResources().getString(R.string.format_price, item.getSizePrices().get(0).getPrice());
      }
      priceView.setText(price);

      tagsView.setText(extractTags(item));
      itemsView.setText(item.getDescription());
      logoView.setVisibility(item.getLogo() != null && showLogo ? View.VISIBLE : View.GONE);

      if (item.getLogo() != null) {
        ImageData imageData = ImageData_.getInstance_(ctx);
        imageData.from(item.getLogo().getNoResolution().getUrl());
        logoView.setImageDrawable(imageData.getImage(ctx, logoView));
      }
    }

    private String extractTags(Item item) {
      final StringBuilder sb = new StringBuilder();
      if (item.getTags() != null) {
        Iterator<Tag> iterator = item.getTags().iterator();

        while (iterator.hasNext()) {
          sb.append(iterator.next().getTag());
          if (iterator.hasNext()) {
            sb.append(", ");
          }
        }
      }
      return sb.toString();
    }
  }

  public void setShowLogo(Boolean showLogo) {
    this.showLogo = showLogo;
  }
}