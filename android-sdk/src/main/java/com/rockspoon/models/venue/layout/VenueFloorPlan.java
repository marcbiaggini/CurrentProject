package com.rockspoon.models.venue.layout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;

@Wither
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VenueFloorPlan implements Serializable {
  private List<DiningSection> diningSections;
  private String name;
  private Boolean isDraft;

  public static VenueFloorPlan newInstance(String name) {
    VenueFloorPlan plan = new VenueFloorPlan();
    plan.name = name;
    plan.isDraft = true;
    plan.diningSections = new ArrayList<>();

    return plan;
  }
}
