package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.rockandui.DragComponents.DragItemAdapter;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.TestDataModel.SeatOrderItem;

import java.util.List;

/**
 * Created by Yury Minich on 1/14/16.
 */
public class SplitOrderItemsAdapter extends DragItemAdapter<SeatOrderItem, SplitOrderItemsAdapter.ViewHolder> {

  private final Context ctx;
  private ItemsListEventsListener eventsListener;
  private int seatNumber;
  private boolean isPaid = false;
  private int splitType;

  public static final int SPLIT_TYPE_EQUALLY = 0;
  public static final int SPLIT_TYPE_BY_PERSON_ORDERED = 1;

  public SplitOrderItemsAdapter(Context context, List<SeatOrderItem> data, int seat, int splitType, ItemsListEventsListener listener, boolean dragOnLongPress) {
    super(dragOnLongPress);
    ctx = context;
    seatNumber = seat;
    this.splitType = splitType;
    this.eventsListener = listener;
    setHasStableIds(true);
    setItemList(data);
  }

  public void setPaid(boolean isPaid) {
    this.isPaid = isPaid;
    setDragEnabled(!isPaid);
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.split_bill_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);

    SeatOrderItem item = mItemList.get(position);
    Item foodItem = item.getItem();
    holder.foodTitle.setText(foodItem.getTitle());

    if (item.getSplitByCount() > 1) {
      // Item is splitted
      if (item.getItemsCount() % item.getSplitByCount() == 0) {
        // Items count is integer number
        holder.foodCount.setText(String.format(ctx.getString(R.string.format_order_items_count_format), item.getItemsCount() / item.getSplitByCount()));
      } else {
        //  Items count is fraction number
        String itemsCount = asFraction(item.getItemsCount(), item.getSplitByCount());
        holder.foodCount.setText(itemsCount);
      }
    } else {
      //  Item not splitted
      holder.splitItem.setVisibility(View.VISIBLE);
      holder.foodCount.setText(String.format(ctx.getString(R.string.format_order_items_count_format), item.getItemsCount()));
    }

    double total = 0;
    Item dish = item.getItem();

    if (dish.getPrices() != null && !dish.getPrices().isEmpty()) {
      SizePrice price = dish.getSizePrices().get(0);
      int splitBy = item.getSplitByCount();
      total += price.getPrice().doubleValue() * item.getItemsCount() / splitBy;
    }

    String totalText = String.format(ctx.getString(R.string.format_price), total);
    holder.totalPrice.setText(totalText);
    holder.foodImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.test_food_image));

    if (splitType == SPLIT_TYPE_EQUALLY) {
      holder.splitItem.setVisibility(View.GONE);
    } else if (splitType == SPLIT_TYPE_BY_PERSON_ORDERED) {
      holder.splitItem.setTag(position);
      holder.splitItem.setOnClickListener(onSplitItemClick);
    }

    if (isPaid) {
      holder.itemView.setEnabled(false);
      holder.paidOverlay.setVisibility(View.VISIBLE);
      holder.splitItem.setEnabled(false);
    } else {
      holder.itemView.setEnabled(true);
      holder.paidOverlay.setVisibility(View.GONE);
      holder.splitItem.setEnabled(true);
    }
  }

  @Override
  public long getItemId(int position) {
    return mItemList.get(position).getItemId();
  }

  public class ViewHolder extends DragItemAdapter<Pair<Long, String>, SplitOrderItemsAdapter.ViewHolder>.ViewHolder {
    public TextView foodTitle;
    public ImageView foodImage;
    public TextView foodCount;
    public TextView splitItem;
    public TextView totalPrice;
    public View paidOverlay;

    public ViewHolder(final View itemView) {
      super(itemView, R.id.item_layout);
      foodTitle = (TextView) itemView.findViewById(R.id.food_title);
      foodImage = (ImageView) itemView.findViewById(R.id.food_image);
      foodCount = (TextView) itemView.findViewById(R.id.food_count);
      splitItem = (TextView) itemView.findViewById(R.id.split_item);
      totalPrice = (TextView) itemView.findViewById(R.id.item_total);
      paidOverlay = itemView.findViewById(R.id.paid_overlay);
    }

  }

  View.OnClickListener onSplitItemClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      if (eventsListener != null) {
        eventsListener.onSplitItemClick((int) v.getTag(), seatNumber);
      }
    }
  };

  public interface ItemsListEventsListener {
    void onSplitItemClick(int position, int seatNumber);
  }

  /**
   * @return the greatest common denominator
   */
  public static long gcm(long a, long b) {
    return b == 0 ? a : gcm(b, a % b);
  }

  public static String asFraction(long a, long b) {
    long gcm = gcm(a, b);
    return (a / gcm) + "/" + (b / gcm);
  }

}


