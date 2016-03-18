package com.rockspoon.rockpos.SplitBill;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.item.sizeprice.SizePrice;
import com.rockspoon.rockandui.Adapters.SplitOrderItemsAdapter;
import com.rockspoon.rockandui.Dialogs.SplitBillItemDialog;
import com.rockspoon.rockandui.DragComponents.BoardView;
import com.rockspoon.rockandui.TestDataModel.OrderPaymentData;
import com.rockspoon.rockandui.TestDataModel.SeatOrder;
import com.rockspoon.rockandui.TestDataModel.SeatOrderItem;
import com.rockspoon.rockandui.TestDataModel.TableOrder;
import com.rockspoon.rockpos.Payment.PaymentActivity;
import com.rockspoon.rockpos.Payment.PaymentActivity_;
import com.rockspoon.rockpos.PaymentCompleted.PaymentSplashActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yury Minich on 1/15/16.
 */

@EActivity(R.layout.split_bill_activity)
public class SplitBillActivity extends Activity implements SplitOrderItemsAdapter.ItemsListEventsListener, SplitBillItemDialog.SplitByListener {

  public static final String SPLIT_TYPE_EXTRA_TAG = "split_type";
  public static final String ORDER_DATA_EXTRA_TAG = "order_data";

  @ViewById(R.id.undo_split_container)
  RelativeLayout undoSplitContainer;

  @ViewById(R.id.undo_split_button)
  Button undoSplitButton;

  @ViewById(R.id.page_title)
  TextView pageTitle;

  @ViewById(R.id.board_view)
  BoardView boardView;

  @Extra(SPLIT_TYPE_EXTRA_TAG)
  void setSplitType(int type) {
    splitType = type;
  }

  @Extra(ORDER_DATA_EXTRA_TAG)
  void setOrderData(TableOrder order) {
    orderData = order;
  }

  private TableOrder orderData;
  private List<SeatOrder> seatOrderList;
  private int splitType;

  private HashMap<Integer, SplitOrderItemsAdapter> seatAdapterList;

  @Click(R.id.undo_split_button)
  void undoSplit() {
    seatOrderList = cloneSeatOrderList(orderData.getSeatOrderList());
    for (int i = 0; i < seatOrderList.size(); i++) {
      SeatOrder seatOrder = seatOrderList.get(i);
      if (seatAdapterList.get(seatOrder.getSeatNumber()) != null) {
        seatAdapterList.get(seatOrder.getSeatNumber()).setItemList(seatOrder.getSeatOrderItemList());
      }
      recountHeader(i);
    }
    undoSplitContainer.setVisibility(View.GONE);
  }

  @Click(R.id.back_button)
  void backButtonClick() {
    onBackPressed();
  }

