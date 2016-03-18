package com.rockspoon.rockpos.Ordering;

import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;

/**
 * Created by greenfrvr
 */
public interface OrderingActions {

  void order(ItemInstanceRequest request);

  void orderAndContinue(ItemInstanceRequest request);

  void orderAndDuplicate(ItemInstanceRequest request);

}
