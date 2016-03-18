package com.rockspoon.models.venue.menu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.venue.item.Item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 02/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MenuCategory {
  @JsonProperty("menuCategoryId")
  private Long id;
  private String title;
  private String description;
  private Integer index;
  private List<MenuCategory> subcategories;
  private List<Item> items;
}
