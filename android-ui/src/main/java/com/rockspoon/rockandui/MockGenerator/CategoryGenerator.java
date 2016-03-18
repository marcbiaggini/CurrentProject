package com.rockspoon.rockandui.MockGenerator;

import android.content.Context;

import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.rockandui.Adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Teske on 29/07/15.
 */
public class CategoryGenerator {

  private final CategoryAdapter categoryAdapter;
  private final List<MenuCategory> categoryData;

  public CategoryGenerator(Context ctx) {

    final FoodDataGenerator foodgen = new FoodDataGenerator(ctx);

    categoryData = new ArrayList<>(6);

    categoryData.add(new MenuCategory(1L, "Salads", "The Salads", 0, null, foodgen.getFoodData("salads")));
    categoryData.add(new MenuCategory(2L, "Sandwiches", "The Sandwiches", 1, null, foodgen.getFoodData("sandwiches")));
    categoryData.add(new MenuCategory(3L, "Soups", "The Soups", 2, null, foodgen.getFoodData("soups")));
    categoryData.add(new MenuCategory(4L, "Category 1", "The Category 1", 3, null, foodgen.getFoodData(true)));
    categoryData.add(new MenuCategory(5L, "Category 2", "The Category 2", 4, null, foodgen.getFoodData(true)));

    categoryAdapter = new CategoryAdapter(ctx, categoryData);
  }

  public CategoryAdapter getAdapter() {
    return categoryAdapter;
  }

  public List<MenuCategory> getCategoryData() {
    return categoryData;
  }
}
