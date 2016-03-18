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

import com.rockspoon.rockandui.Managers.ItemModifiersManager;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Eugen K. on 3/4/16.
 */
public class ListItemSelectableWithPrice extends ModifierView<ItemModifiersManager.ItemPrices> {

  private TextView titleView;
  private TextView descriptionView;
  private TextView mandatoryView;
  private ImageView expandView;
  private TextView priceView;
  private RadioGroup optionsView;

  private SizeChangeListener sizeChangeListener;

  public ListItemSelectableWithPrice(Context context) {
    this(context, null);
  }

  public ListItemSelectableWithPrice(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ListItemSelectableWithPrice(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.modifier_item, this);
    setOrientation(VERTICAL);
    initViews();
  }

  public void setSizeChangeListener(SizeChangeListener sizeChangeListener) {
    this.sizeChangeListener = sizeChangeListener;
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
  public void setViewData(ItemModifiersManager.ItemPrices data) {
    super.setViewData(data);
    setUpUi(getContext().getString(R.string.placeholder_modifier_item_title), true);
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

    List<ItemModifiersManager.ItemPrice> prices = viewData.getPrices();
    int id = 0;
    int listPosition = 0;
    for (ItemModifiersManager.ItemPrice price : prices) {
      View button = LayoutInflater.from(getContext()).inflate(R.layout.modifier_item_radio, optionsView, false);
      button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

      RadioButton radioButton = (RadioButton) button.findViewById(R.id.modifier_item_radio_text);
      if (price.getTitle() != null && !price.getTitle().isEmpty()) {
        radioButton.setText(price.getTitle());
      }
      radioButton.setChecked(price.isSelected());
      radioButton.setTag(id++);
      if (price.isSelected()) {
        setPrice(price.getPrice());
      }

      TextView priceView = (TextView) button.findViewById(R.id.modifier_item_radio_price);
      priceView.setText(price.getPrice().toString());

      int lp = listPosition;
      radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          setCheckOption(buttonView);
          sendCheckedPosition(lp);
          setPrice(price.getPrice());
        }
      });
      optionsView.addView(button);
      listPosition++;
    }
    optionsView.refreshDrawableState();
  }

  protected void sendCheckedPosition(int listPosition) {
    List<ItemModifiersManager.ItemPrice> prices = viewData.getPrices();
    for (int i = 0; i < prices.size(); i++) {
      prices.get(i).setSelected(i == listPosition);
      if (i == listPosition) {
        descriptionView.setText(prices.get(i).getTitle());
        if (sizeChangeListener != null) {
          sizeChangeListener.onSizeChanged(prices.get(i).getTitle());
        }
      }
    }
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

  public void swapOptionsVisibility() {
    boolean visible = optionsView.getVisibility() == GONE;
    expandView.setImageResource(visible ? R.drawable.arrow_up : R.drawable.arrow_down);
    optionsView.setVisibility(visible ? VISIBLE : GONE);
  }

  public interface SizeChangeListener {
    void onSizeChanged(String sizeName);
  }

}
