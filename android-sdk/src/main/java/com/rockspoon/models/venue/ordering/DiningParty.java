package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.models.venue.layout.DiningSectionSpot;

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
public class DiningParty {

  @JsonProperty("diningPartyId")
  private Long id;
  private String name;
  private DiningPartyStatus status;
  private Employee waiter;

  private List<DiningSectionSpot> layoutSpots;
  private List<DinerSession> dinerSessions;
  private Venue venue;
  private boolean canClose;
}
