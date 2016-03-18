package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItem;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * ItemSummaryForKitchen.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/14/16.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemSummaryForKitchen extends ItemSummary {
  private Set<ProductionAreaStationFiredItem> productionAreaStationFiredItems;

  public ItemSummaryForKitchen makeClone() {
    Set<ProductionAreaStationFiredItem> clonedPasFiredItems = new HashSet<>();
    for (ProductionAreaStationFiredItem item: productionAreaStationFiredItems) {
      clonedPasFiredItems.add(item.makeClone());
    }

    return new ItemSummaryForKitchen(itemInstanceId, name, modifiersDescription,
        status, preparationStartedAt, clonedPasFiredItems);
  }

  public ItemSummaryForKitchen(Long itemInstanceId, String name, String modifiersDescription,
                               ItemInstanceStatus status, Timestamp preparationStartedAt,
                               Set<ProductionAreaStationFiredItem> productionAreaStationFiredItems) {
    super(itemInstanceId, name, modifiersDescription, status, preparationStartedAt);
    this.productionAreaStationFiredItems = productionAreaStationFiredItems;
  }
}
