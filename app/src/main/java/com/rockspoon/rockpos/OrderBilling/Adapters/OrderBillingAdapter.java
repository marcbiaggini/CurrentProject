package com.rockspoon.rockpos.OrderBilling.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.TestDataModel.SeatOrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yury Minich on 1/14/16.
 */
public class OrderBillingAdapter extends RecyclerView.Adapter<OrderBillingAdapter.BillingViewHolder> {

  private final Context ctx;
  private List<SeatOrderItem> orderData;
  private String priceFormat;

  public void add(SeatOrderItem s, int position) {
    position = position == -1 ? getItemCount() : position;
    orderData.add(position, s);
    notifyItemInserted(position);
  }

  public void remove(int position) {
    if (position < getItemCount()) {
      orderData.remove(position);
      notifyItemRemoved(position);
    }
  }

  public static class BillingViewHolder extends RecyclerView.ViewHolder {
    public final TextView foodTitle;
    public final TextView foodNotes;
    public final ImageView foodImage;
    public final TextView foodCount;
    public final TextView unitPrice;
    public final TextView totalPrice;

    public BillingViewHolder(View view) {
      super(view);
      foodTitle = (TextView) view.findViewById(R.id.food_title);
      foodNotes = (TextView) view.findViewById(R.id.food_notes);
      foodImage = (ImageView) view.findViewById(R.id.food_image);
      foodCount = (TextView) view.findViewById(R.id.food_count);
      unitPrice = (TextView) view.findViewById(R.id.unit_price);
      totalPrice = (TextView) view.findViewById(R.id.total_price);
    }
  }

  public OrderBillingAdapter(Context context, List<SeatOrderItem> data) {
    ctx = context;
    orderData = data != null ? data : new ArrayList<>();
    priceFormat = ctx.getString(R.string.format_price);
  }

  public BillingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view = LayoutInflater.from(ctx).inflate(R.layout.bill_item, parent, false);
    return new BillingViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BillingViewHolder holder, final int position) {
    SeatOrderItem foodItem = orderData.get(position);
    Item item = foodItem.getItem();
    holder.foodTitle.setText(item.getTitle());
    String notes = item.getDescription();
    if (notes != null && !notes.isEmpty()) {
      holder.foodNotes.setText(notes);
      holder.foodNotes.setVisibility(View.VISIBLE);
    } else {
      holder.foodNotes.setVisibility(View.GONE);
    }

    List<SizePrice> prices = item.getSizePrices();
    if (prices.size() > 0) {
      holder.unitPrice.setText(String.format(priceFormat, prices.get(0).getPrice()));
      double total = prices.get(0).getPrice().doubleValue() * foodItem.getItemsCount();
      holder.totalPrice.setText(String.format(priceFormat, total));
    }
//        if (foodItem.getLogo() != null && foodItem.getLogo().getNoResolution() != null) {
//            holder.foodImage.setImageResource(R.color.white);
//            ImageData image = ImageData_.getInstance_(mContext).from(foodItem.getLogo().getNoResolution().getUrl());
//            image.getImage(mContext, holder.foodImage);
//        } else {
//            holder.foodImage.setImageResource(R.color.white);
//        }
    holder.foodCount.setText(String.format(ctx.getString(com.rockspoon.rockpos.R.string.format_order_items_count_format), foodItem.getItemsCount()));
    holder.foodImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.test_food_image));
  }

  @Override
  public int getItemCount() {
    return orderData.size();
  }
}
