package com.rockspoon.rockpos.Ordering.Fragments;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rockspoon.models.tag.Tag;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.ordering.OrderItem;
import com.rockspoon.models.venue.ordering.item.ItemInstance;
import com.rockspoon.models.venue.ordering.item.ItemInstanceDetails;
import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;
import com.rockspoon.models.venue.ordering.item.ItemInstanceStatus;
import com.rockspoon.rockandui.Adapters.ItemModifiersAdapter;
import com.rockspoon.rockandui.Adapters.OrderItemSeatAdapter;
import com.rockspoon.rockandui.Components.OrderingItemComponents.Selectable;
import com.rockspoon.rockandui.Components.OrderingItemSeat;
import com.rockspoon.rockandui.Interfaces.OnSelectedChanged;
import com.rockspoon.rockandui.Managers.ItemInstanceService;
import com.rockspoon.rockandui.Managers.ItemModifiersManager;
import com.rockspoon.rockandui.Objects.OrderingItemSettings;
import com.rockspoon.rockandui.RecyclerTools.DividerItemDecoration;
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
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by lucas on 03/08/15.
 */
@EFragment
public class RVOrderingItemFragment extends BaseOrderingFragment implements TextView.OnEditorActionListener {

  public static final String QUANTITY_HALF = "1/2";

  @FragmentArg
  Item item;

  @FragmentArg
  long menuCategoryId;

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

  @ViewById(R.id.quantity_increment_top)
  ImageButton plusTop;

  @ViewById(R.id.quantity_increment_bottom)
  ImageButton plusBottom;

  @ViewById(R.id.quantity_decrement_top)
  ImageButton minusTop;

  @ViewById(R.id.quantity_decrement_bottom)
  ImageButton minusBottom;

  @ViewById(R.id.quantity_top)
  EditText quantityTop;

  @ViewById(R.id.quantity_bottom)
  EditText quantityBottom;

  @ViewById(R.id.additions_price_amount)
  EditText additionPrice;

  @ViewById(R.id.scrollable_wrapper)
  NestedScrollView contentWrapper;

  @ViewById(R.id.add_to_order_and_create_duplication)
  Button addAndDuplicate;

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

  @ViewById(R.id.price_and_settings_rv)
  RecyclerView recyclerView;

  private OrderItemSeatAdapter orderItemSeatAdapter;
  private OrderingActions orderingActions;
  private ItemModifiersManager manager;
  private ItemModifiersAdapter modifiersAdapter;

  private ItemInstanceService itemInstanceService = new ItemInstanceService();
  private ItemInstanceRequest request;

  private int selectedSizeItem;
  private double quantity = 1;

  public static RVOrderingItemFragment newInstance(Item item, long menuCategoryId) {
    return RVOrderingItemFragment_.builder().item(item).menuCategoryId(menuCategoryId).build();
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
    quantity = quantity > 1 ? quantity - 1 : 1;
    updateTextViews();
  }

  @Click(R.id.add_to_order_and_create_duplication)
  void addToOrderAndDuplicate() {
    if (orderItemSeatAdapter.getOrderItems().isEmpty()) {
      Toast.makeText(getActivity(), R.string.ordering_select_customers, Toast.LENGTH_SHORT).show();
      return;
    } else if (!isMandatoryModifiersDefined()) {
      Toast.makeText(getActivity(), R.string.ordering_mandatory_modifiers, Toast.LENGTH_SHORT).show();
      return;
    }

    setupRequest();
    resetFragmentViews();
    if (orderingActions != null && request != null) {
      orderingActions.orderAndDuplicate(request);
    }
  }

  @Click(R.id.order_and_continue)
  void addToOrderAndContinue() {
    if (orderItemSeatAdapter.getOrderItems().isEmpty()) {
      Toast.makeText(getActivity(), R.string.ordering_select_customers, Toast.LENGTH_SHORT).show();
      return;
    } else if (!isMandatoryModifiersDefined()) {
      Toast.makeText(getActivity(), R.string.ordering_mandatory_modifiers, Toast.LENGTH_SHORT).show();
      return;
    }

    setupRequest();
    if (orderingActions != null && request != null) {
      orderingActions.orderAndContinue(request);
    }
  }

