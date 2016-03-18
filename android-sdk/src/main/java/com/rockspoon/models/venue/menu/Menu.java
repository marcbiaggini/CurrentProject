package com.rockspoon.models.venue.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.availability.DailyAvailability;
import com.rockspoon.models.venue.sellinginfo.FoodService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Menu {
  @JsonProperty("menuId")
  private Long id;
  private String title;
  private String description;
  private Boolean alwaysAvailable;
  private List<MenuCategory> categories;
  private FoodService.FoodServiceType[] availableFor;
  private Set<DailyAvailability> dailyAvailability;
  private Timestamp created;
  private MenuStatus status;

}
