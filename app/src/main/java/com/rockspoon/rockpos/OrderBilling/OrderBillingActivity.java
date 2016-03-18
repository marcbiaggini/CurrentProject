package com.rockspoon.rockpos.OrderBilling;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.rockandui.Adapters.SectionedRecyclerViewAdapter;
import com.rockspoon.rockandui.Adapters.SplitOrderItemsAdapter;
import com.rockspoon.rockandui.MockGenerator.CategoryGenerator;
import com.rockspoon.rockandui.RecyclerTools.DividerItemDecoration;
import com.rockspoon.rockandui.TestDataModel.OrderMenuCategory;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockandui.TestDataModel.SeatOrder;
import com.rockspoon.rockandui.TestDataModel.SeatOrderItem;
import com.rockspoon.rockandui.TestDataModel.TableOrder;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.CustomBillSplit.CustomBillSplitActivity;
import com.rockspoon.rockpos.CustomBillSplit.CustomBillSplitActivity_;
import com.rockspoon.rockpos.OrderBilling.Adapters.OrderBillingAdapter;
import com.rockspoon.rockpos.Payment.PaymentActivity;
import com.rockspoon.rockpos.Payment.PaymentActivity_;
import com.rockspoon.rockpos.PaymentCompleted.PaymentSplashActivity_;
import com.rockspoon.rockpos.R;
import com.rockspoon.rockpos.SplitBill.SplitBillActivity;
import com.rockspoon.rockpos.SplitBill.SplitBillActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yury Minich on 1/13/16.
 */
@EActivity(R.layout.order_billing_activity)
public class OrderBillingActivity extends Activity {

  public static final String EXTRA_TABLE_NUMBER = "table_number";

  private final DecimalFormat df = new DecimalFormat("0.00");

  private List<OrderMenuCategory> categoryData;
  private List<SeatOrderItem> allOrderedItems;


  private OrderBillingAdapter orderAdapter;

  private OrderPaymentData orderPaymentData;
  private TableOrder orderData;
  private String tableNumber;

  @Extra(EXTRA_TABLE_NUMBER)
  void setExtraTableNumber(String table) {
    tableNumber = table;
  }

  @ViewById(R.id.page_title)
  TextView pageTitle;

  @ViewById(R.id.order_list)
  RecyclerView orderItemsList;

  @ViewById(R.id.order_subtotal)
  TextView orderSubtotalView;

  @ViewById(R.id.order_tax)
  TextView orderTaxView;

  @ViewById(R.id.order_total)
  TextView orderTotalView;

  @Click(R.id.back_button)
  void backButtonClick() {
    onBackPressed();
  }

  @Click(R.id.one_person_pays)
  void onePersonPays() {
    PaymentActivity_.intent(OrderBillingActivity.this)
        .extra(OrderPaymentData.EXTRA_ORDER_DATA, orderPaymentData)
        .startForResult(PaymentActivity.REQUEST_CODE_PAYED);
  }

  @Click(R.id.split_the_bill_equally)
  void splitBillEqually() {
    SplitBillActivity_.intent(OrderBillingActivity.this)
        .extra(SplitBillActivity.SPLIT_TYPE_EXTRA_TAG, SplitOrderItemsAdapter.SPLIT_TYPE_EQUALLY)
        .extra(SplitBillActivity.ORDER_DATA_EXTRA_TAG, orderData)
        .start();
  }

  @Click(R.id.split_by_each_person)
  void splitByEachPerson() {
    SplitBillActivity_.intent(OrderBillingActivity.this)
        .extra(SplitBillActivity.SPLIT_TYPE_EXTRA_TAG, SplitOrderItemsAdapter.SPLIT_TYPE_BY_PERSON_ORDERED)
        .extra(SplitBillActivity.ORDER_DATA_EXTRA_TAG, orderData)
        .start();
  }

  @Click(R.id.custom_split)
  void splitCustom() {
    if (orderData != null) {
      int seatsCount = orderData.getSeatOrderList().size() - 1;
      CustomBillSplitActivity_.intent(OrderBillingActivity.this)
          .extra(OrderPaymentData.EXTRA_ORDER_DATA, orderPaymentData)
          .extra(CustomBillSplitActivity.EXTRA_SEATS_COUNT, seatsCount)
          .start();
    }
  }

