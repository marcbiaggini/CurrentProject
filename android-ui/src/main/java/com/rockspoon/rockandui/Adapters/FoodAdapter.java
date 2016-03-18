package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.rockandui.Interfaces.OnFoodItemOnClickListener;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by lucas on 10/07/15.
 */
@Deprecated
public class FoodAdapter extends LinearGridViewAdapter<Item> {

  @IdRes
  private final static int SINGLE_ITEM_ID = R.layout.food_item;

  @IdRes
  private final static int MULTIPLE_ITEM_ID = R.layout.food_item_multiple;

  private final int itemSpacing;

  private OnFoodItemOnClickListener listener;

  public FoodAdapter(final Context ctx) {
    super(ctx, 1);
    this.itemSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.food_item_spacing);
  }

  public FoodAdapter(final Context ctx, int columnCount) {
    super(ctx, columnCount);
    this.itemSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.food_item_spacing);
  }

  public FoodAdapter(final Context ctx, final List<Item> items) {
    super(ctx, 1, items);
    this.itemSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.food_item_spacing);
  }

  public void setFoodItemOnClickListener(final OnFoodItemOnClickListener listener) {
    this.listener = listener;
    this.notifyDataSetChanged();
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final Item item = getItem(position);

    if (getColCount() > 1) {
      final View v = (convertView == null || convertView.getId() != FoodAdapter.MULTIPLE_ITEM_ID) ? inflater.inflate(R.layout.food_item_multiple, null) : convertView;
      final Button b = (Button) v.findViewById(R.id.food_item_name);

      b.setText(item.getTitle());

      return v;
    } else {
      final View v = (convertView == null || convertView.getId() != FoodAdapter.SINGLE_ITEM_ID) ? inflater.inflate(R.layout.food_item, null) : convertView;
      final ViewHolder vH = (convertView == null || convertView.getId() != FoodAdapter.SINGLE_ITEM_ID) ? new ViewHolder(v) : (ViewHolder) v.getTag();

      vH.name.setText(item.getTitle());
      vH.rating.setVisibility(View.GONE); // TODO: Get rating from DB. We don't have any ratings yet.
      //rating.setRating(this.items.get(position));

      if (item.getPrices() == null) {
        vH.price.setText("");
      } else {
        vH.price.setText(String.format(ctx.getResources().getString(R.string.format_price), item.getSizePrices().get(0).getPrice()));
      }

      final StringBuilder sb = new StringBuilder();
      final List<Tag> stags = item.getTags();
      if (stags != null) {
        for (int i = 0; i < stags.size(); i++) {
          sb.append(stags.get(i).getTag());
          if (stags.size() - 1 != i) {
            sb.append(", ");
          }
        }
      }

      vH.tags.setText(sb.toString());
      vH.items.setText(item.getDescription());
//      if (item.getLogo() != null && item.getLogo().getNoResolution() != null) {
//        vH.image.setImageResource(R.color.white);
//        ImageData img = ImageData_.getInstance_(ctx).from(item.getLogo().getNoResolution().getUrl());
//        img.getImage(ctx, vH.image);
//      } else {
//        vH.image.setImageResource(R.color.white);
//      }
      GridLayout.LayoutParams params = new GridLayout.LayoutParams();
      params.setMargins(itemSpacing, itemSpacing, itemSpacing, 0);
      v.setLayoutParams(params);
      return v;
    }
  }

  @Override
  public void reorderItems(int orgPos, int newPos) {
    // Reorder disabled
  }

  @Override
  public boolean canReorder(int pos) {
    return false;
  }

  @Override
  public boolean itemIsSelected(int position) {
    return false;
  }

  static class ViewHolder {
    RatingBar rating;
    ImageView button;
    TextView price;
    TextView tags;
    TextView items;
    TextView name;
//    ImageView image;

    public ViewHolder(View v) {
      rating = (RatingBar) v.findViewById(R.id.food_item_rating);
      button = (ImageView) v.findViewById(R.id.food_item_arrowbtn);
      price = (TextView) v.findViewById(R.id.food_item_price);
      tags = (TextView) v.findViewById(R.id.food_item_tags);
      items = (TextView) v.findViewById(R.id.food_item_items);
      name = (TextView) v.findViewById(R.id.food_item_name);
//      image = (ImageView) v.findViewById(R.id.food_item_image);
      v.setTag(this);
    }
  }
}
