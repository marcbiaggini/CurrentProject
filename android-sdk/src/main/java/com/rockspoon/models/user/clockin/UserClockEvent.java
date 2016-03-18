package com.rockspoon.models.user.clockin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;

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
public class UserClockEvent {
  @JsonProperty("userClockEventId")
  private String id;
  private ClockEventType eventType;
  private Timestamp when;
  private User user;
  private Venue venue;

}
