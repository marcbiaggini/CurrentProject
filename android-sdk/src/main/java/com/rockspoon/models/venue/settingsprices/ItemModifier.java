package com.rockspoon.models.venue.settingsprices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemMixedModifierOption;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemModifierOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class ItemModifier implements Serializable {

  @JsonProperty("itemModifierId")
  private Long id;
  private String title;
  private String abbreviation;
  private ItemModifierType type;
  private Boolean mandatory;
  private String forFree;
  private Boolean hasDefault;
  private Set<ItemModifierOption> options;
  private List<ItemModifierCustomPrices> customPrices;
  private Short maxFreeModifiers;
  private Short maxModifiers;
  private Boolean singleModifier;
  private Boolean repeatModifier;

  @JsonIgnore
  @NonFinal
  private List<Long> selectedOptions;

  @JsonIgnore
  @NonFinal
  private List<ItemInstanceDetailsItemMixedModifierOption> mixedModifierOptions;

  public List<ItemInstanceDetailsItemMixedModifierOption> getMixedModifierOptions() {
    if (mixedModifierOptions == null) {
      mixedModifierOptions = new ArrayList<>();
    }
    return mixedModifierOptions;
  }

  @JsonIgnore
  public List<ItemInstanceDetailsItemMixedModifierOption> getFullMixedModifierOptions() {
    List<ItemInstanceDetailsItemMixedModifierOption> options = new ArrayList<>();
    for (ItemInstanceDetailsItemMixedModifierOption option : mixedModifierOptions) {
      if (option.getQuantity() == 0 || (option.getQuantity() == 1 && option.getIsOnTheSide())) {
        options.add(option);
      } else {
        for (int i = 0; i < option.getQuantity() - 1; i++) {
          options.add(option);
        }
      }
    }
    return options;
  }

  public List<Long> getSelectedOptions() {
    if (selectedOptions == null) {
      selectedOptions = new ArrayList<>();
    }
    return selectedOptions;
  }

  @JsonIgnore
  public List<ItemInstanceDetailsItemModifierOption> getFullIngredientOptions() {
    List<ItemInstanceDetailsItemModifierOption> options = new ArrayList<>();
    for (Long id : selectedOptions) {
      options.add(new ItemInstanceDetailsItemModifierOption(id));
    }
    return options;
  }
}
