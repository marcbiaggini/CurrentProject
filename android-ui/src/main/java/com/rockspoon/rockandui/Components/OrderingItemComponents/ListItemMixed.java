package com.rockspoon.rockandui.Components.OrderingItemComponents;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.item.ItemInstanceDetailsItemMixedModifierOption;
import com.rockspoon.models.venue.settingsprices.ItemModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifierOption;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by greenfrvr
 */
public class ListItemMixed extends ModifierView<ItemModifier> {

  public static final int DEFAULT_QUANTITY = 1;

  private ImageView extrasExpandView;
  private ImageView sideExpandView;
  private TextView priceView;
  private LinearLayout extrasOptionsView;
  private GridLayout sideOptionsView;

  public ListItemMixed(Context context) {
    this(context, null);
  }

  public ListItemMixed(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemMixed(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setBackground(null);
    inflate(context, R.layout.modifier_mixed, this);
    setOrientation(VERTICAL);
    initViews();
  }

  @Override
  protected void initViews() {
    extrasExpandView = (ImageView) findViewById(R.id.modifier_item_arrow_extras);
    sideExpandView = (ImageView) findViewById(R.id.modifier_item_arrow_side);
    priceView = (TextView) findViewById(R.id.modifier_item_price);
    extrasOptionsView = (LinearLayout) findViewById(R.id.modifier_item_options_extras);
    sideOptionsView = (GridLayout) findViewById(R.id.modifier_item_options_side);
  }

  @Override
  public void setViewData(ItemModifier data) {
    price = BigDecimal.ZERO;

    super.setViewData(data);
    data.getMixedModifierOptions().clear();
    for (ItemModifierOption option : data.getOptions()) {
      data.getMixedModifierOptions().add(new ItemInstanceDetailsItemMixedModifierOption(option.getId(), false, false, false, 1));
    }
    setUpUi();
  }

  @Override
  public boolean isConfigured() {
    return true;
  }

  protected void setUpUi() {
    extrasExpandView.setOnClickListener(v -> swapExtraOptionsVisibility());
    sideExpandView.setOnClickListener(v -> swapSideOptionsVisibility());
    priceView.setText("$0.00");
    setUpOptionsData(extrasOptionsView);
    setUpOptionsData(sideOptionsView);
  }

  protected void setUpOptionsData(LinearLayout optionsView) {
    optionsView.removeAllViews();

    Set<ItemModifierOption> list = viewData.getOptions();
    int listPosition = 0;
    for (ItemModifierOption option : list) {
      if (option.getIsExtra() || option.getIsRemove()) {
        optionsView.addView(createButton(option, listPosition++, option.getId()));
      }
    }
    optionsView.refreshDrawableState();
  }

  protected void setUpOptionsData(GridLayout optionsView) {
    optionsView.removeAllViews();

    Set<ItemModifierOption> list = viewData.getOptions();
    int listPosition = 0;
    for (ItemModifierOption option : list) {
      if (option.getIsOnTheSide() == null || option.getIsOnTheSide()) {
        optionsView.addView(createSelector(option, listPosition++, option.getId()));
      }
    }
    optionsView.refreshDrawableState();
  }

  @SuppressWarnings("unchecked")
  private View createButton(ItemModifierOption option, int tagId, Long id) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.modifier_item_quantity, extrasOptionsView, false);

    TextView ingredientView = (TextView) view.findViewById(R.id.modifier_item_quantity_name);
    ingredientView.setText(option.getIngredient().getTitle());
    ingredientView.setTag(tagId);

    TextView amountView = (TextView) view.findViewById(R.id.modifier_item_quantity_quantity);
    amountView.setText(String.valueOf(DEFAULT_QUANTITY));

    String price;
    if (option.getIsExtra()) {
      price = ((List<Map<String, String>>) option.getPrices()).get(0).get(size);
      TextView priceView = (TextView) view.findViewById(R.id.modifier_item_quantity_price);
      priceView.setVisibility(View.VISIBLE);
      priceView.setText(price);
    } else {
      price = "";
    }

    ImageButton plusButton = (ImageButton) view.findViewById(R.id.modifier_item_quantity_plus);
    ImageButton minusButton = (ImageButton) view.findViewById(R.id.modifier_item_quantity_minus);

