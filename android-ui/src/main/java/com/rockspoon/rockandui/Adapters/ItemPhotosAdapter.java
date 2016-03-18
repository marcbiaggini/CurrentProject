package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.rockandui.Interfaces.OnFoodItemOnClickListener;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by lucas on 11/01/16.
 */
public class ItemPhotosAdapter extends FilterAdapter<Item> {

  private final int itemSpacing;

  private OnFoodItemOnClickListener listener;

  public ItemPhotosAdapter(final Context ctx) {
    super(ctx);
    this.itemSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.food_item_spacing);
  }

  public ItemPhotosAdapter(final Context ctx, final List<Item> items) {
    super(ctx, items);
    this.itemSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.food_item_spacing);
  }

  public void setFoodItemOnClickListener(final OnFoodItemOnClickListener listener) {
    this.listener = listener;
    this.notifyDataSetChanged();
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.itemphotos_item, null) : convertView;
    final ViewHolder vH = (convertView == null) ? new ViewHolder(v) : (ViewHolder) v.getTag();
    final Item item = getItem(position);

    if (listener != null) {
      v.setOnClickListener((not_used) -> listener.onClick(item, position));
      vH.button.setOnClickListener((not_used) -> listener.onClick(item, position));
    }

    vH.name.setText(item.getTitle());
    vH.rating.setVisibility(View.GONE); // TODO: Get rating from DB. We don't have any ratings yet.
    //rating.setRating(this.items.get(position));

    final StringBuilder sb = new StringBuilder();
    final List<Tag> stags = item.getTags();
    if (stags != null) {
      for (int i = 0; i < stags.size(); i++) {
        sb.append(stags.get(i).getTag());
        if (stags.size() - 1 != i)
          sb.append(", ");
      }
    }

    vH.tags.setText(sb.toString());
    vH.description.setText(item.getDescription());
    if (item.getLogo() != null && item.getLogo().getNoResolution() != null) {
      vH.image.setImageResource(R.color.white);
      ImageData img = ImageData_.getInstance_(ctx).from(item.getLogo().getNoResolution().getUrl());
      img.getImage(ctx, vH.image);
    } else {
      vH.image.setImageResource(R.color.white);
    }
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(itemSpacing, itemSpacing, itemSpacing, 0);
    v.setLayoutParams(params);

    return v;
  }

  static class ViewHolder {
    RatingBar rating;
    ImageView button;
    TextView tags;
    TextView description;
    TextView name;
    ImageView image;

    public ViewHolder(View v) {
      rating = (RatingBar) v.findViewById(R.id.itemphotos_item_rating);
      button = (ImageView) v.findViewById(R.id.itemphotos_item_arrowbtn);
      tags = (TextView) v.findViewById(R.id.itemphotos_item_tags);
      description = (TextView) v.findViewById(R.id.itemphotos_item_description);
      name = (TextView) v.findViewById(R.id.itemphotos_item_name);
      image = (ImageView) v.findViewById(R.id.itemphotos_item_image);
      v.setTag(this);
    }
  }
}
