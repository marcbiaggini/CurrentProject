package com.rockspoon.rockpos;

import android.content.Context;

import com.rockspoon.exceptions.DeviceNotInitializedException;
import com.rockspoon.exceptions.UnknownDeviceTypeException;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.kitchentablet.UI.SetupScreen.KitchenSetupActivity_;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.device.DeviceType;
import com.rockspoon.rockpos.OpenTabs.OpenTabsActivity_;
import com.rockspoon.rockpos.Planing.PlaningActivity_;

/**
 * Created by lucas on 21/01/16.
 */
public class MultiDeviceHandler {

  public static void startDeviceSpecificActivity(Context ctx) {
    Device device = RockServices.getDataService().getThisDevice();
    if (device != null) {
      DeviceType deviceType = device.getType();
      if (deviceType != null) {
        switch (deviceType.getId()) { // TODO: We should set a enum on deviceType Table to be more correct.
          case "1": // Fixed PoS
            PlaningActivity_.intent(ctx).start();
            return;
          case "2": // Mobile PoS
            NavigationActivity.startActivity(ctx);
            return;
          case "3": // Payment Tablet
            OpenTabsActivity_.intent(ctx).start();
            return;
          case "4": // Kitchen Tablet
            KitchenSetupActivity_.intent(ctx).start();
            return;
          case "5": // Runner Tablet
            // TODO: Runner Tablet
            NavigationActivity.startActivity(ctx);
            return;
          case "6": // Printer
            // TODO: Printer
            NavigationActivity.startActivity(ctx);
            return;
          default:
            throw new UnknownDeviceTypeException();
        }
      }
    }

    throw new DeviceNotInitializedException();
  }

}
