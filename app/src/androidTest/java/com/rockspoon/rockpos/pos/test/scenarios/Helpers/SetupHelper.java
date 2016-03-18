package com.rockspoon.rockpos.pos.test.scenarios.Helpers;

import android.util.Log;

import com.rockspoon.error.Error;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.device.Device;

import java.util.List;

/**
 * Created by juancamilovilladuarte on 3/18/16.
 */
public class SetupHelper {

  private Venue selectedVenue;
  private Device selectedDevice;

  public void doLogin(final String userEmail, final String userPassword) {
    Log.d("doLogin", "Logging in with " + userEmail + " and " + userPassword);
    try {
      User user = RockServices.getSessionService().userLogin(new ImmutableCredentials(userEmail, userPassword, null));
      List<Venue> venueList = RockServices.getUserService().fetchVenues();
      Log.d("ManagerLoginFragment", "Venues: " + venueList.size());
      if (venueList.size() == 0) {
      } else if (venueList.size() == 1) {
        selectedVenue = venueList.get(0);
        getDevices();
      } else {
        final String[] venueNames = new String[venueList.size()];
        int i = 0;
        for (Venue venue : venueList) {
          venueNames[i] = venue.getBrand().getName() + "\n" + venue.getAddress().getAddress1();
          i++;
        }
        selectedVenue = venueList.get(4);
        getDevices();
      }

    } catch (Error e) {

    }
  }

  void getDevices() {
    try {
      List<Device> deviceList = RockServices.getDeviceService().list(selectedVenue.getId());
      final String[] deviceNames = new String[deviceList.size()];
      int i = 0;
      for (Device device : deviceList) {
        deviceNames[i] = device.getName() + "(" + device.getStatus().toString() + ")";
        i++;
      }
      Device d = RockServices.getSessionService().deviceLogin(deviceList.get(0).getId(), selectedVenue.getId());
      RockServices.getUserService().disconnect();
      RockServices.getDataService().setDeviceVenueId(selectedVenue.getId());
      RockServices.getDataService().setDeviceId(selectedDevice.getId());
    } catch (Error e) {

    }
  }

}
