package com.rockspoon.models.venue.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.layout.ProductionArea;
import com.rockspoon.models.venue.layout.ProductionAreaStation;

import java.util.Date;
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
public class Device {

  @JsonProperty("deviceId")
  private String id;
  private DeviceType type;
  private DeviceStatus status;
  private String queue;
  private String queueHost;
  private String pin;
  private String uid;
  private String name;
  private String version;
  private String preferredNetwork;
  private Map<String, String> diagnostics;
  private Date activatedAt;
  private Date lastContact;
  private Date lastFailure;
  private Map<String, String> metadata;
  private ProductionArea productionArea;
  private ProductionAreaStation station;
  private ProductionAreaStation substation;
}
