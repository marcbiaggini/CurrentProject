package com.rockspoon.models.item;

/**
 * Created by lucas on 02/02/16.
 */
public enum ItemPropertyType {
  // Beverages Property Types
  FRUIT(ComponentStyle.COMBO),
  COFFEE_TEA_BRAND(ComponentStyle.CHECKBOX),
  FRUIT_TYPE(ComponentStyle.CHECKBOX),
  COFFE_TYPE(ComponentStyle.COMBO),
  COFFE_ROASTING(ComponentStyle.COMBO),
  TEA_FLAVOR(ComponentStyle.COMBO),
  SODA_TYPE(ComponentStyle.COMBO),
  JUICE_TYPE(ComponentStyle.COMBO),
  WATER_TYPE(ComponentStyle.COMBO),
  // Alcoholic
  BEER_TYPE(ComponentStyle.COMBO),
  BEER_PACKAGE(ComponentStyle.COMBO),
  WINE_TYPE(ComponentStyle.COMBO),
  WINE_COUNTRY(ComponentStyle.COMBO),
  WINE_GRAPE(ComponentStyle.COMBO),
  SPIRITS_TYPE(ComponentStyle.COMBO),
  SAKE_TYPE(ComponentStyle.COMBO);

  private ComponentStyle style;

  ItemPropertyType(final ComponentStyle style) {
    this.style = style;
  }

  public ComponentStyle getStyle() {
    return this.style;
  }
}
