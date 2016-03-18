package com.rockspoon.helpers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.Tools;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.services.Actions;
import com.rockspoon.services.ManagerService;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by lucas on 10/03/16.
 */
@EReceiver
public class WifiScannerBroadcastReceiver extends BroadcastReceiver {

  @SystemService
  WifiManager wifi;

  @ReceiverAction(actions = {WifiManager.SCAN_RESULTS_AVAILABLE_ACTION})
  public void onReceiveScanResults(Context ctx, Intent intent) {
    final List<ScanResult> data = wifi.getScanResults();
    final List<ScanResult> toRemove = new ArrayList<>();
    // Search for lower signal duplicates
    // TODO: Optimize it
    for (final ScanResult net1 : data) {
      for (final ScanResult net2 : data) {
        if (net2.SSID.equals(net1.SSID) && net1 != net2) {
          toRemove.add((net2.level > net1.level) ? net1 : net2);
          break;
        }
      }
    }

    // Remove the lower signal duplicates
    for (final ScanResult net : toRemove) {
      if (data.contains(net)) {
        data.remove(net);
      }
    }

    /**
     * Pack the broadcast
     */
    Timber.d("WifiScannerBroadcastReceiver::WifiListReceiver - Sending Broadcast result");
    // Send the broadcast
    final Intent it = new Intent(Actions.SEARCH_WIFI_NETWORKS_RESPONSE);
    final String connectSSID = Tools.getConnectedWiFiNetwork(ctx);

    Timber.d("WifiScannerBroadcastReceiver::WifiListReceiver - Sending " + Actions.SEARCH_WIFI_NETWORKS_RESPONSE);
    final ArrayList<Network> networks = new ArrayList<>(data.size());
    for (final ScanResult res : data) {
      final Map<String, String> settings = new HashMap<>();
      settings.put("keytype", (res.capabilities.contains("PSK") | res.capabilities.contains("AES")) ? "WPA" : (res.capabilities.contains("WEP") ? "WEP" : "NONE"));
      settings.put("signal", Integer.toString(Tools.getSignalPercent(res.level)));
      settings.put("signal4", Integer.toString(Tools.getSignalStrengthBar4(res.level)));
      final Network net = new Network(null, res.SSID, null, false, settings);
      if (res.SSID.equals(connectSSID)) {
        it.putExtra("selectedNetwork", net);
      }
      networks.add(net);
    }
    it.putExtra("networks", networks);

    ctx.sendBroadcast(it);

    Timber.d("WifiScannerBroadcastReceiver::WifiListReceiver - Received " + data.size() + " Wireless Networks");
    for (ScanResult net : data) {
      Timber.d("WifiScannerBroadcastReceiver::WifiListReceiver - \t" + net.SSID);
    }
    final ManagerService managerService = RockServices.getManagerService();
    if (managerService != null) {
      managerService.setIsScanning(false);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {

  }
}
