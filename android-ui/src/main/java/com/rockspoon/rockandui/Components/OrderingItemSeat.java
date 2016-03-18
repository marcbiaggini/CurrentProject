package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.rockandui.Adapters.OrderItemSeatAdapter;
import com.rockspoon.rockandui.Interfaces.OnAllSeatsCheckedChange;
import com.rockspoon.rockandui.Interfaces.OnSelectedChanged;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by lucas on 05/08/15.
 */
public class OrderingItemSeat extends LinearLayout {

  private final LinearGridView itemGrid;
  private final ToggleButton allSeatsButton;
  private OnSelectedChanged onSelectedChanged;
  private final OnAllSeatsCheckedChange onAllSeatsCheckedChange = new OnAllSeatsCheckedChange() {
    @Override
    public void onChangeChecked(boolean checked) {
      allSeatsButton.setChecked(checked);
      if (onSelectedChanged != null)
        onSelectedChanged.onChanged();
    }
  };
  private OrderItemSeatAdapter adapter;

  public OrderingItemSeat(final Context ctx) {
    this(ctx, null);
  }

  public OrderingItemSeat(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public OrderingItemSeat(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs);

    inflate(ctx, R.layout.ordering_item_seat, this);

    int padding = ctx.getResources().getDimensionPixelOffset(R.dimen.ordering_item_seat_padding);
    setPadding(padding, padding, padding, padding);
    setOrientation(VERTICAL);

    itemGrid = (LinearGridView) findViewById(R.id.ordering_item_seat_itemgrid);
    allSeatsButton = (ToggleButton) findViewById(R.id.ordering_item_seat_allseatsbtn);

    allSeatsButton.setOnClickListener((view) -> {
      if (adapter != null) {
        if (allSeatsButton.isChecked()) {
          adapter.selectAllSeats();
        } else {
          adapter.unselectAllSeats();
        }
      }
    });

    itemGrid.setOnItemClickListener((v, position, item) -> {
      adapter.setSelected(position, ((FaceName) v).isChecked());
      if (onSelectedChanged != null) {
        onSelectedChanged.onChanged();
      }
    });
  }

  public List<Pair<Integer, DinerSession>> getSelectedSeats() {
    return getAdapter().getSelectedSeats();
  }

  public void setOnSelectedChanged(OnSelectedChanged listener) {
    this.onSelectedChanged = listener;
  }

  public void toggleButton() {
    allSeatsButton.toggle();
  }

  public OrderItemSeatAdapter getAdapter() {
    return (OrderItemSeatAdapter) itemGrid.getAdapter();
  }

  public void setAdapter(@NonNull OrderItemSeatAdapter adapter) {
    if (this.adapter != null) {
      this.adapter.unregisterOnAllSeatsCheckedChange(onAllSeatsCheckedChange);
    }
    this.adapter = adapter;
    itemGrid.setAdapter(adapter);
    adapter.registerOnAllSeatsCheckedChange(onAllSeatsCheckedChange);
  }

  public void onDestroy() {
    if (adapter != null) {
      adapter.unregisterOnAllSeatsCheckedChange(onAllSeatsCheckedChange);
    }
  }
}
