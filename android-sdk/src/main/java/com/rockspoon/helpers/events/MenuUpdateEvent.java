package com.rockspoon.helpers.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.venue.menu.Menu;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 18/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuUpdateEvent implements Serializable, CacheBusEvent {
  private List<Menu> menuList;
}
