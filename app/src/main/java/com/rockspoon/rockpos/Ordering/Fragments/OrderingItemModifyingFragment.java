package com.rockspoon.rockpos.Ordering.Fragments;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.rockandui.Adapters.OrderItemSeatAdapter;
import com.rockspoon.rockandui.Components.ModifierItem;
import com.rockspoon.rockandui.Components.OrderingItemSeat;
import com.rockspoon.rockandui.Interfaces.OnModifierItemSelected;
import com.rockspoon.rockandui.Interfaces.OnSelectedChanged;
import com.rockspoon.rockandui.MockGenerator.ModifierQuantityGenerator;
import com.rockspoon.rockandui.MockGenerator.ModifierRadioGenerator;
import com.rockspoon.rockandui.MockGenerator.TableGenerator;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.Objects.OrderingItemSettings;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.Ordering.BaseOrderingFragment;
import com.rockspoon.rockpos.Ordering.OrderingActions;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lucas on 03/08/15.
 */
@Deprecated
@EFragment
public class OrderingItemModifyingFragment extends BaseOrderingFragment implements TextView.OnEditorActionListener {

  public static final String QUANTITY_HALF = "1/2";

  @FragmentArg
  Item item;

  @FragmentArg
  OrderingItemSettings settings;

  @ViewById(R.id.ordering_fragment_ois_top)
  OrderingItemSeat oisTop;

  @ViewById(R.id.ordering_fragment_ois_bottom)
  OrderingItemSeat oisBottom;

  @ViewById(R.id.split_btn)
  RadioButton splitBtn;

  @ViewById(R.id.share_btn)
  RadioButton shareBtn;

//  @ViewById(R.id.select_size)
//  ModifierItem selectSize;
//
//  @ViewById(R.id.crust_style)
//  ModifierItem crustStyle;
//
//  @ViewById(R.id.toppings)
//  ModifierItem toppings;

  @ViewById(R.id.quantity_top)
  EditText quantityTop;

  @ViewById(R.id.quantity_bottom)
  EditText quantityBottom;

  @ViewById(R.id.additions_price_amount)
  EditText additionPrice;

  @ViewById(R.id.scrollable_wrapper)
  NestedScrollView contentWrapper;

  @ViewById(R.id.food_tags)
  TextView tagsView;

  @ViewById(R.id.food_items)
  TextView descriptionView;

  @ViewById(R.id.total_price)
  TextView totalPriceView;

  @ViewById(R.id.unit_price)
  TextView unitPriceView;

  @ViewById(R.id.special_instructions)
  TextView specialInstructions;

  private OrderItemSeatAdapter orderItemSeatAdapter;
  private OrderingActions orderingActions;

  private int selectedSizeItem;
  private double quantity = 1;

  public static OrderingItemModifyingFragment newInstance(Item item) {
    return OrderingItemModifyingFragment_.builder().item(item).build();
  }

  public static OrderingItemModifyingFragment newInstance(Item item, OrderingItemSettings settings) {
    return OrderingItemModifyingFragment_.builder().item(item).settings(settings).build();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getActivity() instanceof OrderingActions) {
      orderingActions = (OrderingActions) getActivity();
    }
  }

  @Click({R.id.quantity_increment_top, R.id.quantity_increment_bottom})
  void plusClick() {
    quantity = quantity >= 1 ? quantity + 1 : 1;
    updateTextViews();
  }

  @Click({R.id.quantity_decrement_top, R.id.quantity_decrement_bottom})
  void minusClick() {
    quantity = quantity > 1 ? quantity - 1 : 0.5;
    updateTextViews();
  }

  @Click(R.id.order_and_duplicate)
  void addAndDuplicateClick() {
    settings.setAdditionalPrice(new BigDecimal(additionPrice.getText().toString()));
    settings.setQuantity((int) quantity);
    settings.setSeats(oisTop.getSelectedSeats());
    settings.setSelectedSizeId(selectedSizeItem);
    settings.setSpecialInstructions(specialInstructions.getText().toString());

    if (orderingActions != null) {
//      orderingActions.orderAndDuplicate(item, settings);
    }
  }

  @Click(R.id.order_and_continue)
  void orderAndContinue() {
    if (orderingActions != null) {
//      orderingActions.orderAndContinue(item);
    }
  }

  @Click(R.id.order)
  void order() {
    if (orderingActions != null) {
//      orderingActions.order(item);
    }
  }

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    final ModifierRadioGenerator gen1 = new ModifierRadioGenerator(getActivity(), false);
    final ModifierQuantityGenerator gen2 = new ModifierQuantityGenerator(getActivity());

