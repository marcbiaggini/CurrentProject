package com.rockspoon.models.venue.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.item.ItemCategory;
import com.rockspoon.models.ports.DayOfWeek;
import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.models.venue.sellinginfo.FoodService.FoodServiceType;
import com.rockspoon.models.venue.settingsprices.SettingsAndPrices;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {
  @JsonProperty("itemId")
  private Long id;
  private ItemCategory category;
  private String subcategory;
  private String title;
  private Image logo;
  private String abbreviation;
  private String description;
  private FoodServiceType[] availableFor;
  private Boolean applyTaxDeliveryTakeout;
  private Boolean rateable;
  private ItemAvailability availability;
  private DayOfWeek[] availableOn;
  private Long countdown;
  private SettingsAndPrices settingsAndPrices;
  private Set<ItemRoutingSetting> routingSettings;
  private Map<String, BigDecimal> prices;
  private List<Tag> tags;

  // TODO: Remove this. Added only to not break the code.
  @Deprecated
  public Item(Long id, ItemCategory category, String subcategory, String title, String description, String abbreviation, Image logo, List<SizePrice> prices, List<Tag> tags) {
    this(id, category, subcategory, title, logo, abbreviation, description, null, false, false, null, null, null, null, null, new HashMap<>(), tags);
    for (final SizePrice price : prices) {
      this.prices.put(price.getSize(), price.getPrice());
    }
  }

  // TODO: Remove this. Added only to not break the code.
  @SuppressWarnings("unchecked")
  @Deprecated
  public List<SizePrice> getSizePrices() {
    List<SizePrice> sizePrices = new ArrayList<>();

    for (final Map.Entry<String, BigDecimal> entry : prices.entrySet()) {
      sizePrices.add(new SizePrice(entry.getKey(), entry.getValue()));
    }

    return sizePrices;
  }

}
