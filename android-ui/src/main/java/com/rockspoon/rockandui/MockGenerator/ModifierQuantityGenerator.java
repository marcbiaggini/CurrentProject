package com.rockspoon.rockandui.MockGenerator;

import android.content.Context;

import com.rockspoon.rockandui.Adapters.ModifierQuantityAdapter;
import com.rockspoon.rockandui.Objects.QuantityModifierData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 04/08/15.
 */
public class ModifierQuantityGenerator {
  private final ModifierQuantityAdapter quantityAdapter;
  private final List<QuantityModifierData> quantityModifierData = new ArrayList<>(5);

  public ModifierQuantityGenerator(Context ctx) {

    quantityModifierData.add(new QuantityModifierData("Wasab"));
    quantityModifierData.add(new QuantityModifierData("Tofu"));
    quantityModifierData.add(new QuantityModifierData("Mango"));
    quantityModifierData.add(new QuantityModifierData("Apple"));
    quantityModifierData.add(new QuantityModifierData("White Cheese"));

    quantityAdapter = new ModifierQuantityAdapter(ctx, quantityModifierData);
  }

  public ModifierQuantityAdapter getAdapter() {
    return quantityAdapter;
  }

  public List<QuantityModifierData> getCategoryData() {
    return quantityModifierData;
  }
}
