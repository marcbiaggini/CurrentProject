package com.rockspoon.rockpos.PaymentCompleted;

import android.app.Activity;
import android.content.Intent;

import com.rockspoon.rockpos.OpenTabs.OpenTabsActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.UiThreadExecutor;

/**
 * PaymentSplashActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 2/26/16.
 */
@EActivity(R.layout.rock_spoon_view)
public class PaymentSplashActivity extends Activity {

  private static final String HIDE_SCREEN_ID = "hide_screen";

  @AfterViews
  void setupUi() {
    hideScreenAfterDelay();
  }

  @UiThread(id = HIDE_SCREEN_ID, delay = 30000)
  void hideScreenAfterDelay() {
    OpenTabsActivity_.intent(this)
        .flags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .start();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    UiThreadExecutor.cancelAll(HIDE_SCREEN_ID);
  }
}
