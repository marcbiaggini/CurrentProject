package com.rockspoon.kitchentablet.UI.ChefScreenOrders;

import android.app.ActivityManager;
import android.content.Context;
import android.view.KeyEvent;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.ItemsFiredUpdateEvent;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItem;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItemStatus;
import com.rockspoon.models.venue.ordering.item.ItemSummaryForKitchen;
import com.rockspoon.models.venue.ordering.item.ItemsFired;
import com.rockspoon.models.venue.ordering.item.ItemsFiringType;
import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.Tools;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/10/16.
 */
@EActivity
public abstract class ChefBaseActivity extends RSBaseActivity {
  protected List<ItemsFired> orderData = new ArrayList<>();
  protected List<ItemsFired> backupData = new ArrayList<>();
  protected Long productionAreaStationId;

  protected Object itemsFiredCallback = new Object() {
    @Subscribe
    public void onItemsFiredUpdated(ItemsFiredUpdateEvent itemsFiredUpdateEvent) {
      setupOrders(itemsFiredUpdateEvent.getListItemsFired());
    }
  };

  @AfterViews
  void init() {
    Tools.hideNavigationBar(this);
    getItemsFired();
    productionAreaStationId = RockServices.getDataService().getThisDevice().getStation().getId();
    RockServices.getDataService().getCacheBus().register(itemsFiredCallback);
  }

  @Background
  void getItemsFired() {
    try {
      List<ItemsFired> itemsFired = RockServices.getOrderService().listItemsFired();
      setupOrders(itemsFired);
    } catch (com.rockspoon.error.Error error) {
      onError(error);
    }
  }

  @Background
  void updateItemsFired(List<ItemsFired> updateItemsFired) {
    try {
      List<ItemsFired> itemsFired = RockServices.getOrderService().updateListItems(updateItemsFired);
      updateOrders(itemsFired);
      Timber.i("Updated Status: done array size =" + itemsFired.size());
    } catch (com.rockspoon.error.Error error) {
      onError(error);
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateOrders(final List<ItemsFired> newOrders) {
    List<ItemsFired> filteredOrders = filterOrders(newOrders);

    for (ItemsFired order : filteredOrders) {
      for (int index = 0; index < orderData.size(); index++) {
        if (orderData.get(index).getId().equals(order.getId())) {
          orderData.set(index, order);
          break;
        }
      }
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void setupOrders(final List<ItemsFired> itemsFired) {
    orderData = filterOrders(itemsFired);
  }

  protected List<ItemsFired> filterOrders(final List<ItemsFired> orders) {
    List<ItemsFired> filteredOrders = new ArrayList<>();

    // Skip items with DONT type
    for (ItemsFired order : orders) {
      if (order.getType() != ItemsFiringType.dont) {
        filteredOrders.add(order);
      }
    }

    return filteredOrders;
  }

  protected boolean isItemAssignedToOurStation(ItemSummaryForKitchen item) {
    for (ProductionAreaStationFiredItem pasFiredItem: item.getProductionAreaStationFiredItems()) {
      if (pasFiredItem.getProductionAreaStation().getId().equals(productionAreaStationId)) {
        return true;
      }
    }

    return false;
  }

  protected boolean orderHasItemsAssignedToOurStation(ItemsFired order) {
    for (ItemSummaryForKitchen itemSummary: order.getItemsInstances()) {
      if (isItemAssignedToOurStation(itemSummary)) {
        return true;
      }
    }

    return false;
  }

  protected void updatePASFItemsStatus(ItemSummaryForKitchen item, ProductionAreaStationFiredItemStatus status) {
    for (ProductionAreaStationFiredItem pasfItem: item.getProductionAreaStationFiredItems()) {
      if (pasfItem.getProductionAreaStation().getId().equals(productionAreaStationId)) {
        pasfItem.setStatus(status);
      }
    }
  }

  protected void saveToBackup(List<ItemsFired> items) {
    backupData.clear();
    for (ItemsFired item : items) {
      backupData.add(item.makeClone());
    }
  }

  protected void restoreFromBackup() {
    for (ItemsFired backupItem : backupData) {
      boolean found = false;

      for (int index = 0; index < orderData.size(); index++) {
        if (orderData.get(index).getId().equals(backupItem.getId())) {
          orderData.set(index, backupItem);
          found = true;
          break;
        }
      }

      if (!found) {
        orderData.add(backupItem);
      }
    }

    updateItemsFired(backupData);
  }

  @Override
  protected void onUserLeaveHint() {
    super.onUserLeaveHint();
    ((ActivityManager) getApplicationContext().
        getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
  }

  @Override
  protected void onPause() {
    super.onPause();
    ((ActivityManager) getApplicationContext().
        getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    RockServices.getDataService().getCacheBus().unregister(itemsFiredCallback);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_HOME:
      case KeyEvent.KEYCODE_BACK:
        return true;
      case KeyEvent.KEYCODE_MENU:
        onBackPressed();
        return true;
      default:
        return false;
    }
  }
}
