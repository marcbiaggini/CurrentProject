package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.item.ItemSummary;
import com.rockspoon.rockandui.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by greenfrvr
 */
public class OrderedItemsView extends LinearLayout {

  private List<ItemSummary> summaries;
  private OrderedItemListener listener;
  private HashMap<Long, CheckBox> checkBoxMap = new HashMap<>();

  public OrderedItemsView(Context context) {
    this(context, null, 0);
  }

  public OrderedItemsView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OrderedItemsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOrientation(VERTICAL);
    setShowDividers(SHOW_DIVIDER_MIDDLE);
    setDividerDrawable(ContextCompat.getDrawable(context, R.drawable.divider_hor));
    setDividerPadding(context.getResources().getDimensionPixelOffset(R.dimen.order_cart_items_divider));
  }

  public void setSummaries(List<ItemSummary> summaries) {
    this.summaries = summaries;
    getViewTreeObserver().addOnPreDrawListener(preDrawListener);
  }

  public void setOrderItemListener(OrderedItemListener listener) {
    this.listener = listener;
  }

  private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
    @Override
    public boolean onPreDraw() {
      composeView();
      getViewTreeObserver().removeOnPreDrawListener(this);
      return true;
    }
  };

  private void composeView() {
    removeAllViews();

    for (ItemSummary summary : summaries) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.summary_simple_item, this, false);

      TextView titleView = (TextView) view.findViewById(R.id.summary_item_title);
      titleView.setText(summary.getName());

      TextView descrView = (TextView) view.findViewById(R.id.summary_item_description);
      descrView.setText(summary.getModifiersDescription());

      setListeners(view, summary.getItemInstanceId());
      addView(view);
    }
  }

  private void setListeners(View view, Long id) {
    view.setOnClickListener(v -> {
      if (listener != null) {
        listener.onItemClicked(id);
      }
    });

    CheckBox checkBox = (CheckBox) view.findViewById(R.id.summary_item_checkbox);
    checkBoxMap.put(id, checkBox);
    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (listener != null) {
        listener.onItemChecked(isChecked, id);
      }
    });
  }

  public void setChecked(Boolean isChecked) {
    for (Long itemId : checkBoxMap.keySet()) {
      checkBoxMap.get(itemId).setChecked(isChecked);
    }
  }

  public interface OrderedItemListener {
    void onItemClicked(Long instanceId);

    void onItemChecked(boolean isChecked, Long instanceId);
  }

}
