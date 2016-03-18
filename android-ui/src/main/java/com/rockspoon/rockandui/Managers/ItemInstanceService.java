package com.rockspoon.rockandui.Managers;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.ordering.item.ItemInstance;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetails;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsCookingModifierSelection;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsCookingModifiersConfiguration;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemMixedModifierConfiguration;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemMixedModifierSelection;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemModifierConfiguration;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemModifierSelection;
import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;
import com.rockspoon.models.venue.settingsprices.CookingModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifierType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Eugen K. on 3/7/16.
 */
public class ItemInstanceService {

  private final static String MAP_KEY_IS_DEFAULT = "isDefault";
  private final static String MAP_KEY_NAME = "name";

  public ItemInstanceDetails createItemInstanceDetails(Item item, List<Object> modifiers, Long menuCategoryId) {

    List<ItemInstanceDetailsCookingModifierSelection> cookingModifierSelections = new ArrayList<>();
    List<ItemInstanceDetailsItemModifierSelection> ingredientsSelections = new ArrayList<>();
    ItemInstanceDetailsItemMixedModifierSelection mixedSelection = null;

    String size = null;
    for (Object obj : modifiers) {
      if (obj instanceof ItemModifiersManager.ItemPrices) {
        size = extractSize(obj);
      } else if (obj instanceof CookingModifier) {
        extractCookingModifiers((CookingModifier) obj, cookingModifierSelections);
      } else if (obj instanceof ItemModifier) {
        ItemModifier modifier = (ItemModifier) obj;
        if (modifier.getType().equals(ItemModifierType.ingredient)) {
          if (!modifier.getFullIngredientOptions().isEmpty()) {
            ingredientsSelections.add(new ItemInstanceDetailsItemModifierSelection(modifier.getId(), modifier.getFullIngredientOptions()));
          }
        } else if (!modifier.getFullIngredientOptions().isEmpty()) {
          mixedSelection = new ItemInstanceDetailsItemMixedModifierSelection(modifier.getId(), modifier.getFullMixedModifierOptions());
        }
      }
    }

    ItemInstanceDetailsCookingModifiersConfiguration cookingConf = null;
    if (!cookingModifierSelections.isEmpty()) {
      cookingConf = new ItemInstanceDetailsCookingModifiersConfiguration(cookingModifierSelections);
    }
    ItemInstanceDetailsItemModifierConfiguration ingredientConf = null;
    if (!ingredientsSelections.isEmpty()) {
      ingredientConf = new ItemInstanceDetailsItemModifierConfiguration(ingredientsSelections);
    }
    ItemInstanceDetailsItemMixedModifierConfiguration mixedConf = null;
    if (mixedSelection != null) {
      mixedConf = new ItemInstanceDetailsItemMixedModifierConfiguration(mixedSelection);
    }

    //TODO implement creating other models
//    ItemInstanceDetailsSubstitutionModifierConfiguration substitutionsConfiguration = new ItemInstanceDetailsSubstitutionModifierConfiguration(new ArrayList<>());
//    ItemInstanceDetailsCustomModifierConfiguration customModifiersConfiguration = new ItemInstanceDetailsCustomModifierConfiguration(new ArrayList<>());

    return new ItemInstanceDetails(size, menuCategoryId, cookingConf, null, ingredientConf, mixedConf, null);
  }

  public ItemInstanceRequest createItemInstanceRequest(ItemInstance itemInstance, double quantity) {
    List<ItemInstance> list = new ArrayList<>();
    for (int i = 0; i < quantity; i++) {
      list.add(itemInstance);
    }
    return new ItemInstanceRequest(list, null, null);
  }

  private String extractSize(Object obj) {
    for (ItemModifiersManager.ItemPrice price : ItemModifiersManager.ItemPrices.class.cast(obj).getPrices()) {
      if (price.isSelected) {
        return price.getTitle();
      }
    }
    return null;
  }

  private void extractCookingModifiers(CookingModifier modifier, List<ItemInstanceDetailsCookingModifierSelection> selections) {
    String optionName = null;
    for (Map<String, Object> map : modifier.getOptions()) {
      if (Boolean.valueOf(map.get(MAP_KEY_IS_DEFAULT).toString())) {
        optionName = (String) map.get(MAP_KEY_NAME);
      }
    }
    if (optionName != null) {
      selections.add(new ItemInstanceDetailsCookingModifierSelection(modifier.getId(), optionName));
    }
  }

}
