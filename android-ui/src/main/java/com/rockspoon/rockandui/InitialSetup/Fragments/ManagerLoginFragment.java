package com.rockspoon.rockandui.InitialSetup.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.device.DeviceStatus;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Dialogs.AbstractTitleMessageDialog;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.Dialogs.GenericSelectDialog;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockandui.InitialSetup.InitialSetupActivity;
import com.rockspoon.rockandui.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by Lucas Teske on 10/11/15.
 */
@EFragment
public class ManagerLoginFragment extends RSBaseFragment {

  @ViewById(resName = "initialsetup_login_loginbtn")
  Button loginButton;

  @ViewById(resName = "initialsetup_login_email")
  EditText loginEmail;

  @ViewById(resName = "initialsetup_login_password")
  EditText loginPassword;

  private Venue selectedVenue;
  private Device selectedDevice;

  @Override
  public String getFragmentTitle() {
    return "";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.initialsetup_login;
  }

  @Click(resName = "initialsetup_login_loginbtn")
  void onLoginButtonClick() {
    showWaitingDialog(null, getString(R.string.message_initialsetup_trylogin), "tryLoginDialog");
    doLogin(loginEmail.getText().toString(), loginPassword.getText().toString());
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateDialogMessage(String message) {
    ((AbstractTitleMessageDialog) dialog).setMessage(message);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void askForVenue(final List<Venue> venues) {
    dialog.dismiss();
    final String[] venueNames = new String[venues.size()];
    int i = 0;
    for (Venue venue : venues) {
      venueNames[i] = venue.getBrand().getName() + "\n" + venue.getAddress().getAddress1();
      i++;
    }
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_list_item_1, android.R.id.text1, venueNames);

    final Bundle dialogArgs = new Bundle();
    dialogArgs.putString(GenericSelectDialog.PARAM_MESSAGE, getResources().getString(R.string.message_select_device_venue));
    dialog = GenericSelectDialog.newInstance(dialogArgs);

    ((GenericSelectDialog) dialog)
        .setAdapter(adapter)
        .setOnItemClickListener((parent, view, position, id) -> {
          selectedVenue = venues.get(position);
          getDevices(selectedVenue.getId());
        });

    dialog.show(getFragmentManager(), "selectVenueDialog");
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void askForDevice(final List<Device> devices) {
    dialog.dismiss();
    final String[] deviceNames = new String[devices.size()];
    int i = 0;
    for (Device device : devices) {
      deviceNames[i] = device.getName() + "(" + device.getStatus().toString() + ")";
      i++;
    }
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
        android.R.layout.simple_list_item_1, android.R.id.text1, deviceNames);

    final Bundle dialogArgs = new Bundle();
    dialogArgs.putString(GenericSelectDialog.PARAM_MESSAGE, getResources().getString(R.string.message_select_device_association));
    dialog = GenericSelectDialog.newInstance(dialogArgs);

    ((GenericSelectDialog) dialog)
        .setAdapter(adapter)
        .setOnItemClickListener((parent, view, position, id) -> {
          dialog.dismiss();
          confirmSelectDevice(devices, devices.get(position));
        });

    dialog.show(getFragmentManager(), "selectVenueDialog");
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void confirmSelectDevice(final List<Device> devices, final Device selectedDevice) {
    final Bundle dialogArgs = new Bundle();
    dialogArgs.putBoolean(GenericMessageDialog.PARAM_DUAL_BUTTON_MODE, true);
    String formatString = (selectedDevice.getStatus() == DeviceStatus.unregistered) ? getResources().getString(R.string.message_select_device_confirm) : getResources().getString(R.string.message_select_device_confirm_registered);
    dialogArgs.putString(GenericMessageDialog.PARAM_MESSAGE, String.format(formatString, selectedDevice.getName()));

    dialog = GenericMessageDialog.newInstance(dialogArgs);
    ((GenericMessageDialog) dialog).setOnOKButtonClick((v) -> {
      dialog.dismiss();
      startDeviceLogin(selectedDevice);
    });
    ((GenericMessageDialog) dialog).setOnCancelButtonClick((v) -> {
      askForDevice(devices);
    });
    dialog.show(getFragmentManager(), "confirmDeviceAssign");
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void startDeviceLogin(final Device device) {
    selectedDevice = device;
    showWaitingDialog(null, getString(R.string.message_connecting_as_device), "connectingAsDevice");
    connectAsDevice(device);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void finishSetup() {
    RockServices.getDataService().setDeviceVenueId(selectedVenue.getId());
    RockServices.getDataService().setDeviceId(selectedDevice.getId());

    dialog.dismiss();
    dialog = GenericMessageDialog.newInstance(null)
        .setMessage(getString(R.string.message_device_configuration_finished))
        .setTitle(getString(R.string.title_configuration_finished));
    ((GenericMessageDialog) dialog).setOnOKButtonClick((v) -> {
      dialog.dismiss();
      ((InitialSetupActivity) getActivity()).closeInitialSetup();
    });
    dialog.show(getFragmentManager(), "confirmDeviceAssign");
  }

  @Background
  void connectAsDevice(final Device device) {
    try {
      Device d = RockServices.getSessionService().deviceLogin(device.getId(), selectedVenue.getId());
      updateDialogMessage(getString(R.string.message_logged_in_as_device, d.getName()));
      RockServices.getUserService().disconnect();
      finishSetup();
    } catch (Error e) {
      onError(e, "connectAsDeviceError");
    }
  }

  @Background
  void getDevices(String venueId) {
    try {
      List<Device> deviceList = RockServices.getDeviceService().list(selectedVenue.getId());
      Log.d("ManagerLoginFragment", "Received a list of " + deviceList.size() + " devices.");
      askForDevice(deviceList);
    } catch (Error e) {
      onError(e, "doLoginError");
    }
  }

  @Background
  void doLogin(final String userEmail, final String userPassword) {
    Log.d("doLogin", "Logging in with " + userEmail + " and " + userPassword);
    try {
      User user = RockServices.getSessionService().userLogin(new ImmutableCredentials(userEmail, userPassword, null));
      updateDialogMessage(getString(R.string.message_initialsetup_loggedin, user.getFirstName()));
      List<Venue> venueList = RockServices.getUserService().fetchVenues();
      Log.d("ManagerLoginFragment", "Venues: " + venueList.size());
      if (venueList.size() == 0) {
        throw new Error(ErrorCode.InvalidArgumentError, getResources().getString(R.string.message_error_no_venues_to_user));
      } else if (venueList.size() == 1) {
        updateDialogMessage(String.format(getResources().getString(R.string.message_initialsetup_onevenuefound), venueList.get(0).getBrand().getName()));
        selectedVenue = venueList.get(0);
        getDevices(selectedVenue.getId());
      } else {
        askForVenue(venueList);
      }

    } catch (Error e) {
      onError(e, "doLoginError");
    }
  }

}
