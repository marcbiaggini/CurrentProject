package com.rockspoon.kitchentablet.UI.ChefScreenOrders;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Button;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.kitchentablet.Adapters.ChefOrdersAdapter;
import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItemStatus;
import com.rockspoon.models.venue.ordering.item.ItemFiredStatus;
import com.rockspoon.models.venue.ordering.item.ItemInstanceStatus;
import com.rockspoon.models.venue.ordering.item.ItemSummaryForKitchen;
import com.rockspoon.models.venue.ordering.item.ItemsFired;
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
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/9/16.
 */
@EActivity(R.layout.kitchen_chef_inbox_activity)
public class ChefInboxActivity extends ChefBaseActivity {

  @ViewById(R.id.orders_grid)
  RecyclerView ordersGrid;

  @ViewById(R.id.undo_button)
  Button undoButton;

  @ViewById(R.id.start_button)
  Button startButton;

  @ViewById(R.id.profileImage)
  RoundImageView profileImage;

  @ViewById(R.id.swipe_container)
  SwipeRefreshLayout swipeRefreshLayout;

  private ChefOrdersAdapter ordersAdapter;

  @Override
  void init() {
    super.init();

    ordersAdapter = new ChefOrdersAdapter(this, orderData, selectedOrders -> startButton.setEnabled(selectedOrders.size() > 0));
    ordersAdapter.setViewMode(ChefOrdersAdapter.OrdersViewMode.INBOX);
    ordersGrid.setAdapter(ordersAdapter);
    ordersGrid.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));

    swipeRefreshLayout.setOnRefreshListener(this::getItemsFired);

    setupUserProfileImage();
  }

  @Override
  void setupOrders(List<ItemsFired> itemsFired) {
    super.setupOrders(itemsFired);
    ordersAdapter.setOrders(orderData);
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override
  void updateOrders(List<ItemsFired> newOrders) {
    super.updateOrders(newOrders);
    ordersAdapter.setOrders(orderData);
  }

  @Override
  protected List<ItemsFired> filterOrders(List<ItemsFired> orders) {
    List<ItemsFired> parentFilteredOrders = super.filterOrders(orders);
    List<ItemsFired> filteredOrders = new ArrayList<>();

    for (ItemsFired order : parentFilteredOrders) {
      if (order.getStatus() != ItemFiredStatus.pending && order.getStatus() != ItemFiredStatus.in_progress) {
        continue;
      }

      // We need to display only our orders that have items with FIRED and IN_PROGRESS statuses
      Set<ItemSummaryForKitchen> items = new HashSet<>();
      for (ItemSummaryForKitchen itemSummary : order.getItemsInstances()) {
        if (isItemAssignedToOurStation(itemSummary) &&
            (itemSummary.getStatus() == ItemInstanceStatus.fired || itemSummary.getStatus() == ItemInstanceStatus.in_progress)) {
          items.add(itemSummary);
        }
      }

      if (items.size() > 0) {
        filteredOrders.add(order.withItemsInstances(items));
      }
    }

    return filteredOrders;
  }

  @Click(R.id.start_button)
  void startButtonClicked() {
    List<ItemsFired> selectedOrders = ordersAdapter.getSelectedOrders();
    List<ItemsFired> backupOrders = new ArrayList<>();
    List<ItemsFired> modifiedOrders = new ArrayList<>();

    // mark all selected items as IN_PROGRESS
    for (ItemsFired selectedOrder: selectedOrders) {
      for (ItemsFired order: orderData) {
        if (!selectedOrder.getId().equals(order.getId())) {
          continue;
        }

        backupOrders.add(order.makeClone());

        for (ItemSummaryForKitchen item: order.getItemsInstances()) {
          for (ItemSummaryForKitchen selectedItem : selectedOrder.getItemsInstances()) {
            if (selectedItem.getItemInstanceId().equals(item.getItemInstanceId())) {
              updatePASFItemsStatus(item, ProductionAreaStationFiredItemStatus.in_progress);
              break;
            }
          }
        }

        modifiedOrders.add(order);
      }
    }

    ordersAdapter.setOrders(orderData);
    ordersAdapter.clearAllSelections();

    saveToBackup(backupOrders);
    updateItemsFired(modifiedOrders);

    startButton.setEnabled(false);
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
  public void setupUserProfileImage() {
    ImageData imageData = ImageData_.getInstance_(getApplicationContext());
    if (RockServices.getDataService().getLoggedUser().getAvatar() != null) {
      imageData.from(RockServices.getDataService().getLoggedUser().getAvatar().getLoResolution().getUrl());
      profileImage.setImageDrawable(imageData.getImage(getApplicationContext(), profileImage));
    }
  }

  @Override
  protected void restoreFromBackup() {
    super.restoreFromBackup();
    ordersAdapter.setOrders(orderData);
  }

  @Override
  protected void onError(com.rockspoon.error.Error error) {
    super.onError(error);
    swipeRefreshLayout.setRefreshing(false);
  }
}
