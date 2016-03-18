package com.rockspoon.models.venue.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 18/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateDeviceGCMTokenRequest {
  private String token;
}
