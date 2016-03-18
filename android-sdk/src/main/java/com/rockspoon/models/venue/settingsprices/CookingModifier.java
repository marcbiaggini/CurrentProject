package com.rockspoon.models.venue.settingsprices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class CookingModifier implements Serializable {
  @JsonProperty("cookingModifierId")
  private Long id;
  private String title;
  private String abbreviation;
  private Boolean mandatory;
  private Boolean hasDefault;
  private List<Map<String, Object>> options;

  @JsonIgnore
  @NonFinal
  private Map<Long, String> selectedOptions;

  public void setSelectedOptions(Map<Long, String> selectedOptions) {
    this.selectedOptions = selectedOptions;
  }

  public Map<Long, String>  getSelectedOptions() {
    if (selectedOptions == null) {
      selectedOptions = new HashMap<>();
    }
    return selectedOptions;
  }

}
