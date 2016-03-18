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
public class ItemInstanceDetailsItemMixedModifierConfiguration implements Serializable {
  private static final long serialVersionUID = 1L;
  private ItemInstanceDetailsItemMixedModifierSelection itemMixedModifierSelection;
}
