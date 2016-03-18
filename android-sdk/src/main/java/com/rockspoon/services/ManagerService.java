package com.rockspoon.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.Tools;
import com.rockspoon.helpers.events.GCMEvent;
import com.rockspoon.helpers.events.PrintEvent;
import com.rockspoon.helpers.receivers.ConnectWifiBroadcastReceiver_;
import com.rockspoon.helpers.receivers.NetworkCriteriaBroadcastReceiver_;
import com.rockspoon.helpers.receivers.SearchWifiNetworksBroadcastReceiver_;
import com.rockspoon.helpers.receivers.WifiScannerBroadcastReceiver_;
import com.rockspoon.printer.PrinterManager;
import com.rockspoon.printer.Session;
import com.rockspoon.sdk.R;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import timber.log.Timber;

/**
 * Created by lucas on 4/10/15.
 * TODO: Migrate Wireless Connect / List to their activities / fragments. (POS-317)
 */
@EService
public class ManagerService extends Service {

  @Bean
  PrinterManager printerManager;

  private boolean isScanning = false;

  private Object printerCallback = new Object() {
    @Subscribe
    public void onReceiveGCMEvent(GCMEvent event) {
      if (!event.getFrom().contains("/topics/")) {
        final Bundle gcmBundle = event.getData();
        if (gcmBundle.containsKey("printJobId")) {
          // TODO
        }
      }
    }

    @Subscribe
    public void onReceivePrintEvent(PrintEvent printEvent) {
      printerManager.printJob(printEvent.getJob());
    }
  };

  public boolean getIsScanning() {
    return isScanning;
  }

  public void setIsScanning(boolean isScanning) {
    this.isScanning = isScanning;
  }

  @AfterInject
  protected void afterInject() {
    RockServices.setManagerService(this);

    /**
     * Notification Bar
     */
    final Notification notif = new Notification.Builder(this) //
        .setWhen(System.currentTimeMillis()) //
        .setSmallIcon(R.drawable.ic_services_icon) //
        .setContentTitle(getResources().getString(R.string.manager_service_notification_title)) //
        .setContentText(getResources().getString(R.string.manager_service_notification_text)) //
        .build();

    notif.flags |= Notification.FLAG_NO_CLEAR;
    startForeground(ManagerService.class.hashCode() % 32768, notif);

    /**
     * Network Criteria Check
     */
    registerReceiver(new NetworkCriteriaBroadcastReceiver_(), new IntentFilter(Actions.CHECK_NETWORK_CRITERIA_REQUEST));

    /**
     * Printer Service
     */
    RockServices.getDataService().getCacheBus().register(printerCallback);

  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not supported");
  }

  @Override
  public void onDestroy() {
    stopForeground(true);
    printerManager.unRegisterReceivers();
    RockServices.setManagerService(null);
    RockServices.getDataService().getCacheBus().unregister(printerCallback);
    Timber.d("BackgroundService::onDestroy - Service Destroyed");
    super.onDestroy();
  }
}