  @Click(R.id.add_to_order)
  void addToOrderClick() {
    if (orderItemSeatAdapter.getOrderItems().isEmpty()) {
      Toast.makeText(getActivity(), R.string.ordering_select_customers, Toast.LENGTH_SHORT).show();
      return;
    } else if (!isMandatoryModifiersDefined()) {
      Toast.makeText(getActivity(), R.string.ordering_mandatory_modifiers, Toast.LENGTH_SHORT).show();
      return;
    }

    setupRequest();
    if (orderingActions != null && request != null) {
      orderingActions.order(request);
    }
  }

  private void setupRequest() {
    ItemInstance itemInstance = createItemInstance();
    if (request == null) {
      request = itemInstanceService.createItemInstanceRequest(itemInstance, quantity);
    } else {
      request.getItemsToAdd().add(itemInstance);
    }
  }

  private ItemInstance createItemInstance() {
    ItemInstanceDetails details = itemInstanceService.createItemInstanceDetails(item, manager.getModifiersList(), menuCategoryId);
    Timber.i("DETAILS: " + details.toString());
    Set<OrderItem> orderItems = orderItemSeatAdapter.getOrderItems();
    return ItemInstance.builder().item(item).metadata(details).orderItems(orderItems)
        .created(new Timestamp(System.currentTimeMillis()))
        .status(ItemInstanceStatus.pending)
        .splitPlate(false)
        .comp(false)
        .build();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getOrder().diningParty == null) {
      clearFragmentsStack();
    }
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.rv_ordering_item_fragment;
  }

  @Override
  public String getFragmentTitle() {
    return item.getTitle();
  }

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    manager = new ItemModifiersManager(item);

    modifiersAdapter = new ItemModifiersAdapter(manager);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, R.dimen.modifier_top_margin));
    recyclerView.setAdapter(modifiersAdapter);

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

    extractTags();
    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
      descriptionView.setText(item.getDescription());
    }
    unitPriceView.setText(priceFormat(unitPrice(0)));
    totalPriceView.setText(priceFormat(countPrice(1)));
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

  private void updateTextViews() {
    quantityTop.setText(quantityString());
    quantityBottom.setText(quantityString());
    totalPriceView.setText(priceFormat(countPrice(quantity)));
  }

  private String quantityString() {
    return quantity == 0.5 ? QUANTITY_HALF : String.format(Locale.getDefault(), "%.0f", quantity);
  }

  private String priceFormat(BigDecimal value) {
    return String.format(Locale.getDefault(), "%.2f", value);
  }

  private BigDecimal countPrice(double quantity) {
    BigDecimal price = unitPrice(selectedSizeItem);
    price = price == null ? BigDecimal.ZERO : price.multiply(new BigDecimal(quantity));
    price = price.add(new BigDecimal(additionPrice.getText().toString().replaceAll(",", ".")));
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

  private void extractTags() {
    if (item.getTags() == null || item.getTags().isEmpty()) {
      tagsView.setVisibility(View.GONE);
      return;
    }

    StringBuilder builder = new StringBuilder();
    Iterator<Tag> iterator = item.getTags().iterator();

    while (iterator.hasNext()) {
      builder.append(getString(R.string.format_tag, iterator.next().getTag()));
      if (iterator.hasNext()) {
        builder.append(", ");
      }
    }
    tagsView.setText(builder.toString());
  }

  private boolean isMandatoryModifiersDefined() {
    for (int i = 0; i < recyclerView.getChildCount(); i++) {
      if (!((Selectable) recyclerView.getChildAt(i)).isConfigured()) {
        return false;
      }
    }
    return true;
  }

  private void resetFragmentViews() {
    contentWrapper.fullScroll(View.FOCUS_UP);
    manager.clearModifiers();
    manager.extractModiers();
    modifiersAdapter.notifyDataSetChanged();
  }

  private final OnSelectedChanged onSelectedChanged = () -> {
    boolean isSeveralSelected = orderItemSeatAdapter.getSelectedSeats().size() > 1;
    shareBtn.setEnabled(isSeveralSelected);
    splitBtn.setEnabled(isSeveralSelected);
    shareBtn.setChecked(!splitBtn.isChecked());
  };

}
