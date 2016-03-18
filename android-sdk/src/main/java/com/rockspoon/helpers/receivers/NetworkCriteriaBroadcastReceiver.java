package com.rockspoon.helpers.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rockspoon.services.Actions;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.ReceiverAction;

/**
 * Created by lucas on 10/03/16.
 */
@EReceiver
public class NetworkCriteriaBroadcastReceiver extends BroadcastReceiver {

  @ReceiverAction(actions = {Actions.CHECK_NETWORK_CRITERIA_REQUEST})
  void connectToWifi(Context ctx, Intent intent) {
    final Intent criteriaResult = new Intent(Actions.CHECK_NETWORK_CRITERIA_RESPONSE);
        /*if (!PrinterTools.testSignalQuality(ManagerService.this, Session.minSignal)) {
          final Error err = new Error(ErrorCode.CriteriaNotMetError, "Cellphone signal is below " + Integer.toString(Session.minSignal) + "%");
          criteriaResult.putExtra("error", err);
        }*/ // Disable Criteria for now
    ctx.sendBroadcast(criteriaResult);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    // empty, will be overridden in generated subclass
  }
}
