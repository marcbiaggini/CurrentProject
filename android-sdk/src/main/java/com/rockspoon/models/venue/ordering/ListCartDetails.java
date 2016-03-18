package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.venue.ordering.item.ItemSummary;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by greenfrvr
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListCartDetails {

  List<DinerSession> dinerSessions;
  List<ItemSummary> itemsSummary;

  public boolean isGroup() {
    return dinerSessions != null && dinerSessions.size() > 1;
  }

  public boolean hasItems() {
    return itemsSummary != null && !itemsSummary.isEmpty();
  }

}
