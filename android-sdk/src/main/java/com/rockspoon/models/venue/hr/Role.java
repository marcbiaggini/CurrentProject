package com.rockspoon.models.venue.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.businessentity.OperatingEntity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {

  @JsonProperty("roleId")
  private String id;
  private String name;
  private String description;
  private String[] accessRights;
  private RoleStatus status;
  private OperatingEntity operatingEntity;
}
