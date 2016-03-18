package com.rockspoon.rockandui.Objects;

/**
 * Created by lucas on 10/07/15.
 */
public class FoodData {
  private final float rating;
  private final String name, items;
  private final String tags[];
  private final float value;

  public FoodData() {
    this("Unknown", "Unknown", 0.f, 3.5f, new String[]{"Unknown"});
  }

  public FoodData(final String name, final String items, final float value, final float rating, final String[] tags) {
    this.name = name;
    this.items = items;
    this.value = value;
    this.rating = rating;
    this.tags = tags;
  }

  public float getRating() {
    return rating;
  }

  public float getValue() {
    return value;
  }

  public String[] getTags() {
    return tags;
  }

  public String getName() {
    return name;
  }

  public String getItems() {
    return items;
  }
}
