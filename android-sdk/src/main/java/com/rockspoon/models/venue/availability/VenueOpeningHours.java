package com.rockspoon.models.venue.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
public class VenueOpeningHours implements Serializable {

  private Set<DailyAvailability> openingHours;
  private Set<HolidayAvailability> anualClosures;
  private List<HolidayAvailability> orderedAnualClosures;
  private Set<ScheduledClosure> scheduledClosures;

}