    minusButton.setOnClickListener(v -> {
      int value = Integer.valueOf(amountView.getText().toString());
      if (value > 1 || (value == 1 && (option.getIsRemove() == null || option.getIsRemove()))) {
        if (value > 1) {
          setPrice(false, price);
        }
        value = value - 1;
        amountView.setText(String.valueOf(value));
        sendCheckedPosition(false, id);

        if (!option.getIsExtra() && value == 0) {
          enableButton(plusButton);
        }
        if (!option.getIsRemove() && value == 1) {
          disableButton(minusButton);
        }
      }
    });

    plusButton.setOnClickListener(v -> {
      int value = Integer.valueOf(amountView.getText().toString());
      if (option.getIsExtra() == null || option.getIsExtra() || value == 0) {
        amountView.setText(String.valueOf(++value));
        sendCheckedPosition(true, id);
        if (value > 1) {
          setPrice(true, price);
        }

        if (!option.getIsExtra() && value > 0) {
          disableButton(plusButton);
        }
        if (!option.getIsRemove() && value > 1) {
          enableButton(minusButton);
        }
      }
    });

    if (!option.getIsExtra()) {
      disableButton(plusButton);
    }

    if (!option.getIsRemove()) {
      disableButton(minusButton);
    }

    return view;
  }

  private View createSelector(ItemModifierOption option, int tagId, Long id) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.modifiers_mixed_item, sideOptionsView, false);
    view.setTag(id);

    TextView ingredientView = (TextView) view.findViewById(R.id.option_name);
    ingredientView.setText(option.getIngredient().getTitle());

    view.setOnClickListener(v -> {
      view.setSelected(!view.isSelected());
      sendSelectedPosition(view.isSelected(), id);
    });

    return view;
  }

  protected void sendCheckedPosition(boolean isAdded, Long id) {
    ItemInstanceDetailsItemMixedModifierOption option = findOption(viewData.getMixedModifierOptions(), id);
    if (option != null) {
      int quantity = option.getQuantity();
      option.setQuantity(quantity + (isAdded ? 1 : -1));
      onSideEnabled(quantity > 1 || isAdded, id);
    }
  }

  private void onSideEnabled(boolean enable, Long id) {
    for (int i = 0; i < sideOptionsView.getChildCount(); i++) {
      View view = sideOptionsView.getChildAt(i);
      if (view.getTag().equals(id)) {
        view.setEnabled(enable);
      }
    }
  }

  protected void sendSelectedPosition(boolean isSelected, Long id) {
    ItemInstanceDetailsItemMixedModifierOption option = findOption(viewData.getMixedModifierOptions(), id);
    if (option != null) {
      option.setIsOnTheSide(isSelected);
    }
  }

  private ItemInstanceDetailsItemMixedModifierOption findOption(List<ItemInstanceDetailsItemMixedModifierOption> options, Long id) {
    for (ItemInstanceDetailsItemMixedModifierOption option : options) {
      if (option.getItemModifierOptionId().equals(id)) {
        return option;
      }
    }
    return null;
  }

  protected void setPrice(boolean isAdded, String price) {
    if (price != null && !price.isEmpty()) {
      BigDecimal value = new BigDecimal(price.replaceAll(",", "."));
      this.price = isAdded ? this.price.add(value) : this.price.subtract(value);
      priceView.setText(getContext().getString(R.string.format_price, this.price));
    }
  }

  private void disableButton(ImageButton button) {
    button.setEnabled(false);
    button.setColorFilter(ContextCompat.getColor(getContext(), R.color.textcolor_gray));
  }

  private void enableButton(ImageButton button) {
    button.setEnabled(true);
    button.clearColorFilter();
  }

  public void swapExtraOptionsVisibility() {
    boolean visible = extrasOptionsView.getVisibility() == GONE;
    extrasExpandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    extrasOptionsView.setVisibility(visible ? VISIBLE : GONE);
  }

  public void swapSideOptionsVisibility() {
    boolean visible = sideOptionsView.getVisibility() == GONE;
    sideExpandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    sideOptionsView.setVisibility(visible ? VISIBLE : GONE);
  }
}
