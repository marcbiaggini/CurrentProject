package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.OrderItem;
import com.rockspoon.rockandui.Components.FaceName;
import com.rockspoon.rockandui.Interfaces.OnAllSeatsCheckedChange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lucas on 04/08/15.
 */
public class OrderItemSeatAdapter extends LinearGridViewAdapter<DinerSession> {

  private static final int COLUMNS_COUNT = 4;

  private final Context ctx;
  private List<Integer> selectedSeats = new ArrayList<>();
  private final List<DinerSession> sessions = new ArrayList<>();
  private final List<OnAllSeatsCheckedChange> onAllSeatsCheckedChange = new ArrayList<>();
  private boolean isAllSeatsSelected = false;

  public OrderItemSeatAdapter(Context ctx) {
    super(ctx, COLUMNS_COUNT);
    this.ctx = ctx;
  }

  public OrderItemSeatAdapter(Context ctx, List<DinerSession> sessions) {
    this(ctx);
    addAllStableId(sessions);
    this.sessions.addAll(sessions);
    Collections.sort(this.sessions, (lhs, rhs) -> Integer.compare(lhs.getSeatNumber(), rhs.getSeatNumber()));
  }

  public void setSelected(int position, boolean selected) {
    if (selected) {
      selectedSeats.add(sessions.get(position).getSeatNumber());
    } else {
      selectedSeats.remove(sessions.get(position).getSeatNumber());
    }

    if (!selected) {
      isAllSeatsSelected = false;
      for (OnAllSeatsCheckedChange listener : onAllSeatsCheckedChange) {
        listener.onChangeChecked(false);
      }
    }
    if (getSelectedSeats().size() == getCount()) {
      isAllSeatsSelected = true;
      for (OnAllSeatsCheckedChange listener : onAllSeatsCheckedChange) {
        listener.onChangeChecked(true);
      }
    }
    notifyDataSetChanged();
  }

  public void select(int position) {
    setSelected(position, true);
  }

  public boolean isAllSelected() {
    return isAllSeatsSelected;
  }

  public void unselectAllSeats() {
    isAllSeatsSelected = false;
    selectedSeats.clear();
    for (OnAllSeatsCheckedChange listener : onAllSeatsCheckedChange) {
      listener.onChangeChecked(false);
    }
    notifyDataSetChanged();
  }

  public void selectAllSeats() {
    isAllSeatsSelected = true;
    for (DinerSession session : sessions) {
      selectedSeats.add(session.getSeatNumber());
    }
    for (OnAllSeatsCheckedChange listener : onAllSeatsCheckedChange) {
      listener.onChangeChecked(true);
    }
    notifyDataSetChanged();
  }

  public void registerOnAllSeatsCheckedChange(final OnAllSeatsCheckedChange listener) {
    this.onAllSeatsCheckedChange.add(listener);
  }

  public void unregisterOnAllSeatsCheckedChange(final OnAllSeatsCheckedChange listener) {
    this.onAllSeatsCheckedChange.remove(listener);
  }

  public List<Pair<Integer, DinerSession>> getSelectedSeats() {
    List<Pair<Integer, DinerSession>> selectedSessions = new ArrayList<>();
    for (DinerSession session : sessions) {
      if (selectedSeats.contains(session.getSeatNumber())) {
        selectedSessions.add(new Pair<>(sessions.indexOf(session), session));
      }
    }
    return selectedSessions;
  }

  public Set<OrderItem> getOrderItems() {
    Set<OrderItem> orderItemsSet = new HashSet<>();
    for (DinerSession session : sessions) {
      if (selectedSeats.contains(session.getSeatNumber())) {
        orderItemsSet.add(new OrderItem(null, true, session, null));
      }
    }
    return orderItemsSet;
  }

  @Override
  public boolean itemIsSelected(int position) {
    return selectedSeats.contains(sessions.get(position).getSeatNumber());
  }

  @Override
  public int getCount() {
    return sessions.size();
  }

  @Override
  public DinerSession getItem(int pos) {
    return sessions.get(pos);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup root) {
    FaceName v = (convertView != null) ? (FaceName) convertView : new FaceName(ctx);
    DinerSession session = sessions.get(position);

    v.setText(String.format("Seat #%s", session.getSeatNumber()));
//    v.setImageDrawable(session.getImage(ctx, v.getImage())); //will be implemented later
    v.setChecked(itemIsSelected(position));
    v.setOnClickListener((view) -> {
      setSelected(position, v.isChecked());
      if (!v.isChecked()) {
        isAllSeatsSelected = false;
        for (OnAllSeatsCheckedChange listener : onAllSeatsCheckedChange) {
          listener.onChangeChecked(false);
        }
      }
    });

    return v;
  }
}
