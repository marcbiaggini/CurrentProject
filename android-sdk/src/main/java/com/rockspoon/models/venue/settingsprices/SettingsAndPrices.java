package com.rockspoon.models.venue.settingsprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.item.ItemCategory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
public class SettingsAndPrices implements Serializable {

  @JsonProperty("settingsAndPricesId")
  private Long id;
  private ItemCategory category;
  private boolean singleSize;
  private String chooseSizeLabel;
  private String[] sizes;
  private Set<CookingModifier> cookingModifiers;
  private Set<ItemModifier> ingredientModifiers;
  private ItemModifier mixed;
  private Set<IngredientSubstitution> substitutions;
  private Timestamp created;

  public Set<ItemModifier> getItemModifiers() {
    Set<ItemModifier> modifiers = new HashSet<>();
    modifiers.addAll(getIngredientModifiers());
    if (getMixed() != null) {
      modifiers.add(getMixed());
    }
    return modifiers;
  }
}
