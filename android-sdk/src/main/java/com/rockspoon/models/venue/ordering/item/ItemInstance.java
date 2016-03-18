package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.ordering.OrderItem;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItem;
import com.rockspoon.models.venue.ordering.TabEntry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 03/02/16.
 */
@Builder
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemInstance implements Serializable {

  @JsonProperty("itemInstanceId")
  private Long id;
  private Item item;
  private String itemName;
  private Boolean splitPlate;
  private ItemInstanceStatus status;
  private ItemInstanceDetails metadata;
  private Set<OrderItem> orderItems;
  private Set<TabEntry> tabs;
  private Boolean comp;
  private String compReason;
  private Timestamp created;
  private BigDecimal amount;
  private Set<ProductionAreaStationFiredItem> productionAreaStationFiredItems;

  public ItemInstance makeClone() {
    Set<ProductionAreaStationFiredItem> clonedPasFiredItems = new HashSet<>();
    for (ProductionAreaStationFiredItem item : productionAreaStationFiredItems) {
      clonedPasFiredItems.add(item.makeClone());
    }

    return new ItemInstance(id, item, itemName, splitPlate, status, metadata, orderItems, tabs, comp,
        compReason, created, amount, clonedPasFiredItems);
  }
}
