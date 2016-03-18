package com.rockspoon.models.item;

import java.util.EnumSet;

/**
 * Created by lucas on 02/02/16.
 */
public enum ItemSubcategory {
  NONE,
  // Beverages Subcategories
  WATER,
  JUICE,
  SMOOTHIE,
  SODA,
  OTHER,
  // Hot Bev. Subcategories
  COFFEE,
  TEA,
  // Alcoholic Bev. Subcategories
  BEER,
  WINE,
  SAKE,
  SPIRITS,
  COCKTAILS;

  public EnumSet<ItemSubcategory> forCategory(final Enum<ItemCategory> category) {
    if (category == ItemCategory.BEVERAGES) {
      return EnumSet.of(WATER, JUICE, SMOOTHIE, SODA, COFFEE, TEA, OTHER);
    } else if (category == ItemCategory.HOT_BEV) {
      return EnumSet.of(COFFEE, TEA, OTHER);
    } else if (category == ItemCategory.ALCOHOLIC_BEV) {
      return EnumSet.of(WINE, BEER, SPIRITS, COCKTAILS);
    }
    return EnumSet.of(NONE);
  }

}
