package com.rockspoon.rockandui.TestDataModel;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Yury Minich on 2/1/16.
 */
@Getter
@Setter
@AllArgsConstructor
public class CustomSplitData {
  private int payerNumber;
  private boolean isPayed;
  private BigDecimal payAmount;
  private BigDecimal tax;
  private boolean isCustomAmount;

  public CustomSplitData(int number, BigDecimal amount, BigDecimal tax) {
    payerNumber = number;
    payAmount = amount;
    this.tax = tax;
    isCustomAmount = false;
    isPayed = false;
  }

}