  @AfterViews
  void initAfterViews() {
    allOrderedItems = new ArrayList<>();

    orderItemsList.setHasFixedSize(false);
    orderItemsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    DividerItemDecoration decorator = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
    decorator.setDividerHeightOffset(Tools.dpToPx(this, 20));
    orderItemsList.addItemDecoration(decorator);

    loadItemsUi();
  }

  @UiThread
  protected void loadItemsUi() {
    generateTestData();
  }


  @Background
  void generateTestData() {
    long id = 0;
    TableOrder testOrderData = new TableOrder();
    CategoryGenerator categoryGenerator = new CategoryGenerator(this);
    List<MenuCategory> categoryData = categoryGenerator.getCategoryData();
    List<OrderMenuCategory> categories = new ArrayList<>();
    List<SeatOrder> seatOrderEntityList = new ArrayList<>();
    testOrderData.setOrderId(1);
    testOrderData.setTableNumber(tableNumber);
    int i = 1;
    BigDecimal totalAmount = BigDecimal.valueOf(0);
    for (MenuCategory category : categoryData) {
      SeatOrder seatOrder = new SeatOrder();
      seatOrder.setSeatNumber(i);
      List<Item> items = category.getItems();
      List<SeatOrderItem> seatItemsList = new ArrayList<>();
      for (int j = 0; j < items.size(); j++) {
        int count = 1;
        if (j % 2 == 0) {
          count = 2;
        }
        SeatOrderItem seatItem = new SeatOrderItem(id++, count, items.get(j), i);
        seatItemsList.add(seatItem);
        SizePrice price = seatItem.getItem().getSizePrices().get(0);
        totalAmount = totalAmount.add(price.getPrice().multiply(new BigDecimal(seatItem.getItemsCount())));

      }
      seatOrder.setSeatOrderItemList(seatItemsList);
      OrderMenuCategory orderCategory = new OrderMenuCategory(category, seatItemsList);
      seatOrderEntityList.add(seatOrder);
      i++;
      categories.add(orderCategory);
    }
    testOrderData.setSeatOrderList(seatOrderEntityList);
    updateCategories(testOrderData, categories, totalAmount);
  }


  @UiThread
  void updateCategories(TableOrder testOrderData, final List<OrderMenuCategory> categories, BigDecimal total) {
    categoryData = categories;
    orderData = testOrderData;

    pageTitle.setText(String.format(getString(R.string.format_payment_table), orderData.getTableNumber()));

    List<SectionedRecyclerViewAdapter.Section> sections = new ArrayList<>();

    for (OrderMenuCategory menuCategory : categoryData) {
      List<SeatOrderItem> items = menuCategory.getItems();

      if (items != null && !items.isEmpty()) {
        if (sections.isEmpty()) {
          sections.add(new SectionedRecyclerViewAdapter.Section(0, menuCategory.getTitle()));
        } else {
          sections.add(new SectionedRecyclerViewAdapter.Section(allOrderedItems.size(), menuCategory.getTitle()));
        }
        allOrderedItems.addAll(items);
      }
    }
    total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
    orderPaymentData = new OrderPaymentData(tableNumber, total, total);

    orderSubtotalView.setText(df.format(orderPaymentData.getOrderSubtotal()));
    orderTaxView.setText(df.format(orderPaymentData.getOrderTax()));
    orderTotalView.setText(df.format(orderPaymentData.getOrderTotalWithoutTips()));

    orderAdapter = new OrderBillingAdapter(this, allOrderedItems);

    SectionedRecyclerViewAdapter.Section[] dummy = new SectionedRecyclerViewAdapter.Section[sections.size()];
    SectionedRecyclerViewAdapter sectionedAdapter = new SectionedRecyclerViewAdapter(this, R.layout.order_billing_list_section, R.id.section_text, orderAdapter);
    sectionedAdapter.setSections(sections.toArray(dummy));

    orderItemsList.setAdapter(sectionedAdapter);
  }

  @OnActivityResult(PaymentActivity.REQUEST_CODE_PAYED)
  void onResult(int resultCode, @OnActivityResult.Extra(value = OrderPaymentData.EXTRA_ORDER_DATA) OrderPaymentData order) {
    if (resultCode == RESULT_OK && order != null) {
      if (order.isPaid()) {
        PaymentSplashActivity_.intent(this).start();
        finish();
      }
    }
  }
}
