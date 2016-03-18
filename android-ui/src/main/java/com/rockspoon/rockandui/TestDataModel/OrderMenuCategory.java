package com.rockspoon.rockandui.TestDataModel;

import com.rockspoon.models.venue.menu.MenuCategory;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by Yury Minich on 1/28/16.
 */
@Wither
@Value
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class OrderMenuCategory {
  private Long id;
  private String title;
  private String description;
  private List<SeatOrderItem> items = new ArrayList<>();

  public OrderMenuCategory(final MenuCategory category, List<SeatOrderItem> items){
    this.id = category.getId();
    this.description = category.getDescription();
    this.title = category.getTitle();
    this.items.addAll(items);
  }
}
