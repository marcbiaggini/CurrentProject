package com.rockspoon.rockandui.Managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import com.rockspoon.models.venue.network.Network;
import com.rockspoon.models.venue.network.WifiNetworks;
import com.rockspoon.services.Actions;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Lucas Teske on 31/08/15.
 */
public class RockManagerDeviceService {
  private final Context ctx;
  private String uid = null;
  private AtomicReference<Deferred<String, Error, Void>> waitConnectionDefer = new AtomicReference<>();

  public RockManagerDeviceService(final Context ctx) {
    this.ctx = ctx;

    this.uid = Settings.Secure.getString(this.ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

    try {
      if (tm != null)
        this.uid += tm.getDeviceId();
    } catch (final Exception e) { /* Ignore this, because this is not harmful to us. */ }
  }

  //@Override
  public Promise<Void, Error, Void> checkNetworkCriteria() {
    final Deferred<Void, Error, Void> defer = new DeferredObject<>();

    final BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, final Intent intent) {
        RockManagerDeviceService.this.ctx.unregisterReceiver(this);
        final Error error = (Error) intent.getSerializableExtra("error");
        if (error != null)
          defer.reject(error);
        else
          defer.resolve(null);
      }
    };

    this.ctx.registerReceiver(receiver, new IntentFilter(Actions.CHECK_NETWORK_CRITERIA_RESPONSE));
    this.ctx.sendBroadcast(new Intent(Actions.CHECK_NETWORK_CRITERIA_REQUEST));

    return defer.promise();
  }

  //@Override
  public Promise<WifiNetworks, Error, Void> fetchWifiNetworks() {
    final Deferred<WifiNetworks, Error, Void> defer = new DeferredObject<>();

    final BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(final Context context, final Intent intent) {
        RockManagerDeviceService.this.ctx.unregisterReceiver(this);
        final Error error = (Error) intent.getSerializableExtra("error");
        if (error != null)
          defer.reject(error);
        else {
          @SuppressWarnings("unchecked")
          final List<Network> networks = (List<Network>) intent.getSerializableExtra("networks");
          final Network selectedNetwork = (Network) intent.getSerializableExtra("selectedNetwork");
          defer.resolve(new WifiNetworks(selectedNetwork, networks));
        }
      }
    };

    this.ctx.registerReceiver(receiver, new IntentFilter(Actions.SEARCH_WIFI_NETWORKS_RESPONSE));
    this.ctx.sendBroadcast(new Intent(Actions.SEARCH_WIFI_NETWORKS_REQUEST));
    return defer.promise();
  }

  //@Override
  public Promise<Void, Error, Void> connectToWifi(final Network network) {
    final Deferred<Void, Error, Void> defer = new DeferredObject<>();
    final Deferred<String, Error, Void> waitDefer = new DeferredObject<>();

    if (waitConnectionDefer.compareAndSet(null, waitDefer)) {
      final BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
          RockManagerDeviceService.this.ctx.unregisterReceiver(this);
          final Error error = (Error) intent.getSerializableExtra("error");
          if (error != null)
            defer.reject(error);
          else
            defer.resolve(null);
        }
      };

      final BroadcastReceiver waitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
          RockManagerDeviceService.this.ctx.unregisterReceiver(this);
          final Error error = (Error) intent.getSerializableExtra("error");
          if (error != null)
            waitDefer.reject(error);
          else
            waitDefer.resolve(intent.getStringExtra("ssid"));
        }
      };

      final Intent intent = new Intent(Actions.CONNECT_TO_WIFI_REQUEST);
      intent.putExtra("network", network);

      this.ctx.registerReceiver(statusReceiver, new IntentFilter(Actions.WIFI_STATUS_NOTIFICATION));
      this.ctx.registerReceiver(waitReceiver, new IntentFilter(Actions.CONNECT_TO_WIFI_RESPONSE));

      this.ctx.sendBroadcast(intent);
    } else
      defer.reject(new Error(ErrorCode.ConnectionError, "Connection already in progress."));

    return defer.promise();
  }

  //@Override
  public Promise<String, Error, Void> waitWifiConnectionCompletion() {
    final Deferred<String, Error, Void> defer = waitConnectionDefer.getAndSet(null);
    if (defer == null) {
      return (new DeferredObject<String, Error, Void>()).reject(new Error(ErrorCode.ConnectionError,
          "waitWifiConnectionCompletion() called without connectToWifi()"));
    }

    return defer.promise();
  }

  //@Override
  public String getUID() {
    return uid;
  }

}
