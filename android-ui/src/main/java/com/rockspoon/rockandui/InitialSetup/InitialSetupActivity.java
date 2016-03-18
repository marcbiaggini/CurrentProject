package com.rockspoon.rockandui.InitialSetup;

import android.app.Activity;
import android.content.Intent;

import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockandui.InitialSetup.Fragments.FirstScreenFragment_;
import com.rockspoon.rockandui.Tools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Lucas Teske on 31/08/15.
 */
@EActivity(resName = "initial_setup_activity")
public class InitialSetupActivity extends RSBaseActivity {

  Class<? extends Activity> caller = null;

  @AfterViews
  @SuppressWarnings("unchecked")
  void initViewsChild() {
    Intent intent = getIntent();

    if (intent != null) {
      setCaller((Class<? extends Activity>) intent.getSerializableExtra("caller"));
    }

    final FirstScreenFragment_ frag = new FirstScreenFragment_();
    addRootFragment(frag);
  }

  public void setCaller(Class<? extends Activity> caller) {
    this.caller = caller;
  }

  public void closeInitialSetup() {
    RockServices.getDataService().reload();
    if (this.caller != null) {
      final Intent intent = new Intent(this, this.caller);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finishAffinity();
    } else {
      Tools.reboot();
    }
  }

}
