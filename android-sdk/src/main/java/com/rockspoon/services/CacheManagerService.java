package com.rockspoon.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.FloorPlanUpdateEvent;
import com.rockspoon.helpers.events.GCMEvent;
import com.rockspoon.helpers.events.GCMRegistrationCompleteEvent;
import com.rockspoon.helpers.events.ItemsFiredUpdateEvent;
import com.rockspoon.helpers.events.MenuUpdateEvent;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.models.venue.ordering.item.ItemsFired;
import com.rockspoon.sdk.R;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lucas on 18/02/16.
 */
@EService
public class CacheManagerService extends Service {

  private static final String TAG = "CacheManagerService";

  private final Object floorPlanLock = new Object();
  private final Object menuLock = new Object();
  private final Object itemsFiredLock = new Object();

  private boolean updatingFloorPlan = false;
  private boolean updatingMenus = false;
  private boolean updatingItemsFired = false;

  private int resetTimeout = 3000;  //  3 seconds

  private final Object busCallback = new Object() {
    @Subscribe
    public void onReceiveGCMEvent(final GCMEvent event) {
      Log.d(TAG, "GCM Event Received!");
      parseGCMEvent(event);
    }

    @Subscribe
    public void onGCMRegistered(final GCMRegistrationCompleteEvent not_used) {
      Log.d(TAG, "GCM Registered!");
    }
  };

  public CacheManagerService() {
    Log.d(TAG, "CacheManagerService Started");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    try {
      RockServices.getDataService().getCacheBus().unregister(busCallback);
    } catch (IllegalArgumentException e) {
      // Do Nothing
    }
    RockServices.getDataService().getCacheBus().register(busCallback);
    Log.d(TAG, "CacheManagerService listening for GCM Events");

    /**
     * Notification Bar
     */
    final Notification notif = new Notification.Builder(this) //
        .setWhen(System.currentTimeMillis()) //
        .setSmallIcon(R.drawable.ic_services_icon) //
        .setContentTitle(getResources().getString(R.string.cache_service_notification_title)) //
        .setContentText(getResources().getString(R.string.cache_service_notification_text)) //
        .build();

    notif.flags |= Notification.FLAG_NO_CLEAR;
    startForeground(CacheManagerService.class.hashCode() % 32768, notif);

    return START_STICKY;
  }

  @Background
  protected void parseGCMEvent(final GCMEvent event) {
    Log.d(TAG, "From: " + event.getFrom());
    if (event.getFrom().startsWith("/topics/")) {
      // venueId : command : arg0: arg1 : ...
      final String[] fields = event.getFrom().replace("/topics/", "").split("_");
      final String[] arguments = Arrays.copyOfRange(fields, 2, fields.length);
      final String cmd = fields[1];

      switch (cmd) {
        case "update":
          parseUpdateEvent(arguments, event.getData());
          break;
        default:
          Log.e(TAG, "Unknown Command: " + cmd);
      }
    } else {
      // TODO: Parse device specific messages
    }
  }

  private void parseUpdateEvent(final String[] arguments, final Bundle data) {
    if (arguments.length > 0) {
      switch (arguments[0]) {
        case "menu":
          handleMenuListCacheUpdate();
          break;
        case "floorplan":
          handleFloorPlanCacheUpdate();
          break;
        case "itemsfired":
          handlerItemsFiredUpdate();
          break;
        default:
          Log.e(TAG, "Unknown Update Event: " + arguments[0]);
      }
    }
  }

  @Background
  protected void handlerItemsFiredUpdate() {
    boolean doWork = true;
    synchronized (itemsFiredLock) {
      if (!updatingItemsFired) {
        updatingItemsFired = true;
      } else {
        doWork = false;
      }
    }

    if (!doWork) {
      return;
    }

    try {
      updateItemsFired(RockServices.getOrderService().listItemsFired());
    } catch (com.rockspoon.error.Error e) {
      new Handler().postDelayed(() -> handlerItemsFiredUpdate(), resetTimeout);
    }

    synchronized (itemsFiredLock) {
      updatingItemsFired = false;
    }
  }


  /**
   * TODO: Add Dirty Flag to handles. Task (POS-337)
   */

  @Background
  protected void handleFloorPlanCacheUpdate() {
    boolean doWork = true;
    synchronized (floorPlanLock) {
      if (!updatingFloorPlan) {
        updatingFloorPlan = true;
      } else {
        doWork = false;
      }
    }

    if (!doWork) {
      return;
    }

    try {
      updateFloorPlan(RockServices.getVenueService().fetchFloorPlan());
    } catch (com.rockspoon.error.Error e) {
      new Handler().postDelayed(() -> handleFloorPlanCacheUpdate(), resetTimeout);
    }

    synchronized (floorPlanLock) {
      updatingFloorPlan = false;
    }
  }

  /**
   * TODO: Add Dirty Flag to handles. Task (POS-337)
   */

  @Background
  protected void handleMenuListCacheUpdate() {
    boolean doWork = true;
    synchronized (menuLock) {
      if (!updatingMenus) {
        updatingMenus = true;
      } else {
        doWork = false;
      }
    }

    if (!doWork) {
      return;
    }

    try {
      String venueId = RockServices.getDataService().getDeviceVenueId();
      List<Menu> menuList = RockServices.getVenueService().fetchMenuList(venueId, true, true, true);
      updateMenuList(menuList);
    } catch (com.rockspoon.error.Error e) {
      new Handler().postDelayed(() -> handleMenuListCacheUpdate(), resetTimeout);
    }

    synchronized (menuLock) {
      updatingMenus = false;
    }
  }

  @UiThread
  protected void updateMenuList(List<Menu> menuList) {
    RockServices.getDataService().updateMenuList(menuList);
    RockServices.getDataService().sendCacheBusMessage(new MenuUpdateEvent(menuList));
  }

  @UiThread
  protected void updateFloorPlan(VenueFloorPlan floorPlan) {
    RockServices.getDataService().setCurrentFloorPlan(floorPlan);
    RockServices.getDataService().sendCacheBusMessage(new FloorPlanUpdateEvent(floorPlan));
  }

  @UiThread
  protected void updateItemsFired(List<ItemsFired> itemsFireds) {
    RockServices.getDataService().sendCacheBusMessage(new ItemsFiredUpdateEvent(itemsFireds));
  }

  @Override
  public void onDestroy() {
    RockServices.getDataService().getCacheBus().unregister(busCallback);
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
