package com.rockspoon.helpers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.services.Actions;
import com.rockspoon.services.ManagerService;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.annotations.SystemService;

import timber.log.Timber;

/**
 * Created by lucas on 10/03/16.
 */
@EReceiver
public class SearchWifiNetworksBroadcastReceiver extends BroadcastReceiver {

  @SystemService
  WifiManager wifi;

  @ReceiverAction(actions = {Actions.SEARCH_WIFI_NETWORKS_REQUEST})
  protected void searchWifiNetworks(Context ctx, Intent intent) {
    final ManagerService managerService = RockServices.getManagerService();
    if (managerService != null) {
      if (!managerService.getIsScanning()) {
        Timber.d("ManagerService::BROADCAST_SEARCH_WIFI - Received broadcast to start scanning for wifi networks");
        managerService.setIsScanning(true);
        if (!wifi.isWifiEnabled()) {
          Timber.d("BackgroundService::BROADCAST_SEARCH_WIFI - Wifi is disabled. Enabling");
          wifi.setWifiEnabled(true);
        }
        wifi.startScan();
      }
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {

  }
}
