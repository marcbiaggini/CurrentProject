package com.rockspoon.rockandui.TestDataModel;

import com.rockspoon.models.venue.item.Item;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Yury Minich on 1/21/16.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatOrderItem implements Serializable {
  private long itemId;
  private int itemsCount;
  private int seatNumber;
  private int splitByCount = 1;
  private Item item;

  public SeatOrderItem(long id, int count, Item item, int seat) {
    itemId = id;
    itemsCount = count;
    seatNumber = seat;
    this.item = item;
  }

  public SeatOrderItem(SeatOrderItem item) {
    itemsCount = item.getItemsCount();
    seatNumber = item.getSeatNumber();
    this.item = item.getItem();
  }
}
