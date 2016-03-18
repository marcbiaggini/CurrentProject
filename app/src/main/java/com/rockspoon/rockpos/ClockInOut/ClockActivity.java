package com.rockspoon.rockpos.ClockInOut;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockpos.Login.LoginActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

/**
 * Created by lucas on 02/12/15.
 */
@EActivity(R.layout.clockout_activity)
public class ClockActivity extends RSBaseActivity {

  @Override
  public void onBackPressed() {
    doLogout();
  }

  public void doLogout() {
    if (baseDialog != null) {
      baseDialog.dismiss();
    }

    baseDialog = GenericMessageDialog
        .newInstance()
        .setTitle(getString(com.rockspoon.rockandui.R.string.message_please_wait))
        .setMessage(getString(com.rockspoon.rockandui.R.string.message_logging_out));
    baseDialog.show(getFragmentManager(), "loggingOutDialog");

    backgroundDoLogout();
  }

  @Background
  protected void backgroundDoLogout() {
    RockServices.getUserService().disconnect();
    afterLogout();
  }

  @UiThread
  protected void afterLogout() {
    baseDialog.dismiss();
    RockServices.getDataService().logout();

    LoginActivity_.intent(this).start();
    finish();
  }

}
