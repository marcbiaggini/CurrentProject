package com.rockspoon.rockandui.InitialSetup.Fragments;

import android.os.Bundle;
import android.widget.Button;

import com.rockspoon.error.Error;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.Dialogs.GenericWaitingDialog;
import com.rockspoon.rockandui.Managers.RockManagerDeviceService;
import com.rockspoon.rockandui.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

/**
 * Created by Lucas Teske on 31/08/15.
 */

@EFragment
public class FirstScreenFragment extends RSBaseFragment {

  private RSDialog dialog;

  @ViewById(resName = "initialsetup_startsetupbtn")
  Button startSetup;

  @Override
  public String getFragmentTitle() {
    return "";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.initialsetup_firstscreen;
  }

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());
  }

  @Click(resName = "initialsetup_startsetupbtn")
  void startSetupClick() {
    final Bundle dialogArgs = new Bundle();
    dialogArgs.putString(GenericWaitingDialog.PARAM_TITLE, getString(R.string.message_please_wait));
    dialogArgs.putString(GenericWaitingDialog.PARAM_MESSAGE, getString(R.string.message_checking_network_connectivity));
    dialog = GenericWaitingDialog.newInstance(dialogArgs);
    dialog.show(getFragmentManager(), "checkNetworkDialog");
    afterCheckNetworkCriteria();
  }

  @Background
  void checkNetworkCriteria() {
    final RockManagerDeviceService rockManager = new RockManagerDeviceService(getActivity());
    rockManager.checkNetworkCriteria().then(onCheckNetwork).fail(onCheckNetworkError);
  }

  @UiThread
  void afterCheckNetworkCriteria() {
    dialog.dismiss();
    final ManagerLoginFragment_ frag = new ManagerLoginFragment_();
    replaceFragment(frag);
  }

  private FailCallback<Error> onCheckNetworkError = new FailCallback<Error>() {
    @Override
    public void onFail(Error result) {
      dialog.dismiss();
      final SelectWiFiFragment_ frag = new SelectWiFiFragment_();
      replaceFragment(frag);
    }
  };

  private DoneCallback<Void> onCheckNetwork = result -> afterCheckNetworkCriteria();

}
