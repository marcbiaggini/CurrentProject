package com.rockspoon.rockpos.pos.test.scenarios.Resources.HandlerEvents;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by juancamilovilladuarte on 3/16/16.
 */
public class EventCountDownHandler {
  private Countdown countdown = null;

  public void setCountdown(Countdown countdown) {
    cancelPreviousCountdownIfExists();
    this.countdown = countdown;
    startCountdown();
  }

  private void cancelPreviousCountdownIfExists() {
    if (this.countdown != null) {
      this.countdown.stop();
    }
  }

  private void startCountdown() {
    countdown.startCounting();
  }

  public Handler makeHandler() {
    return new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case Countdown.COUNTDOWN_STARTED:
            //onStartCountdown();
            break;
          case Countdown.COUNTDOWN_UPDATED:
            //onUpdate(msg.arg1);
            break;
          case Countdown.COUNTDOWN_FINISHED:
            //onEndCountdown();
            break;
        }
      }
    };
  }
}
