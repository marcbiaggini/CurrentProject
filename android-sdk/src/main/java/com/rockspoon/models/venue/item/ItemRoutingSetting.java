package com.rockspoon.models.venue.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.layout.ProductionArea;
import com.rockspoon.models.venue.layout.ProductionAreaStation;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

/**
 * Created by lucas on 02/02/16.
 */
@Wither
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemRoutingSetting implements Serializable {

  @JsonProperty("itemRoutingSettingId")
  private Long id;
  private ProductionAreaStation station;
  private ProductionArea productionArea;

}
