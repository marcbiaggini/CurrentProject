package com.rockspoon.helpers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.Tools;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.services.Actions;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;
import org.androidannotations.annotations.SystemService;

import timber.log.Timber;

/**
 * Created by lucas on 10/03/16.
 */
@EReceiver
public class ConnectWifiBroadcastReceiver extends BroadcastReceiver {

  @SystemService
  WifiManager wifi;

  @ReceiverAction(actions = {Actions.CONNECT_TO_WIFI_REQUEST})
  void connectToWifi(Context ctx, Intent intent) {
    Timber.d("ManagerService::CONNECT_WIFI - Received a connection request");
    final Network network = (Network) intent.getSerializableExtra("network");
    if (network == null) {
      final Intent it = new Intent(Actions.CONNECT_TO_WIFI_RESPONSE);
      final com.rockspoon.error.Error err = new Error(ErrorCode.UnknownError, "Empty Network Object");
      it.putExtra("error", err);
      ctx.sendBroadcast(it);
      return;
    }

    final String ssid = network.getSsid();
    final String password = network.getSettings().get("password");
    final String keytype = network.getSettings().get("keytype").toUpperCase();
    if (ssid.length() != 0 && keytype.length() != 0) {

      final WifiConfiguration wifiConfig = new WifiConfiguration();
      wifiConfig.SSID = "\"" + ssid + "\"";
      wifiConfig.priority = 0;
      wifiConfig.status = WifiConfiguration.Status.ENABLED;

      switch (keytype) {
        case "NONE":
          // No security
          Timber.d("ManagerService::CONNECT_WIFI - Open Network ");
          wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
          wifiConfig.allowedAuthAlgorithms.clear();
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
          break;
        case "WPA":
        case "WPA2":
          Timber.d("ManagerService::CONNECT_WIFI - WPA Network");
          //WPA/WPA2 Security
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
          wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
          wifiConfig.preSharedKey = "\"".concat(password).concat("\"");
          break;
        case "WEP":
          Timber.d("ManagerService::CONNECT_WIFI - WEP Network");
          // WEP Security
          wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
          wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
          wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
          wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
          wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
          wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

          if (Tools.getHexKey(password)) wifiConfig.wepKeys[0] = password;
          else wifiConfig.wepKeys[0] = "\"".concat(password).concat("\"");
          wifiConfig.wepTxKeyIndex = 0;
          break;
        default:
          Timber.d("ManagerService::CONNECT_WIFI - Unknown type: " + keytype);
          // Send the broadcast
          final Intent it = new Intent(Actions.CONNECT_TO_WIFI_RESPONSE);
          final Error err = new Error(ErrorCode.UnknownError, "Unknown type: " + keytype);
          it.putExtra("error", err);
          ctx.sendBroadcast(it);
          return;
      }
      wifi.setWifiEnabled(true);
      final int netId = wifi.addNetwork(wifiConfig);
      wifi.enableNetwork(netId, true);
      wifi.setWifiEnabled(true);
      // Send the broadcast
      final Intent it = new Intent(Actions.WIFI_STATUS_NOTIFICATION);
      it.putExtra("ssid", ssid);
      ctx.sendBroadcast(it);
      /*cacheSSID = "\"" + ssid + "\"";
      cacheSSIDStamp = Tools.getTimeStamp();
      Timber.d("ManagerService::CONNECT_WIFI - Timestamp: " + cacheSSIDStamp);
      checkSSIDHandler.postDelayed(checkSSID, 1000);*/
    } else {
      // Send the broadcast
      final Intent it = new Intent(Actions.CONNECT_TO_WIFI_RESPONSE);
      Timber.wtf("ManagerService::CONNECT_WIFI - Empty SSID or KeyType. This should NOT happen!");
      final Error err = new Error(ErrorCode.UnknownError, "Empty SSID or KeyType");
      it.putExtra("error", err);
      ctx.sendBroadcast(it);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    // empty, will be overridden in generated subclass
  }
}
