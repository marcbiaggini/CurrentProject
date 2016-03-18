package com.rockspoon.models.credentials;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImmutableCredentials implements Serializable {
  private String userName;
  private Long employeeId;
  private String userPassword;
  private boolean keepLogged;
  private String deviceUID;

  // TODO: Remove, added for compatibility
  @Deprecated
  public ImmutableCredentials(String userName, String userPassword, String deviceUID) {
    this(userName, null, userPassword, true, null);
  }
}
