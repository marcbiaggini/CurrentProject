package com.rockspoon.rockpos.Ordering;

import com.rockspoon.rockandui.Components.RSActionFragment;
import com.rockspoon.rockpos.NavigationActivity;

/**
 * Created by greenfrvr
 */
public abstract class BaseOrderingFragment extends RSActionFragment {

  //NOTICE: NEVER TRY TO RETRIEVE ORDER BUNDLE EARLIER THAN onActivityCreated()
  public OrderBundle getOrder() {
    return ((NavigationActivity) getActivity()).getOrderBundle();
  }

  public Integer getTableNumber() {
    return getOrder().tableNumber;
  }

  public Integer getSeatNumber() {
    return getOrder().seatNumber;
  }

  public boolean isAllSeatsSelected() {
    return getOrder().isAllSelected;
  }

  public boolean isValidSeatNumber() {
    return getOrder().seatNumber > -1;
  }

  public String getTableName() {
    return getOrder().getTableName();
  }

}
