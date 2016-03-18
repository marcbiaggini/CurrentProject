package com.rockspoon.models.venue.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.ports.DayOfWeek;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyAvailability implements Serializable {
  @JsonProperty("dailyAvailabilityId")
  private String id;
  private boolean enabled;
  private Boolean closedAllDay;
  private DayOfWeek dayOfWeek;
  private List<Object> time;
}
