package com.rockspoon.rockandui.MockGenerator;

import android.content.Context;

import com.rockspoon.rockandui.Adapters.ModifierRadioAdapter;
import com.rockspoon.rockandui.Objects.RadioModifierData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 04/08/15.
 */
public class ModifierRadioGenerator {
  private final ModifierRadioAdapter radioAdapter;
  private final List<RadioModifierData> radioModifierData = new ArrayList<>(5);

  public ModifierRadioGenerator(Context ctx, boolean withPrice) {

    if (withPrice) {
      radioModifierData.add(new RadioModifierData("Small", BigDecimal.valueOf(12.95)));
      radioModifierData.add(new RadioModifierData("Medium", BigDecimal.valueOf(14.95)));
      radioModifierData.add(new RadioModifierData("Large", BigDecimal.valueOf(15.95)));
    } else {
      radioModifierData.add(new RadioModifierData("Gluten Free"));
      radioModifierData.add(new RadioModifierData("Regular Crust"));
      radioModifierData.add(new RadioModifierData("Thin Crust"));
    }
    radioAdapter = new ModifierRadioAdapter(ctx, radioModifierData);

  }

  public ModifierRadioGenerator(Context ctx, Map<String, BigDecimal> prices, int selectedId) {
    int i = 0;
    for (Map.Entry<String, BigDecimal> entry : prices.entrySet()) {
      radioModifierData.add(new RadioModifierData(entry.getKey(), entry.getValue()));
      if (i == selectedId) {
        radioModifierData.get(i).setSelected(true);
      }
      i++;
    }
    radioAdapter = new ModifierRadioAdapter(ctx, radioModifierData);
  }

  public ModifierRadioGenerator(Context ctx, Map<String, BigDecimal> prices) {
    this(ctx, prices, -1);
  }

  public ModifierRadioAdapter getAdapter() {
    return radioAdapter;
  }

  public List<RadioModifierData> getCategoryData() {
    return radioModifierData;
  }
}
