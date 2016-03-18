package com.rockspoon.rockandui.TestDataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Yury Minich on 1/21/16.
 */
@Getter
@Setter
@NoArgsConstructor
public class SeatOrder implements Serializable{
    private int seatNumber;
    private boolean isPaid = false;
    private List<SeatOrderItem> seatOrderItemList;

    public SeatOrder(int seat, List<SeatOrderItem> seatItems) {
        this.seatNumber = seat;
        this.seatOrderItemList = new ArrayList<>();
        this.seatOrderItemList.addAll(seatItems);
    }
}
