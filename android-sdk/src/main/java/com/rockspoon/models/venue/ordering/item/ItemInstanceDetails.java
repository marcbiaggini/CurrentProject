package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemInstanceDetails implements Serializable {
  private static final long serialVersionUID = 1L;
  private String size;
  private Long menuCategoryId;
  private ItemInstanceDetailsCookingModifiersConfiguration cookingModifiersConfiguration;
  private ItemInstanceDetailsSubstitutionModifierConfiguration substitutionsConfiguration;
  private ItemInstanceDetailsItemModifierConfiguration ingredientModifiersConfiguration;
  private ItemInstanceDetailsItemMixedModifierConfiguration mixedConfiguration;
  private ItemInstanceDetailsCustomModifierConfiguration customModifiersConfiguration;

}
