package com.rockspoon.rockandui.Dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.rockspoon.error.Error;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.InitialSetup.Actions;
import com.rockspoon.rockandui.Managers.RockManagerDeviceService;
import com.rockspoon.rockandui.Tools;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import com.rockspoon.rockandui.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas Teske on 27/08/15.
 */
public class ConnectWirelessDialog extends RSDialog {

  private RSDialog waitingDialog;
  private String ssid;
  private String securityType;

  public ConnectWirelessDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitUp);
  }

  public static ConnectWirelessDialog newInstance(final Bundle args) {
    final ConnectWirelessDialog dialog = new ConnectWirelessDialog();
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void setArguments(final Bundle args) {
    ssid = args.getString("ssid");
    securityType = args.getString("securityType");
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_connect_wifi, container, false);

    final Button cancelBtn = (Button) v.findViewById(R.id.dialog_connect_wifi_cancelbtn);
    final Button closeBtn = (Button) v.findViewById(R.id.dialog_connect_wifi_closebtn);
    final Button connectBtn = (Button) v.findViewById(R.id.dialog_connect_wifi_connectbtn);

    final TextView ssidView = (TextView) v.findViewById(R.id.dialog_connect_wifi_ssid);
    final TextView securityTypeView = (TextView) v.findViewById(R.id.dialog_connect_wifi_securitytype);
    final EditText password = (EditText) v.findViewById(R.id.dialog_connect_wifi_password);
    final CheckBox showPassword = (CheckBox) v.findViewById(R.id.dialog_connect_wifi_showpassword);

    final View.OnClickListener closeClick = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ConnectWirelessDialog.this.dismiss();
      }
    };

    showPassword.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        password.setInputType(showPassword.isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
      }
    });

    closeBtn.setOnClickListener(closeClick);
    cancelBtn.setOnClickListener(closeClick);

    connectBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final String pass = password.getText().toString();
        if (Tools.checkWirelessPasswordLength(pass, securityType)) {
          final Map<String, String> settings = new HashMap<String, String>();
            settings.put("password", pass);
            settings.put("keytype", securityType);

          final Network connectTarget = new Network(null, ssid, securityType, true, settings);
          final RockManagerDeviceService rockManager = new RockManagerDeviceService(getActivity());

          waitingDialog = new GenericWaitingDialog().setMessage(getString(R.string.message_connecting_to_wifi));
          waitingDialog.show(getFragmentManager(), "watingToConnectDialog");

          rockManager.connectToWifi(connectTarget).then(onWifiAdded).fail(onWifiAddError);

        } else
          GenericMessageDialog.showDialog(getFragmentManager(), getString(R.string.message_invalid_password), getString(R.string.message_invalid_password_length_message));

      }
    });

    if (ssid == null || securityType == null) {
      this.dismiss();
      GenericMessageDialog.showDialog(getFragmentManager(), getString(R.string.error), getString(R.string.message_internal_error));
    } else {
      ssidView.setText(ssid);
      securityTypeView.setText(securityType);
    }

    return v;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    final Intent intent = new Intent(Actions.WifiCallbackReceiver);
    intent.putExtra("finish", false);
    getActivity().sendBroadcast(intent);

    super.onDismiss(dialog);
  }

  private DoneCallback<Void> onWifiAdded = new DoneCallback<Void>() {
    @Override
    public void onDone(Void result) {
      waitingDialog.dismiss();
      final Intent intent = new Intent(Actions.WifiCallbackReceiver);
      intent.putExtra("finish", true);
      getActivity().sendBroadcast(intent);
    }
  };

  private FailCallback<com.rockspoon.error.Error> onWifiAddError = new FailCallback<com.rockspoon.error.Error>() {
    @Override
    public void onFail(Error result) {
      waitingDialog.dismiss();
      GenericMessageDialog.showDialog(getFragmentManager(), getString(R.string.error), getString(R.string.message_error_connecting_to_wifi));
    }
  };
}
