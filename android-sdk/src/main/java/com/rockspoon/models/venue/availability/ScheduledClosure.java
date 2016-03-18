package com.rockspoon.models.venue.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 02/02/16.
 */

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduledClosure implements Serializable {
  @JsonProperty("scheduledClosureId")
  private String id;
  private boolean enabled;
  private Boolean closedAllDay;
  private Timestamp from;
  private String reason;
}
