package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Components.OrderedItemsView;
import com.rockspoon.rockpos.R;

/**
 * Created by greenfrvr
 */
public class MemberCartHolder extends OrderViewHolder<ListCartDetails> {

  private TextView seatView;
  private TextView nameView;
  private RoundImageView avatarView;
  private OrderedItemsView orderedItemsView;

  public MemberCartHolder(View itemView) {
    super(itemView);
    seatView = (TextView) itemView.findViewById(R.id.customer_seat);
    nameView = (TextView) itemView.findViewById(R.id.customer_name);
    avatarView = (RoundImageView) itemView.findViewById(R.id.customer_avatar);
    orderedItemsView = (OrderedItemsView) itemView.findViewById(R.id.customer_ordered_items);
  }

  @Override
  void bind(ListCartDetails details) {
    DinerSession session = details.getDinerSessions().get(0);
    seatView.setText(String.format("Seat #%s", session.getSeatNumber()));
    nameView.setText(String.format("Customer #%s", session.getId()));
    orderedItemsView.setSummaries(details.getItemsSummary());
  }

  public void setChecked(Boolean isChecked) {
    orderedItemsView.setChecked(isChecked);
  }

  public void setOrderItemListener(OrderedItemsView.OrderedItemListener listener) {
    orderedItemsView.setOrderItemListener(listener);
  }
}
