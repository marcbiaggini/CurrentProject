package com.rockspoon.rockandui.MockGenerator;

import android.content.Context;

import com.rockspoon.models.image.Image;
import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.item.ItemCategory;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.rockandui.Adapters.FoodAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by lucas on 15/07/15.
 */
public class FoodDataGenerator {

  private final static String descs[] = {
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
      "Mauris risus nisl, sodales in fringilla nec, aliquam in tortor.",
      "Ut eget auctor ligula. Ut a mauris sem.",
      "Cras nisi est, tristique nec dictum at, fermentum nec ligula",
      "Phasellus egestas felis varius, pharetra ante cursus, convallis felis."
  };

  private final static String[][] tags = {
      {"Tag0", "Tag1", "HUE", "BR"},
      {"A", "B", "C", "D"},
      {"1", "2", "3", "4"},
      {"AA", "BA", "CA", "DA"},
      {"ZA", "ZB", "ZC", "ZD"}
  };

  private final FoodAdapter foodAdapter;
  private final List<Item> foodData = new ArrayList<>();

  public FoodDataGenerator(Context ctx) {
    foodAdapter = new FoodAdapter(ctx);
    reGen();
  }

  private List<SizePrice> generateSizePrice(String size, BigDecimal price) {
    List<SizePrice> sizePriceList = new LinkedList<>();
    sizePriceList.add(new SizePrice(size, price));
    return sizePriceList;
  }

  private List<Tag> generateTag(String[] tags, String category) {
    List<Tag> tagsList = new ArrayList<>(tags.length);
    for (String str: tags) {
      tagsList.add(new Tag(String.valueOf(tagsList.size()+1), str, category));
    }
    return tagsList;
  }

  public void genSalads() {
    foodData.clear();
    foodData.add(new Item(1L, ItemCategory.FOOD, "Salad", "Kale Caesar", "crunchy romaine, baby kale, cherry tomatoes, cucumbers, broccoli, parmesan, breadcrumbs, Caesar dressing", "K. Caesar", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(10)), generateTag(new String[]{"salad", "cucumbers", "parmesan"}, "salad")));
    foodData.add(new Item(2L, ItemCategory.FOOD, "Salad", "Quinoa Crunch Bowl", "quinoa tabbouleh, fresh crunchy vegetables, avocado, arugula, edamame hummus, chipotle vinaigrette, fireman’s hot sauce", "Quinoa Crunch", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(4)), generateTag(new String[]{"salad", "quinoa", "vinaigrette"}, "salad")));
    foodData.add(new Item(3L, ItemCategory.FOOD, "Salad", "Grilled Veggie", "romaine, baby spinach, roasted peppers, eggplant, onions, tomatoes, snap peas, fresh mozzarella, croutons, garlic herb vinaigrette", "G. Veggie", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(9)), generateTag(new String[]{"salad", "croutons", "garlic"}, "salad")));
    foodData.add(new Item(4L, ItemCategory.FOOD, "Salad", "Farmer's Market", "arugula, blackberries, pickled onions, spiced pecans, goat cheese, balsamic vinaigrette", "F. Market", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(9)), generateTag(new String[]{"salad", "balsamic", "goat cheese"}, "salad")));
    foodAdapter.setData(foodData);
  }

  public void genSandwiches() {
    foodData.clear();
    foodData.add(new Item(1L, ItemCategory.FOOD, "Sandwich", "Farmhouse Burger", "100% grass-fed beef, crunchy romaine, tomato, red onion, farmhouse pickle,  dijonnaise on multi-grain bun, gluten-free upon request", "Farmhouse", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(5)), generateTag(new String[]{"sandwich", "tomato", "romaine"}, "sandwich")));
    foodData.add(new Item(2L, ItemCategory.FOOD, "Sandwich", "Mahi Fish Tacos", "chayote slaw, avocado, cilantro, chipotle aioli on corn tortillas, salsa fresca, gluten-free and vegan upon request", "Mahi Fish", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(14)), generateTag(new String[]{"sandwich", "salsa", "cilantro"}, "sandwich")));
    foodData.add(new Item(3L, ItemCategory.FOOD, "Sandwich", "Quinoa Crunch", "quinoa tabbouleh, crunchy vegetables, avocado, edamame hummus, fireman’s hot sauce on the side", "Quinoa Crunch", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(9)), generateTag(new String[]{"sandwich", "croutons", "garlic"}, "sandwich")));
    foodData.add(new Item(4L, ItemCategory.FOOD, "Sandwich", "Buffalo Chicken", "avocado, black beans, corn, chayote, romaine, and Greek yogurt ranch", "Buffalo Chicken", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(7)), generateTag(new String[]{"sandwich", "greek yogurt", "corn"}, "sandwich")));
        foodAdapter.setData(foodData);
  }

  public void genSoups() {
    foodData.clear();
    foodData.add(new Item(1L, ItemCategory.FOOD, "Soup", "Sweet Corn Chowder", "made with cashew cream", "Sweet Corn", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(12)), generateTag(new String[]{"soup", "corn", "cashew"}, "soup")));
    foodData.add(new Item(2L, ItemCategory.FOOD, "Soup", "Seasonal Soup", "ask about today’s soup", "Seasonal", new Image(), generateSizePrice("Normal", BigDecimal.valueOf(14)), generateTag(new String[]{"soup"}, "soup")));
    foodAdapter.setData(foodData);
  }

  public void reGen() {
    final Random randomGenerator = new Random();
    int qty = randomGenerator.nextInt(12);

    foodData.clear();

    for (long i = 0; i < qty; i++) {
      int descId = randomGenerator.nextInt(descs.length);
      int tagsId = randomGenerator.nextInt(tags.length);
      float value = randomGenerator.nextFloat() * randomGenerator.nextInt(123);

      foodData.add(new Item(i, ItemCategory.FOOD, "Generated", "Food " + i, descs[descId],  "Food " + i, new Image(), generateSizePrice("Normal", BigDecimal.valueOf(value)), generateTag(tags[tagsId], "Generated")));
    }

    foodAdapter.setData(foodData);
  }

  public FoodAdapter getAdapter() {
    return foodAdapter;
  }

  public List<Item> getFoodData() {
    return getFoodData(false);
  }

  public List<Item> getFoodData(boolean regen) {
    if (regen)
      reGen();
    List<Item> itemList = new LinkedList<>();
    itemList.addAll(foodData);
    return itemList;
  }

  public List<Item> getFoodData(String type) {
    switch (type) {
      case "salads":
        genSalads();
        break;
      case "sandwiches":
        genSandwiches();
        break;
      case "soups":
        genSoups();
        break;
      default:
        reGen();
    }
    List<Item> itemList = new LinkedList<>();
    itemList.addAll(foodData);
    return itemList;
  }
}
