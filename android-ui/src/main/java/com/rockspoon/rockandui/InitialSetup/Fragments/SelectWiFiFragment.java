package com.rockspoon.rockandui.InitialSetup.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.receivers.ConnectWifiBroadcastReceiver_;
import com.rockspoon.helpers.receivers.SearchWifiNetworksBroadcastReceiver_;
import com.rockspoon.helpers.receivers.WifiScannerBroadcastReceiver_;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.models.venue.network.WifiNetworks;
import com.rockspoon.rockandui.Adapters.WifiNetworkListAdapter;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.Dialogs.AddWirelessDialog;
import com.rockspoon.rockandui.Dialogs.ConnectWirelessDialog;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.Dialogs.GenericWaitingDialog;
import com.rockspoon.rockandui.InitialSetup.Actions;
import com.rockspoon.rockandui.InitialSetup.InitialSetupActivity;
import com.rockspoon.rockandui.Managers.RockManagerDeviceService;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.Tools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

/**
 * Created by Lucas Teske on 31/08/15.
 */
@EFragment
public class SelectWiFiFragment extends RSBaseFragment {

  @ViewById(resName = "initialsetup_wifi_list")
  ListView wifiList;

  @ViewById(resName = "initialsetup_addwifibtn")
  Button addNetworkBtn;

  @SystemService
  WifiManager wifi;

  private RockManagerDeviceService rockManager;

  private RSDialog tmpdialog;
  private Handler refreshHandler;
  private DoneCallback<WifiNetworks> onWifiNetworksReceived = new DoneCallback<WifiNetworks>() {
    @Override
    public void onDone(WifiNetworks result) {
      final WifiNetworkListAdapter adapter = new WifiNetworkListAdapter(getActivity(), result.getNetworks());
      wifiList.setAdapter(adapter);

      if (tmpdialog != null) {
        tmpdialog.dismiss();
        tmpdialog = null;
      }

      // Re-schedule to update wifi in 5 seconds
      refreshHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          rockManager.fetchWifiNetworks().then(onWifiNetworksReceived);
        }
      }, 5000);
    }
  };
  private FailCallback<com.rockspoon.error.Error> onWifiNetworksFetchError = (result) -> {
    GenericMessageDialog.showDialog(getFragmentManager(), getString(R.string.error), getString(R.string.message_error_fetching_wifi_networks));
  };

  @Override
  public String getFragmentTitle() {
    return "";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.initialsetup_select_wifi;
  }

  @Click(resName = "initialsetup_addwifibtn")
  void addNetworkBtnClick() {
    tmpdialog = AddWirelessDialog.newInstance(null);
    tmpdialog.show(getFragmentManager(), "AddWirelessDialog");
    registerWiFiCallBack();
  }

  @ItemClick(resName = "initialsetup_wifi_list")
  void wifiListItemClick(Network network) {
    final Bundle wifiData = new Bundle();
    wifiData.putString("ssid", network.getSsid());
    wifiData.putString("securityType", network.getSettings().get("keytype"));
    tmpdialog = ConnectWirelessDialog.newInstance(wifiData);
    tmpdialog.show(getFragmentManager(), "ConnectWifiDialog");
    registerWiFiCallBack();
  }

  @AfterViews
  void initViews() {
    refreshHandler = new Handler();

    tmpdialog = new GenericWaitingDialog().setMessage(getString(R.string.message_fetching_wifi_networks)).setTitle(getString(R.string.message_please_wait));
    tmpdialog.show(getFragmentManager(), "fetchingWiFiNetworks");

    if (Tools.verifyLocationPermissions(getActivity())) {
      scanWirelessNetworks();
    }

  }

  private void scanWirelessNetworks() {
    wifi.setWifiEnabled(true);
    getActivity().registerReceiver(new SearchWifiNetworksBroadcastReceiver_(), new IntentFilter(com.rockspoon.services.Actions.SEARCH_WIFI_NETWORKS_REQUEST));
    getActivity().registerReceiver(new WifiScannerBroadcastReceiver_(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    getActivity().registerReceiver(new ConnectWifiBroadcastReceiver_(), new IntentFilter(com.rockspoon.services.Actions.CONNECT_TO_WIFI_REQUEST));
    registerWiFiCallBack();

    rockManager = new RockManagerDeviceService(getActivity());
    rockManager.fetchWifiNetworks().then(onWifiNetworksReceived).fail(onWifiNetworksFetchError);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case Tools.REQUEST_COARSE_LOCATION: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          scanWirelessNetworks();
        } else {
          Toast.makeText(getActivity(), getString(R.string.message_wifi_permission_denied), Toast.LENGTH_LONG);
          Tools.verifyLocationPermissions(getActivity());
        }
      }
    }
  }

  private void registerWiFiCallBack() {
    getActivity().registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        getActivity().unregisterReceiver(this);
        tmpdialog.dismiss();

        if (intent.getBooleanExtra("finish", false)) {

          if (!RockServices.isRegistered()) {

            final ManagerLoginFragment_ frag = new ManagerLoginFragment_();
            replaceFragment(frag);

          } else {
            /**
             * I currently rebooting the system after configuring the wifi because of two reasons:
             * 1. We may have an update after that and I think its better to update before using the software
             * 2. This is a cross-application Activity and I didn't found any way to go back to the main application
             * regardless of the application that ran this activity.
             */
            final GenericWaitingDialog finishThemAll = GenericWaitingDialog.newInstance(null).setMessage(getString(R.string.message_device_configured_rebooting)).setTitle(getString(R.string.message_rebooting));
            finishThemAll.show(getFragmentManager(), "rebootingDialog");
            refreshHandler.postDelayed(() -> ((InitialSetupActivity) getActivity()).closeInitialSetup(), 3000);

          }
        }
      }
    }, new IntentFilter(Actions.WifiCallbackReceiver));
  }

}