//    crustStyle.setAdapter(gen1.getAdapter());
//    toppings.setAdapter(gen2.getAdapter());

    orderItemSeatAdapter = new OrderItemSeatAdapter(getActivity(), getOrder().diningParty.getDinerSessions());
    if (isAllSeatsSelected()) {
      orderItemSeatAdapter.selectAllSeats();
      oisTop.toggleButton();
    } else if (isValidSeatNumber()) {
      orderItemSeatAdapter.select(getSeatNumber() - 1);
    }
    oisTop.setAdapter(orderItemSeatAdapter);
    oisBottom.setAdapter(orderItemSeatAdapter);

    oisTop.setOnSelectedChanged(onSelectedChanged);
    oisBottom.setOnSelectedChanged(onSelectedChanged);
    quantityTop.setOnEditorActionListener(this);
    quantityBottom.setOnEditorActionListener(this);
    additionPrice.setOnEditorActionListener(this);

    Tools.setupHideKeyboardListeners(contentWrapper, getActivity());
    setScrollableView(contentWrapper);

    if (settings != null) {
      setUiValues(settings);
    } else {
      final ModifierRadioGenerator selectSizeGenerator = item.getPrices() != null ? new ModifierRadioGenerator(getActivity(), item.getPrices()) : new ModifierRadioGenerator(getActivity(), true);
//      selectSize.setAdapter(selectSizeGenerator.getAdapter());
//      selectSize.setExternalOnModifierItemSelected(externalOnModifierItemSelected);
      settings = new OrderingItemSettings();
    }

    tagsView.setText(extractTags());
    descriptionView.setText(item.getDescription());
    unitPriceView.setText(priceFormat(unitPrice(0)));
    totalPriceView.setText(priceFormat(countPrice(1)));
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.ordering_item_fragment;
  }

  @Override
  public void onDetach() {
    if (oisTop != null && oisBottom != null) {
      oisTop.onDestroy();
      oisBottom.onDestroy();
    }
    super.onDetach();
  }

  @Override
  public String getFragmentTitle() {
    return item.getTitle();
  }

  @Override
  public void onBackClicked() {
    super.onBackClicked();
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (actionId == EditorInfo.IME_ACTION_DONE) {
      switch (v.getId()) {
        case R.id.quantity_top:
          quantityBottom.setText(quantityTop.getText());
          quantityBottom.clearFocus();
          break;
        case R.id.quantity_bottom:
          quantityTop.setText(quantityBottom.getText());
          quantityBottom.clearFocus();
          break;
        case R.id.additions_price_amount:
          additionPrice.clearFocus();
          totalPriceView.setText(priceFormat(countPrice(quantity)));
          break;
      }
      contentWrapper.requestFocus();
      Tools.hideSoftKeyboard(getActivity());
    }
    return false;
  }

  private BigDecimal countPrice(double quantity) {
    BigDecimal price = unitPrice(selectedSizeItem);
    price = price == null ? BigDecimal.ZERO : price.multiply(new BigDecimal(quantity));
    price = price.add(new BigDecimal(additionPrice.getText().toString()));
    return price;
  }

  private BigDecimal unitPrice(int itemId) {
    Map<String, BigDecimal> prices = item.getPrices();
    if (prices == null) {
      return BigDecimal.ZERO;
    }
    int i = 0;
    BigDecimal price = null;
    for (Map.Entry<String, BigDecimal> entry : prices.entrySet()) {
      if (i == itemId) {
        price = entry.getValue();
      }
      i++;
    }
    return price;
  }

  private void setUiValues(OrderingItemSettings settings) {
    quantityBottom.setText(String.valueOf(settings.getQuantity()));
    quantityTop.setText(String.valueOf(settings.getQuantity()));
    additionPrice.setText(priceFormat(settings.getAdditionalPrice()));

    selectedSizeItem = settings.getSelectedSizeId();
    for (Pair<Integer, DinerSession> pair : settings.getSeats()) {
      oisTop.getAdapter().select(pair.first);
    }
    specialInstructions.setText(settings.getSpecialInstructions());

    final ModifierRadioGenerator selectSizeGenerator = item.getPrices() != null ? new ModifierRadioGenerator(getActivity(), item.getPrices()) : new ModifierRadioGenerator(getActivity(), true);
//    selectSize.setAdapter(selectSizeGenerator.getAdapter());
//    selectSize.setExternalOnModifierItemSelected(externalOnModifierItemSelected);
    totalPriceView.setText(priceFormat(countPrice(settings.getQuantity())));
  }

  private void updateTextViews() {
    quantityTop.setText(quantityString());
    quantityBottom.setText(quantityString());
    totalPriceView.setText(priceFormat(countPrice(quantity)));
  }

  private String extractTags() {
    if (item.getTags() == null) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    Iterator<Tag> iterator = item.getTags().iterator();

    while (iterator.hasNext()) {
      builder.append(getString(R.string.format_tag, iterator.next().getTag()));
      if (iterator.hasNext()) {
        builder.append(", ");
      }
    }
    return builder.toString();
  }

  private String quantityString() {
    return quantity == 0.5 ? QUANTITY_HALF : String.format(Locale.getDefault(), "%.0f", quantity);
  }

  private String priceFormat(BigDecimal value) {
    return String.format(Locale.getDefault(), "%.2f", value);
  }

  private final OnModifierItemSelected externalOnModifierItemSelected = itemId -> {
    selectedSizeItem = itemId;
    unitPriceView.setText(unitPrice(itemId).toString());
  };

  private final OnSelectedChanged onSelectedChanged = () -> {
    boolean isSeveralSelected = orderItemSeatAdapter.getSelectedSeats().size() > 1;
    shareBtn.setEnabled(isSeveralSelected);
    splitBtn.setEnabled(isSeveralSelected);
    shareBtn.setChecked(!splitBtn.isChecked());
  };

}
