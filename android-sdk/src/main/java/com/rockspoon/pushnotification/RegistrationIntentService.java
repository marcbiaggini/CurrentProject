package com.rockspoon.pushnotification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.GCMRegistrationCompleteEvent;
import com.rockspoon.models.venue.device.UpdateDeviceGCMTokenRequest;
import com.rockspoon.sdk.BuildConfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by lucas on 17/02/16.
 */
public class RegistrationIntentService extends IntentService {

  private static final String TAG = "RSGCMREG";

  public RegistrationIntentService() {
    super(TAG);
  }

  public static boolean initializeGCM(final Context ctx) {
    if (checkPlayServices(ctx)) {
      // Start IntentService to register this application with GCM.
      Intent intent = new Intent(ctx, RegistrationIntentService.class);
      ctx.startService(intent);
      return true;
    } else {
      return false;
    }
  }

  private static boolean checkPlayServices(final Context ctx) {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(ctx);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (apiAvailability.isUserResolvableError(resultCode)) {
        Log.d(TAG, "Error: " + apiAvailability.getErrorString(resultCode));
      } else {
        Log.i(TAG, "This device is not supported.");
      }
      return false;
    }
    return true;
  }

  @Override
  protected void onHandleIntent(final Intent intent) {
    try {
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(BuildConfig.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

      Log.i(TAG, "GCM Registration Token: " + token);

      int count = 0;

      while (count < 10) {
        if (sendRegistrationToServer(token)) {
          break;
        }

        count++;

        if (count == 10) {
          throw new com.rockspoon.error.Error(ErrorCode.InternalError, "Cannot reach Rockspoon Cloud");
        }
      }

      subscribeTopics(token);
      RockServices.getDataService().setGCMTokenSent(true);
    } catch (Exception e) {
      Log.d(TAG, "Failed to complete token refresh", e);
      RockServices.getDataService().setGCMTokenSent(false);
    }

    RockServices.getDataService().sendCacheBusMessage(new GCMRegistrationCompleteEvent());
  }

  /**
   * Persist registration to third-party servers.
   * <p>
   * Modify this method to associate the user's GCM registration token with any server-side account
   * maintained by your application.
   *
   * @param token The new token.
   */
  private boolean sendRegistrationToServer(String token) {
    try {
      RockServices.getDeviceService().updateGCMToken(RockServices.getDataService().getDeviceVenueId(), new UpdateDeviceGCMTokenRequest(token));
      return true;
    } catch (com.rockspoon.error.Error error) {
      Log.e(TAG, "Error saving the token at the server: " + error.getLocalizedMessage());
      error.printStackTrace();
      return false;
    }
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   *
   * @param token GCM token
   * @throws IOException if unable to reach the GCM PubSub service
   */
  private void subscribeTopics(String token) throws IOException {
    GcmPubSub pubSub = GcmPubSub.getInstance(this);
    List<String> cacheTopics = RockServices.getDataService().getCacheTopics();
    for (String topic : cacheTopics) {
      pubSub.subscribe(token, topic, null);
      Log.d(TAG, "Subscribed to " + topic);
    }
  }
}
