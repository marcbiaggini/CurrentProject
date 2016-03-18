package com.rockspoon.models.venue.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
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
public class ProductionAreaStation implements Serializable {
  @JsonProperty("productionAreaStationId")
  private Long id;
  private String name;
  private ProductionAreaStation parent;
  private Set<ProductionAreaStation> substations;
  private ProductionArea productionArea;
}
