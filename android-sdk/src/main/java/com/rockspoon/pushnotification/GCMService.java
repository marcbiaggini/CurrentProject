package com.rockspoon.pushnotification;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.GCMEvent;

/**
 * Created by lucas on 17/02/16.
 */
public class GCMService extends GcmListenerService {
  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */

  @Override
  public void onMessageReceived(String from, Bundle data) {
    Log.d("GCMService", "From: " + from);
    RockServices.getDataService().sendCacheBusMessage(new GCMEvent(from, data));
  }
}
