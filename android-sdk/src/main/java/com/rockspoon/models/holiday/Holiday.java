package com.rockspoon.models.holiday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.ports.DayOfWeek;
import com.rockspoon.models.ports.Month;
import com.rockspoon.models.venue.Venue;

import java.io.Serializable;

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
public class Holiday implements Serializable {
  @JsonProperty("holidayId")
  private String id;
  private String name;
  private HolidayType type;
  private Short day;
  private Month month;
  private DayOfWeek dayOfWeek;
  private DayOfWeekCount dayOfWeekCount;
  private String country;
  private String region;
  private Venue venue;

  public enum HolidayType {
    ABSOLUTE_DATE,
    RELATIVE_DATE
  }

  public enum DayOfWeekCount {
    First,
    Second,
    Third,
    Fourth,
    Last
  }
}
