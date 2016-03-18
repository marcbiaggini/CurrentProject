package com.rockspoon.rockandui.TestDataModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class TableOrder implements Serializable{

    private long orderId;
    private String tableNumber;
    private List<SeatOrder> seatOrderList;

    public TableOrder(TableOrder order) {
        this.orderId = order.getOrderId();
        this.tableNumber = order.getTableNumber();
        this.seatOrderList = new ArrayList<>();
        this.seatOrderList.addAll(order.getSeatOrderList());
    }
}
