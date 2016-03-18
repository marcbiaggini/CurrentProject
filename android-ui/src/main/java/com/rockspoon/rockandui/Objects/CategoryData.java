package com.rockspoon.rockandui.Objects;

import com.rockspoon.models.venue.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Teske on 27/07/15.
 */
public class CategoryData {
  private final List<Item> items = new ArrayList<>();
  private String name;

  public CategoryData(String name) {
    this.name = name;
  }

  public CategoryData(String name, List<Item> items) {
    this(name);
    this.items.addAll(items);
  }

  public void addItem(final Item item) {
    items.add(item);
  }

  public List<Item> getItems() {
    return items;
  }

  public int getItemCount() {
    return items.size();
  }

  public String getName() {
    return name;
  }
}
