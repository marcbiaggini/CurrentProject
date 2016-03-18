package com.rockspoon.rockandui.Managers;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.settingsprices.CookingModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eugen K. on 3/4/16.
 */
public class ItemModifiersManager {

  private String itemSize = "";
  private Item item;
  private ItemInstanceService service;

  private List<Object> modifiersList;

  public ItemModifiersManager(Item item) {
    this.item = item;
    this.modifiersList = createModifiersList();
  }

  public String getItemSize() {
    return itemSize;
  }

  public void setItemSize(String itemSize) {
    this.itemSize = itemSize;
  }

  public Object getItemById(int id) {
    return modifiersList.get(id);
  }

  public int getItemsCount() {
    return modifiersList.size();
  }

  public void setModifier(Object o, int position) {
    if (modifiersList.get(position) != null) {
      modifiersList.set(position, o);
    }
  }

  public void extractModiers() {
    modifiersList = createModifiersList();
  }

  public void clearModifiers() {
    modifiersList.clear();
  }

  public List<Object> getModifiersList() {
    return modifiersList;
  }

  private List<Object> createModifiersList() {
    List<Object> modifiers = new ArrayList<>();

    ItemPrices prices = new ItemPrices();
    prices.convertPrices(item.getPrices());
    modifiers.add(prices);

    for (CookingModifier modifier : item.getSettingsAndPrices().getCookingModifiers()) {
      modifiers.add(modifier);
    }
    for (ItemModifier modifier : item.getSettingsAndPrices().getItemModifiers()) {
      modifiers.add(modifier);
    }
    return modifiers;
  }

  @Getter
  @Setter
  public class ItemPrices {
    private List<ItemPrice> prices;

    public void convertPrices(Map<String, BigDecimal> prices) {
      this.prices = new ArrayList<>();
      for (Map.Entry<String, BigDecimal> entry : prices.entrySet()) {
        ItemPrice price = new ItemPrice();
        price.setPrice(entry.getValue());
        price.setTitle(entry.getKey());
        this.prices.add(price);
      }
      this.prices.get(0).setSelected(true);
      itemSize = this.prices.get(0).getTitle();
    }
  }

  @Getter
  @Setter
  public class ItemPrice {
    String title;
    BigDecimal price;
    boolean isSelected;
  }
}