  @AfterViews
  void initAfterViews() {
    boardView.setSnapToColumnsWhenScrolling(false);
    boardView.setSnapToColumnWhenDragging(false);
    boardView.setSnapDragItemToTouch(true);

    if (orderData != null) {
      if (SplitOrderItemsAdapter.SPLIT_TYPE_EQUALLY == splitType) {
        splitOrderEqually(orderData);
      } else {
        fillData(orderData);
      }
    }

    boardView.setBoardListener(new BoardView.BoardListener() {
      @Override
      public void onItemDragStarted(int column, int row) {
      }

      @Override
      public void onItemChangedColumn(int oldColumn, int newColumn) {
        if (oldColumn != newColumn) {
          recountHeader(oldColumn);
          recountHeader(newColumn);
        }
      }

      @Override
      public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
      }
    });
  }


  @UiThread
  void fillData(TableOrder testOrderData) {
    seatAdapterList = new HashMap<>();
    if (orderData == null) {
      orderData = testOrderData;
    }

    seatOrderList = cloneSeatOrderList(orderData.getSeatOrderList());
    for (SeatOrder order : seatOrderList) {
      addColumnList(order);
    }
  }

  private void addColumnList(SeatOrder seatOrder) {
    SplitOrderItemsAdapter listAdapter = new SplitOrderItemsAdapter(this, seatOrder.getSeatOrderItemList(),
        seatOrder.getSeatNumber(), splitType, this, true);
    seatAdapterList.put(seatOrder.getSeatNumber(), listAdapter);

    final View header = View.inflate(this, R.layout.split_bill_column_header, null);

    TextView seatNumber = (TextView) header.findViewById(com.rockspoon.rockandui.R.id.seat_number);
    TextView seatOrderSubtotal = (TextView) header.findViewById(com.rockspoon.rockandui.R.id.order_subtotal);
    TextView seatOrderTax = (TextView) header.findViewById(com.rockspoon.rockandui.R.id.order_tax);
    TextView seatOrderTotal = (TextView) header.findViewById(com.rockspoon.rockandui.R.id.order_total);
    Button payBillButton = (Button) header.findViewById(com.rockspoon.rockandui.R.id.pay_button);
    TextView paidLabel = (TextView) header.findViewById(com.rockspoon.rockandui.R.id.label_paid);

    SeatHeaderViewHolder holder = new SeatHeaderViewHolder();

    holder.seatId = seatOrder.getSeatNumber();
    holder.seatNumber = seatNumber;
    holder.seatOrderSubtotal = seatOrderSubtotal;
    holder.seatOrderTax = seatOrderTax;
    holder.seatOrderTotal = seatOrderTotal;
    holder.payButton = payBillButton;
    holder.paidLabel = paidLabel;

    header.setTag(holder);

    fillHeader(seatOrder, header);

    boardView.addColumnList(listAdapter, header, false);
  }


  private void fillHeader(SeatOrder order, View header) {
    SeatHeaderViewHolder holder = (SeatHeaderViewHolder) header.getTag();

    List<SeatOrderItem> seatOrderItemList = order.getSeatOrderItemList();

    holder.seatNumber.setText(String.format(getString(com.rockspoon.rockandui.R.string.format_seat_n), order.getSeatNumber()));

    BigDecimal subTotal = BigDecimal.ZERO;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO;

    if (seatOrderItemList != null && !seatOrderItemList.isEmpty()) {
      subTotal = countSubTotalAmount(seatOrderItemList);
      tax = subTotal.multiply(OrderPaymentData.TAX_PERCENT);
      total = subTotal.add(tax);
      OrderPaymentData paymentData = new OrderPaymentData(order.getSeatNumber() - 1, orderData.getTableNumber(), getNonPaidAmount(), subTotal);

      holder.payButton.setOnClickListener(v ->
          PaymentActivity_.intent(this)
              .extra(OrderPaymentData.EXTRA_ORDER_DATA, paymentData)
              .startForResult(PaymentActivity.REQUEST_CODE_PAYED));
      holder.payButton.setEnabled(true);
    } else {
      holder.payButton.setEnabled(false);
    }

    String subTotalText = String.format(getString(com.rockspoon.rockandui.R.string.format_price), subTotal);
    holder.seatOrderSubtotal.setText(subTotalText);

    String taxText = String.format(getString(com.rockspoon.rockandui.R.string.format_price), tax);
    String totalText = String.format(getString(com.rockspoon.rockandui.R.string.format_price), total);

    holder.seatOrderTax.setText(taxText);
    holder.seatOrderTotal.setText(totalText);
  }

  @Background
  void splitOrderEqually(TableOrder orderData) {
    long id = 0;
    List<SeatOrderItem> allOrderedItems = new ArrayList<>();
    // Find all ordered items
    List<SeatOrder> seatOrders = orderData.getSeatOrderList();
    for (SeatOrder order : seatOrders) {
      List<SeatOrderItem> orderItemList = order.getSeatOrderItemList();
      for (SeatOrderItem orderedItem : orderItemList) {
        if (allOrderedItems.contains(orderedItem)) {
          int position = allOrderedItems.indexOf(orderedItem);
          int itemsCount = allOrderedItems.get(position).getItemsCount();
          allOrderedItems.get(position).setItemsCount(itemsCount + orderedItem.getItemsCount());
        } else {
          allOrderedItems.add(orderedItem);
        }
      }
    }
    // Split all ordered items between seats
    for (SeatOrder order : seatOrders) {
      order.getSeatOrderItemList().clear();
      List<SeatOrderItem> orderItemList = new ArrayList<>();
      for (SeatOrderItem item : allOrderedItems) {
        SeatOrderItem splittedItem = new SeatOrderItem(item);
        splittedItem.setSplitByCount(seatOrders.size());
        splittedItem.setItemId(id++);
        orderItemList.add(splittedItem);
      }
      order.setSeatOrderItemList(orderItemList);
    }
    fillData(orderData);
  }

  private void splitItem(int position, int seatNumber, int count) {
    for (SeatOrder order : seatOrderList) {
      if (seatNumber == order.getSeatNumber()) {
        int columnPos = seatOrderList.indexOf(order);
        List<SeatOrderItem> orderItemList = order.getSeatOrderItemList();
        SeatOrderItem item = orderItemList.get(position);
        orderItemList.remove(position);
        long itemId = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
          SeatOrderItem splittedItem = new SeatOrderItem(item);
          splittedItem.setSplitByCount(item.getSplitByCount() * count);
          splittedItem.setItemId(itemId + i);
          orderItemList.add(position + i, splittedItem);
        }
        boardView.getAdapter(columnPos).notifyDataSetChanged();
        break;
      }
    }
  }

  private List<SeatOrder> cloneSeatOrderList(List<SeatOrder> orderList) {
    List<SeatOrder> destList = new ArrayList<>();
    for (SeatOrder order : orderList) {
      SeatOrder destOrder = new SeatOrder(order.getSeatNumber(), new ArrayList<>(order.getSeatOrderItemList()));
      destList.add(destOrder);
    }
    return destList;
  }

  @Override
  public void onSplitItemClick(int position, int seatNumber) {
    SplitBillItemDialog dialog = SplitBillItemDialog.newInstance(position, seatNumber, orderData.getSeatOrderList().size());
    dialog.setListener(this);
    dialog.show(getFragmentManager(), "SplitBillItemDialog");
  }

  @Override
  public void onSplitBy(int position, int seatNumber, int count) {
    splitItem(position, seatNumber, count);

    if (seatAdapterList.size() > position && seatAdapterList.get(position) != null) {
      seatAdapterList.get(seatNumber).notifyDataSetChanged();
    }

    if (View.VISIBLE != undoSplitContainer.getVisibility()) {
      undoSplitContainer.setVisibility(View.VISIBLE);
    }
  }

  static class SeatHeaderViewHolder {
    public int seatId;
    public TextView seatNumber;
    public TextView seatOrderSubtotal;
    public TextView seatOrderTax;
    public TextView seatOrderTotal;
    public Button payButton;
    public TextView paidLabel;
  }

  private void recountHeader(int columnNumber) {
    SeatOrder seatOrder = seatOrderList.get(columnNumber);
    BigDecimal subTotal = BigDecimal.ZERO;
    BigDecimal tax;
    BigDecimal total;

    List<SeatOrderItem> seatOrderItemList = seatOrder.getSeatOrderItemList();
    View headerView = boardView.getHeaderView(columnNumber);
    SeatHeaderViewHolder holder = (SeatHeaderViewHolder) headerView.getTag();

    if (seatOrderItemList != null && !seatOrderItemList.isEmpty()) {
      subTotal = countSubTotalAmount(seatOrderItemList);
      holder.payButton.setEnabled(true);
    } else {
      holder.payButton.setEnabled(false);
    }

    if (seatOrder.isPaid()) {
      holder.payButton.setVisibility(View.INVISIBLE);
      holder.paidLabel.setVisibility(View.VISIBLE);
    } else {
      holder.payButton.setVisibility(View.VISIBLE);
      holder.paidLabel.setVisibility(View.INVISIBLE);
    }

    holder.seatOrderSubtotal.setText(String.format(getString(com.rockspoon.rockandui.R.string.format_price), subTotal));

    tax = subTotal.multiply(OrderPaymentData.TAX_PERCENT);
    total = subTotal.add(tax);

    holder.seatOrderTax.setText(String.format(getString(com.rockspoon.rockandui.R.string.format_price), tax));
    holder.seatOrderTotal.setText(String.format(getString(com.rockspoon.rockandui.R.string.format_price), total));
  }

  private BigDecimal countSubTotalAmount(List<SeatOrderItem> seatOrderItemList) {
    BigDecimal subTotal = BigDecimal.ZERO;
    for (SeatOrderItem item : seatOrderItemList) {
      Item dish = item.getItem();
      if (dish.getPrices() != null && !dish.getPrices().isEmpty()) {
        SizePrice price = dish.getSizePrices().get(0);
        int splitBy = item.getSplitByCount();
        subTotal = subTotal.add(price.getPrice().multiply((new BigDecimal(item.getItemsCount()).divide(new BigDecimal(splitBy), OrderPaymentData.SCALE, RoundingMode.HALF_UP))));
      }
    }
    return subTotal;
  }

  private BigDecimal getNonPaidAmount() {
    BigDecimal nonPaidAmount = BigDecimal.ZERO;
    for (SeatOrder seatOrder : seatOrderList) {
      List<SeatOrderItem> seatOrderItemList = seatOrder.getSeatOrderItemList();
      if (seatOrderItemList != null && !seatOrderItemList.isEmpty() && !seatOrder.isPaid()) {
        nonPaidAmount = nonPaidAmount.add(countSubTotalAmount(seatOrderItemList));
      }
    }
    return nonPaidAmount;
  }

  @OnActivityResult(PaymentActivity.REQUEST_CODE_PAYED)
  void onResult(int resultCode, @OnActivityResult.Extra(value = ORDER_DATA_EXTRA_TAG) OrderPaymentData order) {
    if (resultCode == RESULT_OK && order != null) {
      int seatIndex = order.getPayerId();
      SeatOrder currentSeatOrder = seatOrderList.get(seatIndex);
      currentSeatOrder.setPaid(true);

      // check if all seat orders are paid
      boolean allSeatsPaid = true;
      for (SeatOrder seatOrder : seatOrderList) {
        if (!seatOrder.isPaid()) {
          allSeatsPaid = false;
          break;
        }
      }

      if (allSeatsPaid) {
        // show splash screen
        PaymentSplashActivity_.intent(this).start();
        finish();
      } else {
        // update UI
        recountHeader(seatIndex);
        SplitOrderItemsAdapter orderItemsAdapter = seatAdapterList.get(seatIndex + 1);
        orderItemsAdapter.setPaid(true);
      }
    }
  }

}
