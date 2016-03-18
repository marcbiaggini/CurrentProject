package com.rockspoon.rockandui.Objects;

import java.math.BigDecimal;

/**
 * Created by lucas on 03/08/15.
 */
public class RadioModifierData {
  private final String name;
  private final BigDecimal price;
  private boolean selected = false;

  public RadioModifierData(String name) {
    this(name, BigDecimal.ZERO);
  }

  public RadioModifierData(String name, BigDecimal price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public boolean getSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public BigDecimal getPrice() {
    return price;
  }
}
