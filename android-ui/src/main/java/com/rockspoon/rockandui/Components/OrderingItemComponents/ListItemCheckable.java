package com.rockspoon.rockandui.Components.OrderingItemComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.settingsprices.ItemModifier;
import com.rockspoon.models.venue.settingsprices.ItemModifierOption;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by greenfrvr
 */
public class ListItemCheckable extends ModifierView<ItemModifier> {

  private TextView titleView;
  private TextView mandatoryView;
  private ImageView expandView;
  private TextView priceView;
  private LinearLayout optionsView;

  public ListItemCheckable(Context context) {
    this(context, null);
  }

  public ListItemCheckable(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemCheckable(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.modifier_checkable, this);
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
    super.setViewData(data);
    setUpUi(data.getTitle(), data.getMandatory());
  }

  @Override
  public boolean isConfigured() {
    return isMandatory && !viewData.getSelectedOptions().isEmpty();
  }

  protected void setUpUi(String title, Boolean mandatory) {
    isMandatory = mandatory;
    titleView.setText(title);
    mandatoryView.setText(getContext().getString(mandatory ? R.string.text_mandatory : R.string.text_optional));
    expandView.setOnClickListener(v -> swapOptionsVisibility());
    setUpOptionsData(optionsView);
  }

  protected void setUpOptionsData(LinearLayout optionsView) {
    optionsView.removeAllViews();

    priceView.setVisibility(GONE);
    ItemModifier modifier = viewData;
    Set<ItemModifierOption> list = modifier.getOptions();
    int listPosition = 0;
    for (ItemModifierOption option : list) {
      optionsView.addView(createButton(option, listPosition++, option.getId()));
    }
    optionsView.refreshDrawableState();
  }

  private View createButton(ItemModifierOption option, int tagId, Long id) {
    View button = LayoutInflater.from(getContext()).inflate(R.layout.modifier_item_checkbox, optionsView, false);
    button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    CheckBox checkBox = (CheckBox) button.findViewById(R.id.modifier_item_check_text);
    checkBox.setText(option.getIngredient().getTitle());
    checkBox.setTag(tagId);
    checkBox.setChecked(option.getIsDefault());
    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> sendCheckedPosition(isChecked, id));
    return button;
  }

  protected void sendCheckedPosition(boolean isChecked, Long id) {
    for (ItemModifierOption option : viewData.getOptions()) {
      if (option.getId().equals(id)) {
        if (isChecked) {
          viewData.getSelectedOptions().add(id);
        } else {
          viewData.getSelectedOptions().remove(id);
        }
      }
    }
  }

  protected void setPrice(BigDecimal price) {
    priceView.setText(getContext().getString(R.string.format_price, price));
  }

  public void swapOptionsVisibility() {
    boolean visible = optionsView.getVisibility() == GONE;
    expandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    optionsView.setVisibility(visible ? VISIBLE : GONE);
  }
}
