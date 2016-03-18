package com.rockspoon.models.venue.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.holiday.Holiday;

import java.io.Serializable;
import java.util.List;

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
public class HolidayAvailability implements Serializable {

  @JsonProperty("holidayAvailability4VenueId")
  private String id;
  private Holiday holiday;
  private List<Object> time;
  private Boolean enabled;
  private Boolean closedAllDay;
}
