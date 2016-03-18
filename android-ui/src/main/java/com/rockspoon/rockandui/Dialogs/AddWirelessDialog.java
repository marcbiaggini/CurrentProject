package com.rockspoon.rockandui.Dialogs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.rockspoon.error.Error;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.InitialSetup.Actions;
import com.rockspoon.rockandui.Managers.RockManagerDeviceService;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.Tools;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lucas Teske on 27/08/15.
 */
public class AddWirelessDialog extends RSDialog {

  private RSDialog waitingDialog;

  public AddWirelessDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitUp);
  }

  public static AddWirelessDialog newInstance(final Bundle args) {
    final AddWirelessDialog dialog = new AddWirelessDialog();
    dialog.setArguments(args);
    dialog.setCancelable(false);
    return dialog;
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_add_wifi, container, false);

    final Button cancelBtn = (Button) v.findViewById(R.id.dialog_add_wifi_cancelbtn);
    final Button closeBtn = (Button) v.findViewById(R.id.dialog_add_wifi_closebtn);
    final Button addBtn = (Button) v.findViewById(R.id.dialog_add_wifi_addbtn);

    final EditText ssid = (EditText) v.findViewById(R.id.dialog_add_wifi_networkname);
    final Spinner securityType = (Spinner) v.findViewById(R.id.dialog_add_wifi_securitytype);
    final EditText password = (EditText) v.findViewById(R.id.dialog_add_wifi_password);
    final CheckBox showPassword = (CheckBox) v.findViewById(R.id.dialog_add_wifi_showpassword);

    final View.OnClickListener closeClick = (view) -> {
        AddWirelessDialog.this.dismiss();
    };

    final String[] securityTypes = inflater.getContext().getResources().getStringArray(R.array.security_type);
    final ArrayAdapter<String> securityTypeAdapter = new ArrayAdapter<>(inflater.getContext(), android.R.layout.simple_list_item_1, securityTypes);
    securityType.setAdapter(securityTypeAdapter);

    closeBtn.setOnClickListener(closeClick);
    cancelBtn.setOnClickListener(closeClick);

    showPassword.setOnClickListener((view) -> {
      password.setInputType(showPassword.isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD);
    });

    addBtn.setOnClickListener((view) -> {
      final String pass = password.getText().toString();
      final String security = securityType.getSelectedItem().toString();
      if (Tools.checkWirelessPasswordLength(pass, security)) {
        final Map<String, String> settings = new HashMap<>();
        settings.put("password", pass);
        settings.put("keytype", security);

        final Network connectTarget = new Network(null, ssid.getText().toString(), security, true,  settings);
        final RockManagerDeviceService rockManager = new RockManagerDeviceService(getActivity());

        waitingDialog = new GenericWaitingDialog().setMessage(getString(R.string.message_connecting_to_wifi));
        waitingDialog.show(getFragmentManager(), "watingToConnectDialog");

        rockManager.connectToWifi(connectTarget).then(onWifiAdded).fail(onWifiAddError);

      } else
        GenericMessageDialog.showDialog(getFragmentManager(), getString(R.string.message_invalid_password), getString(R.string.message_invalid_password_length_message));
    });

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
