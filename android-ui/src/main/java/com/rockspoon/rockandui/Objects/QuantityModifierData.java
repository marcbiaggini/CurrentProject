package com.rockspoon.rockandui.Objects;

/**
 * Created by lucas on 03/08/15.
 */
public class QuantityModifierData {
  private final String name;
  private int quantity = 0;

  public QuantityModifierData(String name) {
    this(name, 0);
  }

  public QuantityModifierData(String name, int quantity) {
    this.name = name;
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
