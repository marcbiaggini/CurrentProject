package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 03/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemsFired implements Serializable {

  @JsonProperty("itemsFiredId")
  private Long id;
  private Timestamp fireTime;
  private ItemsFiringType type;
  private Set<ItemSummaryForKitchen> itemsInstances;
  private Timestamp created;
  private ItemFiredStatus status;

  public ItemsFired makeClone() {
    Set<ItemSummaryForKitchen> clonedItemsInstances = new HashSet<>();
    for (ItemSummaryForKitchen instance: itemsInstances) {
      clonedItemsInstances.add(instance.makeClone());
    }

    return new ItemsFired(id, fireTime, type, clonedItemsInstances, created, status);
  }

  @JsonIgnore
  public String getTypeAsString() {
    switch (type) {
      case together:
        return "Together";
      case as_ready:
        return "As Ready";
      case add_on:
        return "Add On";
      case dont:
        return "Don't";
      case rush:
        return "Rush";
      case timed_fire:
        return "Timed Fire";
      default:
        return "";
    }
  }
}
