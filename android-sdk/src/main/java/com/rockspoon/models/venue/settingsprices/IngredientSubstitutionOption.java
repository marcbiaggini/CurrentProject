package com.rockspoon.models.venue.settingsprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.ingredient.Ingredient;

import java.io.Serializable;

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
public class IngredientSubstitutionOption implements Serializable {

  @JsonProperty("ingredientSubstitutionOptionId")
  private Long id;
  private Short index;
  private Ingredient ingredient;
}
