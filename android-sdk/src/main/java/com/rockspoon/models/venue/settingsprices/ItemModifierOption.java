package com.rockspoon.models.venue.settingsprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.ingredient.Ingredient;
import com.rockspoon.models.venue.ingredient.Side;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

/**
 * Created by lucas on 02/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemModifierOption implements Serializable {
  @JsonProperty("itemModifierOptionId")
  private Long id;
  private Boolean showInMenu;
  private Boolean notAvailable;
  private Ingredient ingredient;
  private Boolean isDefault;
  private Boolean isExtra;
  private Boolean isRemove;
  private Boolean isOnTheSide;
  private Object prices;

}
