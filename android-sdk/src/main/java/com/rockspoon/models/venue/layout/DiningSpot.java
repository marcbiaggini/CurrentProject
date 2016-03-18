package com.rockspoon.models.venue.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by greenfrvr
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiningSpot {
  @JsonProperty("diningSectionSpotId")
  private Long id;
  private Long diningPartyId;
  private int number;
  private int seats;
  private int maximumCapacity;
  private SpotStatus status;

}
