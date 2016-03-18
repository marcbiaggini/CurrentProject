package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.hr.Employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by juancamilovilladuarte on 3/3/16.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductionAreaStationFiredItem {

  @JsonProperty("productionAreaStationFiredItemId")
  private Long id;
  private ProductionAreaStationFiredItemStatus status;
  private ProductionAreaStation productionAreaStation;
  private Employee employee;

  public ProductionAreaStationFiredItem makeClone() {
    return new ProductionAreaStationFiredItem(id, status, productionAreaStation, employee);
  }
}
