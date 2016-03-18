package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.item.ItemSummary;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockandui.Components.OrderingCustomersView;
import com.rockspoon.rockpos.R;

/**
 * Created by greenfrvr
 */
public class MembersGroupHolder extends OrderViewHolder<ListCartDetails> {

  private TextView itemTitleView;
  private TextView itemDescrView;
  private CheckBox checkBox;
  private OrderingCustomersView customersView;

  private MembersGroupListener listener;

  public MembersGroupHolder(View itemView) {
    super(itemView);
    itemTitleView = (TextView) itemView.findViewById(R.id.summary_item_title);
    itemDescrView = (TextView) itemView.findViewById(R.id.summary_item_description);
    checkBox = (CheckBox) itemView.findViewById(R.id.summary_item_checkbox);
    customersView = (OrderingCustomersView) itemView.findViewById(R.id.customers_group);
  }

  @Override
  void bind(ListCartDetails details) {
    customersView.setCustomers(details.getDinerSessions());

    ItemSummary summary = details.getItemsSummary().get(0);
    itemTitleView.setText(summary.getName());
    itemDescrView.setText(summary.getModifiersDescription());
    itemView.setOnClickListener(v -> {
      if (listener != null) {
        listener.onClick(summary.getItemInstanceId());
      }
    });

    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (listener != null) {
        listener.onItemChecked(isChecked, summary.getItemInstanceId());
      }
    });
  }

  public void setChecked(Boolean isSelected) {
    checkBox.setSelected(isSelected);
  }

  public void setMembersGroupListener(MembersGroupListener listener) {
    this.listener = listener;
  }

  public interface MembersGroupListener {
    void onClick(Long instanceId);

    void onItemChecked(boolean isChecked, Long instanceId);
  }

}
