package com.rockspoon.kitchentablet.UI.SetupScreen;

import android.app.ActivityManager;
import android.content.Context;
import android.view.KeyEvent;

import com.rockspoon.kitchentablet.UI.ChefScreenOrders.ChefInProgressActivity_;
import com.rockspoon.kitchentablet.UI.ClockScreen.ClockActivity_;
import com.rockspoon.kitchentablet.UI.SetupScreen.Fragments.SelectOrderModeFragment;
import com.rockspoon.kitchentablet.UI.SetupScreen.Fragments.SelectOrderModeFragment_;
import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * KitchenSetupActivity.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/1/16.
 */
@EActivity(R.layout.kitchen_setup_activity)
public class KitchenSetupActivity extends RSBaseActivity {
  private KitchenSetupData setupData;
  private OnSetupDataFragment currentFragment;

  @AfterViews
  void initViews() {
    setupData = new KitchenSetupData();

    SelectOrderModeFragment frag = SelectOrderModeFragment_.builder().build();
    frag.setSetupData(setupData);
    Tools.hideNavigationBar(this);
    currentFragment = frag;
    addRootFragment(frag);
  }

  @Click(R.id.back_button)
  void backButtonClicked() {
    finish();
  }

  @Click(R.id.continue_button)
  void continueButtonClicked() {
    setupData = currentFragment.getSetupData();

    switch (setupData.getOrderViewMode()) {
      case INBOX:
        ClockActivity_.intent(this).start();
        break;
      case IN_PROGRESS:
        ChefInProgressActivity_.intent(this).start();
        break;
    }
  }

  public interface OnSetupDataFragment {
    void setSetupData(KitchenSetupData data);

    KitchenSetupData getSetupData();
  }

  @Override
  protected void onUserLeaveHint() {
    super.onUserLeaveHint();
    ((ActivityManager) getApplicationContext().
        getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
  }

  @Override
  protected void onPause() {
    super.onPause();
    ((ActivityManager) getApplicationContext().
        getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_HOME:
      case KeyEvent.KEYCODE_BACK:
        return true;
      case KeyEvent.KEYCODE_MENU:
        onBackPressed();
        return true;
      default:
        return false;
    }
  }
}
