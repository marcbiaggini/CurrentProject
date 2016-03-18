package com.rockspoon.rockpos.Login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.user.LoginUserData;
import com.rockspoon.models.user.User;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.rockandui.Adapters.LoginUserAdapter;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.rockpos.ClockInOut.ClockActivity_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.login_baselayout)
public class LoginActivity extends Activity implements KeypadDialogFragment.KeypadListener {

  private KeypadDialogFragment keypadFragment;

  @ViewById(R.id.login_users)
  ListView users;

  private String userEmail = "";
  private Employee selectedEmployee;

  public static void startActivity(Context context) {
    LoginActivity_.intent(context).start();
  }

  @ItemClick(R.id.login_users)
  void usersItemClick(LoginUserData user) {
    Log.d("Clicked User", user.getUserData().getFirstName());
    selectedEmployee = user.getEmployeeData();
    userEmail = user.getUserData().getEmail();
    openKeypad();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_baselayout);
    keypadFragment = KeypadDialogFragment.newInstance();
  }

  @AfterViews
  void initViews() {
    final LoginUserAdapter adapter = new LoginUserAdapter(this, RockServices.getDataService().getVenueEmployees());
    users.setAdapter(adapter);
  }

  @Background
  void tryLogin(String shortPass) {
    try {
      User user = RockServices.getSessionService().userLogin(new ImmutableCredentials(userEmail, shortPass, null));
      RockServices.getDataService().setLogged(user, selectedEmployee);

      final String venueId = RockServices.getDataService().getDeviceVenueId();
      List<UserClockEvent> clockEvents = RockServices.getEmployeeService().getUserOpenClockEvents(venueId);
      RockServices.getDataService().updateCurrentClock(clockEvents);
      loginSuccess();
    } catch (Error e) {
      loginFailure(e);
    }
  }

  @UiThread
  void loginSuccess() {
    ClockActivity_.intent(LoginActivity.this).start();
    finish();
  }

  @UiThread
  void loginFailure(final Error error) {
    if (error.getErrorCode() == ErrorCode.WrongUsernamePasswordError) {
      Toast.makeText(LoginActivity.this, getString(R.string.wrong_password), Toast.LENGTH_LONG).show();
    } else {
      Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    keypadFragment.resetPass();
    keypadFragment.updateBalls();
    keypadFragment.shakeKeypad();
    keypadFragment.unlockKeypad();
  }

  public void showForgotPasswordDialog(String email) {
    final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
    final TextView emailCode = (TextView) dialogLayout.findViewById(R.id.forgot_password_codelabel);
    emailCode.setText(String.format(getResources().getString(R.string.enter_code_sent_email), email));

    final Button closeBtn = (Button) dialogLayout.findViewById(R.id.forgot_password_closebtn);
    final EditText password = (EditText) dialogLayout.findViewById(R.id.forgot_password_password);
    final EditText password_confirm = (EditText) dialogLayout.findViewById(R.id.forgot_password_password_confirm);
    final CheckBox showpass = (CheckBox) dialogLayout.findViewById(R.id.forgot_password_showpassword);

    showpass.setOnClickListener(v -> {
      if (showpass.isChecked()) {
        password.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        password_confirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        password.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        password_confirm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
      }
    });

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setView(dialogLayout);
    final AlertDialog dialog = builder.show();

    closeBtn.setOnClickListener(v -> dialog.dismiss());
  }

  public void openKeypad() {
    final View content = getWindow().getDecorView().findViewById(android.R.id.content);
    final Bitmap image = Tools.blur(content);

    keypadFragment.setBackgroundImage(new BitmapDrawable(getResources(), image));
    keypadFragment.show(getFragmentManager(), null);
  }

  @Override
  public void login(String pin) {
    tryLogin(pin);
  }

  @Override
  public void showForgetPasswordDialog() {
    showForgotPasswordDialog(userEmail);
  }

}
