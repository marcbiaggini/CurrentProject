package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by greenfrvr
 */
public class OrderingCustomersView extends LinearLayout {

  private List<DinerSession> sessions;

  public OrderingCustomersView(Context context) {
    this(context, null, 0);
  }

  public OrderingCustomersView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OrderingCustomersView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setOrientation(HORIZONTAL);
  }

  public void setCustomers(List<DinerSession> sessions) {
    this.sessions = sessions;
    getViewTreeObserver().addOnPreDrawListener(preDrawListener);
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

    for (DinerSession session : sessions) {
      View view = LayoutInflater.from(getContext()).inflate(R.layout.customers_list_item, this, false);

      TextView seatView = (TextView) view.findViewById(R.id.customer_seat);
      seatView.setText(String.format("#%s", session.getSeatNumber()));

//      RoundImageView descrView = (RoundImageView) view.findViewById(R.id.customer_avatar); //will be used later

      addView(view);
    }
  }

}
