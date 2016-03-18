package com.rockspoon.models.venue.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
public class DiningSpotsListRequest {

  @JsonProperty("diningSectionIds")
  private List<Long> ids;

}
