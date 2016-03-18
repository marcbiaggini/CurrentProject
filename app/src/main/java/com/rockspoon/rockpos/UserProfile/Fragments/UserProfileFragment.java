package com.rockspoon.rockpos.UserProfile.Fragments;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.user.User;
import com.rockspoon.rockandui.Components.DualEditText;
import com.rockspoon.rockandui.Components.InternationalPhoneEdit;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockpos.Camera.CameraActivity;
import com.rockspoon.rockpos.Camera.CameraActivity_;
import com.rockspoon.rockpos.Camera.CameraStorage;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.AfterViews;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by raphael.pinheiro on 17/12/2015.
 */
@EFragment
public class UserProfileFragment extends RSBaseFragment {

  private final int PROFILE_CAMERA_RESULT_CODE = 101;

  @ViewById(R.id.user_profile_firstName_txt)
  TextView firstNameTxt;

  @ViewById(R.id.user_profile_firstName_input)
  EditText firstNameInput;

  private DualEditText firstName;

  @ViewById(R.id.user_profile_lastName_txt)
  TextView lastNameTxt;

  @ViewById(R.id.user_profile_lastName_input)
  EditText lastNameInput;

  private DualEditText lastName;

  @ViewById(R.id.user_profile_userEmail_txt)
  TextView emailTxt;

  @ViewById(R.id.user_profile_userEmail_input)
  EditText emailInput;

  private DualEditText email;

  @ViewById(R.id.user_profile_userMobile_input2)
  InternationalPhoneEdit mobileInternational;

  @ViewById(R.id.user_profile_userImage)
  protected ImageView userImage;

  @ViewById(R.id.user_profile_save)
  protected Button saveButton;

  @ViewById(R.id.user_profile_cancel)
  protected Button cancelButton;

  @Override
  public String getFragmentTitle() {
    return "User Profile";
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.user_profile_fragment;
  }

  @AfterViews
  void init() {
    setTopBarTitle(getFragmentTitle());

    User user = RockServices.getDataService().getLoggedUser();
    firstName = new DualEditText(firstNameTxt, firstNameInput);
    firstName.setText(user.getFirstName());

    lastName = new DualEditText(lastNameTxt, lastNameInput);
    lastName.setText(user.getLastName());

    email = new DualEditText(emailTxt, emailInput);
    email.setText(user.getEmail());

    try {
      mobileInternational.setVisibility(View.VISIBLE);
      mobileInternational.setPhoneMap(user.getPhone());
    } catch (Exception e) {
      Log.e(UserProfileFragment.class.getName(), "Error setting phone value", e);
    }

    if (user.getAvatar() != null) {
      final ImageData_ avatarData = ImageData_.getInstance_(getActivity());
      avatarData.from(user.getAvatar().getNoResolution().getUrl());
      userImage.setImageDrawable(avatarData.getImage(getActivity(), userImage));
    }
    // It was decided that for now user will enter this page in EDIT mode
    makeItEditable();
    // makeItReadOnly();
  }

  @Background
  public void updateUser() {
    Map<String, String> phone = new HashMap<>();
    User loggedUser = RockServices.getDataService().getLoggedUser();
    phone.putAll(loggedUser.getPhone());
    phone.put("phone", mobileInternational.getFormattedPhoneString());

    User updatedUser = new User().withFirstName(firstNameInput.getText().toString()).withLastName(lastNameInput.getText().toString())//
        .withEmail(emailInput.getText().toString()).withPhone(phone);

    if (CameraStorage.getImageB64() != null) {
      Image image = new Image(CameraStorage.getImageB64());
      updatedUser = updatedUser.withAvatar(image);
    }
    try {
      RockServices.getUserService().update(updatedUser);
      processSuccess();
    } catch (RestClientException e) {
      //TODO: Better Error Handler
      onError(new com.rockspoon.error.Error(ErrorCode.InternalError, e.getLocalizedMessage()), "updateUserProfileError");
    }
  }

  @UiThread
  @Override
  protected void onError(final com.rockspoon.error.Error error, final String label) {
    closeWaitingDialog();
    super.onError(error, label);
  }

  @UiThread
  protected void processSuccess() {
    closeWaitingDialog();
    onSuccess(getString(R.string.change_profile_success), "changeProfileSuccessDialog");
  }

  @Click(R.id.user_profile_save)
  void saveButtonClick() {
    showWaitingDialog(null, getString(R.string.message_saving_information), "savingProfile");
    updateUser();
  }

  @Click(R.id.user_profile_cancel)
  void cancelButtonClick() {
    getActivity().onBackPressed();
  }

  @Click(R.id.user_profile_userImage)
  void profileImageClick() {
    CameraActivity_.intent(this).startForResult(PROFILE_CAMERA_RESULT_CODE);
  }

  @Background
  protected void resizeImage() {
    CameraStorage.resizeStoredImage(640, 640);
    setUserImage(CameraStorage.getImageBmp());
  }

  @UiThread
  protected void setUserImage(final Bitmap bmp) {
    if (bmp != null)
      userImage.setImageBitmap(bmp);
    closeWaitingDialog();
  }

  @OnActivityResult(PROFILE_CAMERA_RESULT_CODE)
  protected void onResult(int resultCode) {
    if (resultCode == CameraActivity.RESULT_OK) {
      Log.d(UserProfileFragment.class.getName(), "Picture data received");
      showWaitingDialog(null, getString(R.string.message_processing_image), "processingImageDialog");
      resizeImage();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    CameraStorage.clear();
  }

  private void makeItEditable() {
    firstName.makeItEditable();
    lastName.makeItEditable();
    email.makeItEditable();
    mobileInternational.makeItEditable();
  }

}
