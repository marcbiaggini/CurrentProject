package com.rockspoon.rockandui.Components;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.Dialogs.GenericWaitingDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by greenfrvr
 */
public abstract class RSBaseFragment extends Fragment implements TopBar.BackButtonListener, TopBar.ImageButtonListener, TopBar.ExtraButtonListener, TopBar.TitleClickListener {

  private TopBar topBar;

  protected RSDialog dialog;

  @LayoutRes
  public abstract int getFragmentLayoutId();

  public abstract String getFragmentTitle();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(getFragmentLayoutId(), container, false);
    topBar = (TopBar) view.findViewById(R.id.topbar);
    if (topBar != null) {
      topBar.setBackButtonListener(this);
      topBar.setImageButtonListener(this);
      topBar.setExtraButtonListener(this);
      topBar.setTitleClickListener(this);
    }
    return view;
  }

  @Nullable
  public TopBar getTopBar() {
    return topBar;
  }

  public void setTopBarTitle(String title) {
    if (topBar != null) {
      topBar.setTitle(title);
    }
  }

  public void setTopBarTitle(@StringRes int titleRes) {
    if (topBar != null) {
      topBar.setTitle(titleRes);
    }
  }

  public void setTopBarImageButton(@DrawableRes int imageRes) {
    if (topBar != null) {
      topBar.setImageButtonRes(imageRes);
    }
  }

  public void setTopBarExtraButton(@StringRes int titleRes) {
    if (topBar != null) {
      topBar.setExtraButtonText(titleRes);
    }
  }

  public void replaceFragment(Fragment fragment) {
    replaceFragment(fragment, false, true);
  }

  public void replaceFragment(Fragment fragment, boolean clearStack) {
    replaceFragment(fragment, clearStack, true);
  }

  public void replaceFragment(Fragment fragment, boolean clearStack, boolean addToStack) {
    if (getActivity() instanceof RSBaseActivity) {
      ((RSBaseActivity) getActivity()).replaceFragment(fragment, clearStack, addToStack);
    }
  }

  public void clearFragmentsStack() {
    ((RSBaseActivity) getActivity()).clearStack();
  }

  protected void onError(final Error error, final String label) {
    closeWaitingDialog();

    ErrorCode code = error.getErrorCode();
    String message;
    int codeId;
    if (code != null && (codeId = getResources().getIdentifier(code.toString(), "string",
        getActivity().getPackageName())) > 0) {
      message = getResources().getString(codeId);
    } else {
      message = error.getMessage();
    }

    showDialog(getString(R.string.error), message, label);
  }

  protected void onSuccess(final String message, final String label) {
    closeWaitingDialog();
    showDialog(getString(R.string.success), message, label);
  }

  protected void showWaitingDialog(final String title, final String message, final String label) {
    dialog = GenericWaitingDialog.newInstance()
        .setMessage(message)
        .setTitle(title);
    dialog.show(getFragmentManager(), label);
  }

  protected void closeWaitingDialog() {
    try {
      if (dialog != null)
        dialog.dismiss();
      dialog = null;
    } catch (Exception e) {
      // If got an exception is because the dialog is already dismissed. So no worries.
    }
  }

  private void showDialog(final String title, final String message, final String label) {
    GenericMessageDialog.newInstance()
        .setMessage(message)
        .setTitle(title)
        .show(getFragmentManager(), label);
  }

  @Override
  public void onBackClicked() {
    getActivity().onBackPressed();
  }

  @Override
  public void onImageButtonClicked() {
  }

  @Override
  public void onExtraButtonClicked() {
  }

  @Override
  public void onTitleClicked() {
  }
}
