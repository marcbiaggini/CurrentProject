package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DinerSessionEventHistory {

  @JsonProperty("dinerSessionEventHistoryId")
  private Long id;
  private Timestamp created;
  private DiningParty diningParty;
  private DinerSession dinerSession;
  private DinerSessionEvent eventType;
}
