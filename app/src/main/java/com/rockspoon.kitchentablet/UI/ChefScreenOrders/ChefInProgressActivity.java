package com.rockspoon.kitchentablet.UI.ChefScreenOrders;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.kitchentablet.Adapters.ChefDoneOrdersAdapter;
import com.rockspoon.kitchentablet.Adapters.ChefOrdersAdapter;
import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItemStatus;
import com.rockspoon.models.venue.ordering.item.ItemFiredStatus;
import com.rockspoon.models.venue.ordering.item.ItemInstanceStatus;
import com.rockspoon.models.venue.ordering.item.ItemSummaryForKitchen;
import com.rockspoon.models.venue.ordering.item.ItemsFired;
import com.rockspoon.models.venue.ordering.item.ItemsFiringType;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ChefInProgressActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/1/16.
 */
@EActivity(R.layout.kitchen_chef_in_progress_activity)
public class ChefInProgressActivity extends ChefBaseActivity {

  @ViewById(R.id.orders_grid)
  RecyclerView ordersGrid;

  @ViewById(R.id.done_list)
  RecyclerView doneList;

  @ViewById(R.id.undo_button)
  Button undoButton;

  @ViewById(R.id.done_button)
  Button doneButton;

  @ViewById(R.id.profileImage)
  RoundImageView profileImage;

  @ViewById(R.id.swipe_container)
  SwipeRefreshLayout swipeRefreshLayout;

  private List<ItemsFired> doneOrders = new ArrayList<>();
  private List<ItemsFired> inProgressOrders = new ArrayList<>();
  private ChefOrdersAdapter ordersAdapter;
  private ChefDoneOrdersAdapter doneOrdersAdapter;

  @Override
  void init() {
    super.init();

    ordersAdapter = new ChefOrdersAdapter(this, inProgressOrders, selectedOrders -> doneButton.setEnabled(selectedOrders.size() > 0));
    ordersAdapter.setViewMode(ChefOrdersAdapter.OrdersViewMode.IN_PROGRESS);
    ordersGrid.setAdapter(ordersAdapter);
    ordersGrid.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

    doneOrdersAdapter = new ChefDoneOrdersAdapter(this, doneOrders);
    doneList.setAdapter(doneOrdersAdapter);
    doneList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    swipeRefreshLayout.setOnRefreshListener(this::getItemsFired);

    setupUserProfileImage();
  }

