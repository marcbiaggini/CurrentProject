package com.rockspoon.rockpos.UserProfile.Fragments;

import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.models.user.ImmutableChangePasswordRequest;
import com.rockspoon.models.user.PasswordType;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.web.client.RestClientException;

/**
 * Created by raphael.pinheiro on 17/12/2015.
 */
@EFragment
public class ChangePasswordFragment extends RSBaseFragment {

  @ViewById(R.id.change_password_current)
  EditText currentPassword;

  @ViewById(R.id.change_password_new)
  EditText newPassword;

  @ViewById(R.id.change_password_confirmation)
  EditText newPasswordConfirmation;

  @ViewById(R.id.change_password_save)
  Button save;

  @ViewById(R.id.change_password_show)
  CheckBox showPassword;

  @Click(R.id.change_password_save)
  void updatePasswordClick() {
    updatePassword();
  }

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());
  }

  @Click(R.id.change_password_show)
  void showHidePassword() {
    if (Boolean.TRUE.equals(showPassword.isChecked())) {
      currentPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
      newPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
      newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_NUMBER);
    } else {
      currentPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      newPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    }
  }

  @Override
  public String getFragmentTitle() {
    return "Change Password";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.change_password_fragment;
  }

  @Background
  public void updatePassword() {
    try {
      RockServices.getUserService().changePassword(new ImmutableChangePasswordRequest(currentPassword.getText().toString(), newPassword.getText().toString(), newPasswordConfirmation.getText().toString(), PasswordType.shortPassword));
      processSuccess();
    } catch (RestClientException e) {
      onError(new com.rockspoon.error.Error(ErrorCode.InternalError, e.getLocalizedMessage()), "changePasswordErrorDialog");
    }
  }

  @UiThread
  @Override
  protected void onError(final Error error, final String label) {
    super.onError(error, label);
  }

  @UiThread
  protected void processSuccess() {
    onSuccess(getString(R.string.change_password_success), "changePasswordSuccessDialog");
    currentPassword.setText(null);
    newPassword.setText(null);
    newPasswordConfirmation.setText(null);
    showPassword.setChecked(false);
    currentPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    newPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
    currentPassword.requestFocus();
  }

}
