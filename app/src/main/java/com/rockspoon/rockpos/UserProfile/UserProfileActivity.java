package com.rockspoon.rockpos.UserProfile;


import android.content.Context;

import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.EActivity;

/**
 * Created by raphael.pinheiro on 17/12/2015.
 */
@EActivity(R.layout.user_profile_activity)
public class UserProfileActivity extends RSBaseActivity {

  public static void startActivity(Context context) {
    UserProfileActivity_.intent(context).start();
  }

}