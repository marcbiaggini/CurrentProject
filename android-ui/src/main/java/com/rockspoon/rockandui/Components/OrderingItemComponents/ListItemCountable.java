package com.rockspoon.rockandui.Components.OrderingItemComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.settingsprices.ItemModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifierOption;
import com.rockspoon.models.venue.settingsprices.ItemModifierType;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by greenfrvr
 */
public class ListItemCountable extends ModifierView<ItemModifier> {

  public static final int DEFAULT_ADD = 0;
  public static final int DEFAULT_EXTRA = 1;

  private int defaultAmount = DEFAULT_ADD;

  private TextView titleView;
  private TextView subtitleView;
  private TextView mandatoryView;
  private ImageView expandView;
  private TextView priceView;
  private LinearLayout optionsView;

  public ListItemCountable(Context context) {
    this(context, null);
  }

  public ListItemCountable(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemCountable(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.modifier_countable, this);
    setOrientation(VERTICAL);
    initViews();
  }

  @Override
  protected void initViews() {
    titleView = (TextView) findViewById(R.id.modifier_item_title);
    mandatoryView = (TextView) findViewById(R.id.modifier_item_mandatorybox);
    priceView = (TextView) findViewById(R.id.modifier_item_price);
    expandView = (ImageView) findViewById(R.id.modifier_item_arrow);
    optionsView = (LinearLayout) findViewById(R.id.modifier_item_options);
  }

  @Override
  public void setViewData(ItemModifier data) {
    price = BigDecimal.ZERO;

    super.setViewData(data);
    defaultAmount = data.getType().equals(ItemModifierType.ingredient) ? DEFAULT_ADD : DEFAULT_EXTRA;
    setUpUi(data.getTitle(), data.getMandatory());
  }

  @Override
  public boolean isConfigured() {
    return isMandatory && !viewData.getSelectedOptions().isEmpty() || !isMandatory;
  }

  protected void setUpUi(String title, Boolean mandatory) {
    isMandatory = mandatory;
    titleView.setText(title);
    priceView.setText("$0.00");
    mandatoryView.setText(getContext().getString(mandatory ? R.string.text_mandatory : R.string.text_optional));
    expandView.setOnClickListener(v -> swapOptionsVisibility());
    setUpOptionsData(optionsView);
  }

  protected void setUpOptionsData(LinearLayout optionsView) {
    optionsView.removeAllViews();

    ItemModifier modifier = viewData;
    Set<ItemModifierOption> list = modifier.getOptions();
    int listPosition = 0;
    for (ItemModifierOption option : list) {
      optionsView.addView(createButton(option, listPosition++, option.getId()));
    }
    optionsView.refreshDrawableState();
  }

  private View createButton(ItemModifierOption option, int tagId, Long id) {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.modifier_item_quantity, optionsView, false);

    TextView ingredientView = (TextView) view.findViewById(R.id.modifier_item_quantity_name);
    ingredientView.setText(option.getIngredient().getTitle());
    ingredientView.setTag(tagId);

    TextView amountView = (TextView) view.findViewById(R.id.modifier_item_quantity_quantity);
    amountView.setText(String.valueOf(defaultAmount));

    String price;
    if (!((List) option.getPrices()).isEmpty()) {
      price = ((List<Map<String, String>>) option.getPrices()).get(0).get(size);
      TextView priceView = (TextView) view.findViewById(R.id.modifier_item_quantity_price);
      priceView.setVisibility(View.VISIBLE);
      priceView.setText(price);
    } else {
      price = "";
    }

    view.findViewById(R.id.modifier_item_quantity_minus).setOnClickListener(v -> {
      int value = Integer.valueOf(amountView.getText().toString());
      amountView.setText(String.valueOf(value == 0 ? 0 : value - 1));
      sendCheckedPosition(false, id);
      if (value > 0) {
        setPrice(false, price);
      }
    });
    view.findViewById(R.id.modifier_item_quantity_plus).setOnClickListener(v -> {
      int value = Integer.valueOf(amountView.getText().toString());
      amountView.setText(String.valueOf(++value));
      sendCheckedPosition(true, id);
      setPrice(true, price);
    });

    return view;
  }

  protected void sendCheckedPosition(boolean isAdded, Long id) {
    for (ItemModifierOption option : viewData.getOptions()) {
      if (option.getId().equals(id)) {
        if (isAdded) {
          viewData.getSelectedOptions().add(id);
        } else {
          viewData.getSelectedOptions().remove(id);
        }
      }
    }
  }

  protected void setPrice(boolean isAdded, String price) {
    if (price != null && !price.isEmpty()) {
      BigDecimal value = new BigDecimal(price.replaceAll(",", "."));
      this.price = isAdded ? this.price.add(value) : this.price.subtract(value);
      priceView.setText(getContext().getString(R.string.format_price, this.price));
    }
  }


  public void swapOptionsVisibility() {
    boolean visible = optionsView.getVisibility() == GONE;
    expandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    optionsView.setVisibility(visible ? VISIBLE : GONE);
  }
}
