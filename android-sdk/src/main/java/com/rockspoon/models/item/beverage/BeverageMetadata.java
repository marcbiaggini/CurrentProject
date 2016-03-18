package com.rockspoon.models.item.beverage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.item.ItemProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 02/02/16.
 */

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeverageMetadata {

  private ItemProperty waterType;
  private ItemProperty sodaType;
  private ItemProperty juiceType;
  private ItemProperty[] fruitType;
  private ItemProperty[] fruit;

  private boolean eligibleForRefil;
}
