package com.rockspoon.models.venue.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

@Wither
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class DiningSectionSpot implements Serializable {

  @JsonProperty("diningSectionSpotId")
  private Long id;
  private int number;
  private SpotStatus status;
  private SpotType type;
  private SpotShape shape;
  private int seats;
  private int maximumCapacity;
  private Timestamp created;
  private Map<String, Object> metadata;
}
