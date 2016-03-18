package com.rockspoon.rockandui.Components.OrderingItemComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rockspoon.models.venue.settingsprices.CookingModifier;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eugen K. on 3/4/16.
 */
public class ListItemSelectable extends ModifierView<CookingModifier> {

  protected final static String MAP_KEY_IS_DEFAULT = "isDefault";
  protected final static String MAP_KEY_NAME = "name";

  private TextView titleView;
  private TextView descriptionView;
  private TextView mandatoryView;
  private ImageView expandView;
  private TextView priceView;
  private RadioGroup optionsView;

  private Map<String, Object> noneOption;

  public ListItemSelectable(Context context) {
    this(context, null);
  }

  public ListItemSelectable(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemSelectable(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.modifier_item, this);
    setOrientation(VERTICAL);
    initViews();
  }

  @Override
  protected void initViews() {
    titleView = (TextView) findViewById(R.id.modifier_item_title);
    descriptionView = (TextView) findViewById(R.id.modifier_item_description);
    mandatoryView = (TextView) findViewById(R.id.modifier_item_mandatorybox);
    priceView = (TextView) findViewById(R.id.modifier_item_price);
    expandView = (ImageView) findViewById(R.id.modifier_item_arrow);
    optionsView = (RadioGroup) findViewById(R.id.modifier_item_options);
  }

  @Override
  public void setViewData(CookingModifier data) {
    super.setViewData(data);
    setUpUi(data.getTitle(), data.getMandatory());
  }

  @Override
  public boolean isConfigured() {
    return true;
  }

  protected void setUpUi(String title, Boolean mandatory) {
    isMandatory = mandatory;
    titleView.setText(title);
    mandatoryView.setText(getContext().getString(mandatory ? R.string.text_mandatory : R.string.text_optional));
    expandView.setOnClickListener(v -> swapOptionsVisibility());
    setUpOptionsData(optionsView);
  }

  protected void setUpOptionsData(RadioGroup optionsView) {
    optionsView.removeAllViews();

    priceView.setVisibility(GONE);
    CookingModifier modifier = viewData;
    List<Map<String, Object>> list = modifier.getOptions();
    int listPosition = 0;
    for (Map<String, Object> map : list) {
      optionsView.addView(createButton(map, listPosition, listPosition));
      listPosition++;
    }
    if (!modifier.getMandatory()) {
      optionsView.addView(createButton(getNonObject(), listPosition, listPosition));
    }
    optionsView.refreshDrawableState();
  }

  private View createButton(Map<String, Object> map, int tagId, int lp) {
    View button = LayoutInflater.from(getContext()).inflate(R.layout.modifier_item_radio, optionsView, false);
    button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

    RadioButton radioButton = (RadioButton) button.findViewById(R.id.modifier_item_radio_text);
    radioButton.setText(map.get(MAP_KEY_NAME).toString());
    radioButton.setTag(tagId);
    radioButton.setChecked(Boolean.valueOf(map.get(MAP_KEY_IS_DEFAULT).toString()));
    radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (isChecked) {
        setCheckOption(buttonView);
        sendCheckedPosition(lp);
      }
    });
    return button;
  }

  protected void sendCheckedPosition(int listPosition) {
    List<Map<String, Object>> list = viewData.getOptions();
    for (int i = 0; i < list.size(); i++) {
      list.get(i).put(MAP_KEY_IS_DEFAULT, i == listPosition);
      if (i == listPosition) {
        descriptionView.setText(list.get(i).get(MAP_KEY_NAME).toString());
      }
    }
    updateNoneOption(list.size() == listPosition);
  }

  protected void setCheckOption(View v) {
    int childCount = optionsView.getChildCount();
    for (int i = 0; i < childCount; i++) {
      int firstTag = Integer.parseInt(v.getTag().toString());
      RadioButton radioButton = (RadioButton) optionsView.getChildAt(i).findViewById(R.id.modifier_item_radio_text);
      int secondTag = Integer.parseInt(radioButton.getTag().toString());
      if (firstTag != secondTag) {
        radioButton.setChecked(false);
      }
    }
  }

  protected void setPrice(BigDecimal price) {
    priceView.setText(getContext().getString(R.string.format_price, price));
  }

  private void updateNoneOption(boolean selected) {
    if (noneOption != null) {
      noneOption.put(MAP_KEY_IS_DEFAULT, selected);
      if (selected) {
        descriptionView.setText(noneOption.get(MAP_KEY_NAME).toString());
      }
    }
  }

  private Map<String, Object> getNonObject() {
    if (noneOption == null) {
      noneOption = new HashMap<>();
      noneOption.put(MAP_KEY_NAME, getContext().getString(R.string.none));
      noneOption.put(MAP_KEY_IS_DEFAULT, true);
    }
    return noneOption;
  }

  public void swapOptionsVisibility() {
    boolean visible = optionsView.getVisibility() == GONE;
    expandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    optionsView.setVisibility(visible ? VISIBLE : GONE);
  }

}
