package com.rockspoon.helpers.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.venue.ordering.item.ItemsFired;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by juancamilovilladuarte on 3/7/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemsFiredUpdateEvent implements Serializable, CacheBusEvent {
  private List<ItemsFired> listItemsFired;
}
