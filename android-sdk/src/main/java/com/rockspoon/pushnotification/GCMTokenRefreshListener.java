package com.rockspoon.pushnotification;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by lucas on 17/02/16.
 */
public class GCMTokenRefreshListener extends InstanceIDListenerService {
  @Override
  public void onTokenRefresh() {
    // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
    Intent intent = new Intent(this, RegistrationIntentService.class);
    startService(intent);
  }
}
