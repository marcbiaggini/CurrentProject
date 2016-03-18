package com.rockspoon.models.venue.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Network implements Serializable {

  private static final long serialVersionUID = -3849835925545400959L;
  @JsonProperty("networkId")
  private String id;
  private String ssid;
  private String type;
  private Boolean privateNetwork;
  private Map<String, String> settings;
}
