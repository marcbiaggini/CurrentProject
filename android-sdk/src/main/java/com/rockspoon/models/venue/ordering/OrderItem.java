package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

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
public class OrderItem implements Serializable {

  @JsonProperty("orderItemId")
  private Long id;
  private Boolean isPayable;
  private DinerSession dinerSession;
  private DiningParty giftTo;
}
