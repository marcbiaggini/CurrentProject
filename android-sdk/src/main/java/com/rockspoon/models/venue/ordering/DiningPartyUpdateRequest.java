package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 10/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiningPartyUpdateRequest implements Serializable {

  private String name;
  private List<Long> diningSectionSpotIdsToRemove;
  private List<Long> diningSectionSpotIdsToAdd;
  private List<DinerSession> dinersToRemove;
  private List<DinerSession> dinersToUpdate;
  private List<DinerSession> dinersToAdd;
  private DiningPartyStatus status;
}
