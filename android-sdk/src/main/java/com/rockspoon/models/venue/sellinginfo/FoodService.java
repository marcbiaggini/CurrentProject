package com.rockspoon.models.venue.sellinginfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class FoodService implements Serializable {

  @JsonProperty("foodServiceId")
  private Long id;
  private FoodServiceType type;
  private BigDecimal[] area;
  private Object estimatedTime;
  private BigDecimal minimumFee;
  private BigDecimal fee;
  private Boolean sameAsTakeout;

  public enum FoodServiceType {
    delivery,
    takeout,
    catering,
    dinein
  }
}
