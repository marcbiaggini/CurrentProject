package com.rockspoon.models.venue.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceType {
  @JsonProperty("value")
  private String id;
  private String label;
  private Boolean needsLocation;
  private Boolean needsStation;
  private Boolean needsSubstation;
}
