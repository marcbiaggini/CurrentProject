package com.rockspoon.rockandui.Components;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

import com.rockspoon.error.Error;
import com.rockspoon.error.RestErrorEvent;
import com.rockspoon.helpers.BusProvider;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.R;
import com.squareup.otto.Subscribe;

/**
 * Created by greenfrvr
 */
public abstract class RSBaseActivity extends Activity {

  protected RSDialog baseDialog;
  private int rootFragmentIdentifier = -1;
  private final static String TAG = "RSBaseActivity";

  private final Object busCallback = new Object() {
    @Subscribe
    public void handleRestErrorEvent(RestErrorEvent event) {
      Log.e(TAG, event.getMessage());
      onError(new Error(event.getErrorCode(), event.getMessage()));
    }
  };

  public void addRootFragment(Fragment fragment) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.setCustomAnimations(0, R.animator.slide_left_exit, R.animator.slide_right_enter, R.animator.slide_right_exit);
    transaction.add(R.id.fragment_container, fragment);
    transaction.addToBackStack(null);

    rootFragmentIdentifier = transaction.commit();
  }

  public void replaceFragment(Fragment fragment, boolean clearStack, boolean addToStack) {
    if (clearStack && rootFragmentIdentifier >= 0) {
      getFragmentManager().popBackStack(rootFragmentIdentifier, 0);
    }

    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.setCustomAnimations(R.animator.slide_left_enter, R.animator.slide_left_exit, R.animator.slide_right_enter, R.animator.slide_right_exit);
    transaction.replace(R.id.fragment_container, fragment);

    if (addToStack) {
      transaction.addToBackStack(null);
    }

    transaction.commit();
  }

  public void replaceFragment(Fragment fragment, boolean clearStack) {
    replaceFragment(fragment, clearStack, true);
  }

  @Override
  public void onBackPressed() {
    if (getFragmentManager().getBackStackEntryCount() > 1) {
      getFragmentManager().popBackStack();
    } else {
      if (isRootActivity()) {
        moveTaskToBack(false);
      } else {
        finish();
      }
    }
  }

  public void clearStack() {
    if (rootFragmentIdentifier >= 0) {
      getFragmentManager().popBackStack(rootFragmentIdentifier, 0);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    BusProvider.getInstance().register(busCallback);
  }

  @Override
  protected void onPause() {
    super.onPause();
    BusProvider.getInstance().unregister(busCallback);
  }

  protected boolean isRootActivity() {
    return false;
  }

  protected void onError(Error error) {
    if (baseDialog != null) {
      baseDialog.dismiss();
    }

    baseDialog = GenericMessageDialog.newInstance()
        .setTitle(getString(R.string.error))
        .setMessage(error.getMessage());
    baseDialog.show(getFragmentManager(), "logoutErrorDialog");
  }

}