  @Override
  void setupOrders(List<ItemsFired> itemsFired) {
    super.setupOrders(itemsFired);
    separateOrders();
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  void updateOrders(List<ItemsFired> newOrders) {
    super.updateOrders(newOrders);
    separateOrders();
  }

  @Override
  protected List<ItemsFired> filterOrders(List<ItemsFired> orders) {
    List<ItemsFired> parentFilteredOrders = super.filterOrders(orders);
    List<ItemsFired> filteredOrders = new ArrayList<>();

    for (ItemsFired order : parentFilteredOrders) {
      // We need to display (order must be with IN_PROGRESS or DONE status):
      // 1. For TOGETHER fire type orders --- All items, if we assigned to at least one of items.
      // 2. For other fire type orders --- Only items with IN_PROGRESS or READY_FOR_DELIVERY statuses, assigned to us.

      if ((order.getStatus() != ItemFiredStatus.in_progress && order.getStatus() != ItemFiredStatus.done) ||
          !orderHasItemsAssignedToOurStation(order)) {
        continue;
      }

      if (order.getType() == ItemsFiringType.together) {
        filteredOrders.add(order);
        continue;
      }

      Set<ItemSummaryForKitchen> items = new HashSet<>();
      for (ItemSummaryForKitchen itemSummary : order.getItemsInstances()) {
        if ((itemSummary.getStatus() == ItemInstanceStatus.in_progress || itemSummary.getStatus() == ItemInstanceStatus.ready_for_delivery) &&
            isItemAssignedToOurStation(itemSummary)) {
          items.add(itemSummary);
        }
      }

      if (items.size() > 0) {
        filteredOrders.add(order.withItemsInstances(items));
      }
    }

    return filteredOrders;
  }

  @Click(R.id.done_button)
  void doneButtonClicked() {
    List<ItemsFired> selectedOrders = ordersAdapter.getSelectedOrders();
    List<ItemsFired> backupOrders = new ArrayList<>();
    List<ItemsFired> modifiedOrders = new ArrayList<>();

    // mark all selected items as DONE
    for (ItemsFired selectedOrder: selectedOrders) {
      for (ItemsFired order: orderData) {
        if (!selectedOrder.getId().equals(order.getId())) {
          continue;
        }

        backupOrders.add(order.makeClone());

        for (ItemSummaryForKitchen item: order.getItemsInstances()) {
          for (ItemSummaryForKitchen selectedItem : selectedOrder.getItemsInstances()) {
            if (selectedItem.getItemInstanceId().equals(item.getItemInstanceId())) {
              updatePASFItemsStatus(item, ProductionAreaStationFiredItemStatus.done);
              break;
            }
          }
        }

        modifiedOrders.add(order);
      }
    }

    saveToBackup(backupOrders);
    updateItemsFired(modifiedOrders);
    separateOrders();

    ordersAdapter.clearAllSelections();

    doneButton.setEnabled(false);
    undoButton.setEnabled(true);
  }

  @Click(R.id.undo_button)
  void undoButtonClicked() {
    restoreFromBackup();
    undoButton.setEnabled(false);
  }

  @Click(R.id.backRipple)
  void back() {
    finish();
  }

  @UiThread
  void setupUserProfileImage() {
    ImageData imageData = ImageData_.getInstance_(getApplicationContext());
    if (RockServices.getDataService().getLoggedUser().getAvatar() != null) {
      imageData.from(RockServices.getDataService().getLoggedUser().getAvatar().getLoResolution().getUrl());
      profileImage.setImageDrawable(imageData.getImage(getApplicationContext(), profileImage));
    }
  }

  @Override
  protected void restoreFromBackup() {
    super.restoreFromBackup();
    separateOrders();
  }

  private void separateOrders() {
    inProgressOrders.clear();
    doneOrders.clear();

    for (ItemsFired order : orderData) {
      if (order.getType() == ItemsFiringType.together) {
        // For TOGETHER type orders we need to make the following with items, that have READY_FOR_DELIVERY status:
        // 1. Display them in opacity mode within in progress view cards
        // 2. Add them to the done list
        Set<ItemSummaryForKitchen> readyItems = new HashSet<>();

        for (ItemSummaryForKitchen item : order.getItemsInstances()) {
          if (item.getStatus() == ItemInstanceStatus.ready_for_delivery) {
            readyItems.add(item.makeClone());
          }
        }

        if (readyItems.size() < order.getItemsInstances().size()) {
          inProgressOrders.add(order);
        }

        if (readyItems.size() > 0) {
          doneOrders.add(order.withItemsInstances(readyItems));
        }
      } else {
        // For all other type orders we need to make the following with the same items:
        // 1. Exclude them from in progress view cards
        // 2. Add them to the done list
        Set<ItemSummaryForKitchen> readyItems = new HashSet<>();
        Set<ItemSummaryForKitchen> inProgressItems = new HashSet<>();

        for (ItemSummaryForKitchen item : order.getItemsInstances()) {
          if (item.getStatus() == ItemInstanceStatus.ready_for_delivery) {
            readyItems.add(item.makeClone());
          } else if (item.getStatus() == ItemInstanceStatus.in_progress) {
            inProgressItems.add(item.makeClone());
          }
        }

        if (inProgressItems.size() > 0) {
          inProgressOrders.add(order.withItemsInstances(inProgressItems));
        }

        if (readyItems.size() > 0) {
          doneOrders.add(order.withItemsInstances(readyItems));
        }
      }
    }

    ordersAdapter.setOrders(inProgressOrders);
    doneOrdersAdapter.setOrders(doneOrders);
  }

  @Override
  protected void onError(com.rockspoon.error.Error error) {
    super.onError(error);
    swipeRefreshLayout.setRefreshing(false);
  }
}
