package com.rockspoon.printer;

import android.content.Context;

import com.rockspoon.sdk.R;

/**
 * Created by lucas on 4/10/15.
 */
public class Session {

  /**
   * Minimum Signal Criteria in %
   */
  public static int minSignal = 40;

  public static long connectWiFiTimeout = 10 * 1000;  //  10 seconds wifi timeout

  public static void initSession(Context ctx) {
    try {
      minSignal = Integer.parseInt(ctx.getString(R.string.min_signal));
    } catch (NumberFormatException e) {
      minSignal = 40;
    }
  }
}
