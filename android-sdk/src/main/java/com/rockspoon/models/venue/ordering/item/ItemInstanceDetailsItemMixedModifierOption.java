package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

@Wither
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemInstanceDetailsItemMixedModifierOption implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long itemModifierOptionId;
  private Boolean isExtra;
  private Boolean isRemove;
  private Boolean isOnTheSide;

  @JsonIgnore
  @NonFinal
  int quantity = 1;

  public void setQuantity(int quantity) {
    this.quantity = quantity;
    isExtra = quantity >= 2;
    isRemove = quantity == 0;
    isOnTheSide = isOnTheSide && !isRemove;
  }

}
