package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.user.User;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DinerSession {
  //TODO: Check with Cateano
  @JsonProperty("dinerSessionId")
  private Long id;
  private Integer seatNumber;
  private Timestamp created;
  private User diner;

  //private List<DinerSessionEventHistory> eventHistory;
  // private Set<OrderItem> orderItems;
  // private Set<TabEntry> tabEntries;
  // private Set<Tip> tips;
}
