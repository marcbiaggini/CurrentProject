package com.rockspoon.rockandui.TestDataModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Yury Minich on 1/15/16.
 */
// test data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentData implements Serializable {
  public final static String EXTRA_ORDER_DATA = "order_data";
  public static final BigDecimal TAX_PERCENT = BigDecimal.valueOf(0.09);
  public static final int SCALE = 2;

  private String tableNumber;
  private int payerId;
  private BigDecimal billSubtotal;
  private BigDecimal orderSubtotal;
  private CreditCard creditCard;
  private OrderSubPaymentData creditCardPayment;
  private OrderSubPaymentData cashPayment;

  public OrderPaymentData(String tableNumber, BigDecimal billSubtotal, BigDecimal orderSubtotal) {
    this.tableNumber = tableNumber;
    this.billSubtotal = billSubtotal;
    this.orderSubtotal = orderSubtotal;
  }

  public OrderPaymentData(int payerId, String tableNumber, BigDecimal billSubtotal, BigDecimal orderSubtotal) {
    this.payerId = payerId;
    this.tableNumber = tableNumber;
    this.billSubtotal = billSubtotal;
    this.orderSubtotal = orderSubtotal;
  }

  private BigDecimal tips = BigDecimal.ZERO;

  public BigDecimal getBillTax() {
    return billSubtotal.multiply(TAX_PERCENT).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal getOrderTax() {
    return orderSubtotal.multiply(TAX_PERCENT).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal getOrderTotal() {
    return orderSubtotal.add(getOrderTax().add(tips)).setScale(OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal getOrderTotalWithoutTips() {
    return orderSubtotal.add(getOrderTax()).setScale(OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal getBillTotal() {
    return billSubtotal.add(getBillTax().add(tips)).setScale(OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal getBillTotalWithoutTips() {
    return billSubtotal.add(getBillTax()).setScale(OrderPaymentData.SCALE, BigDecimal.ROUND_HALF_UP);
  }

  public boolean isPaid() {
    return orderSubtotal.compareTo(BigDecimal.ZERO) == 0;
  }

  public void orderPartPayed(BigDecimal payedAmount) {
    if (payedAmount.compareTo(getBillTotal()) >= 0) {
      setOrderSubtotal(BigDecimal.ZERO);
      setBillSubtotal(BigDecimal.ZERO);
    } else if (payedAmount.compareTo(getOrderTotal()) >= 0) {
      setOrderSubtotal(BigDecimal.ZERO);
    } else {
      BigDecimal nonPaidOrder = getOrderTotal().subtract(payedAmount);
      BigDecimal nonPaidBill = getBillTotal().subtract(payedAmount);
      setOrderSubtotal(nonPaidOrder.divide(BigDecimal.ONE.add(OrderPaymentData.TAX_PERCENT), RoundingMode.HALF_UP));
      setBillSubtotal(nonPaidBill.divide(BigDecimal.ONE.add(OrderPaymentData.TAX_PERCENT), RoundingMode.HALF_UP));
    }
  }

}
