package com.rockspoon.rockpos.UserProfile;

import android.content.Context;

import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.EActivity;

/**
 * Created by raphael.pinheiro on 22/12/2015.
 */
@EActivity(R.layout.user_profile_activity)
public class ChangePasswordActivity extends RSBaseActivity {

  public static void startActivity(Context context) {
    ChangePasswordActivity_.intent(context).start();
  }
}
