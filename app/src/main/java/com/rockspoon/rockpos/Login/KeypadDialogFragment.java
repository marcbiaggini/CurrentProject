package com.rockspoon.rockpos.Login;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

/**
 * Created by greenfrvr
 */
@EFragment
public class KeypadDialogFragment extends DialogFragment {

  private String shortPass = "";

  @ViewsById({R.id.input_ball0, R.id.input_ball1, R.id.input_ball2, R.id.input_ball3})
  List<ImageView> keypadBalls;
  @ViewsById({R.id.input_key1, R.id.input_key2, R.id.input_key3, R.id.input_key4, R.id.input_key5,
      R.id.input_key6, R.id.input_key7, R.id.input_key8, R.id.input_key9, R.id.input_key0})
  List<Button> keypadButtons;
  @ViewById(R.id.login_keypad)
  View keypadView;
  @ViewById(R.id.input_password_keypad_pad)
  LinearLayout keypadPad;

  private BitmapDrawable background;
  private KeypadListener listener;

  private boolean keypadLocked = false;

  public static KeypadDialogFragment newInstance() {
    return new KeypadDialogFragment_();
  }

  @Click(R.id.input_password_back)
  void btnBackClick() {
    if (!keypadLocked) {
      dismiss();
    }
  }

  @Click(R.id.input_password_forgotpass)
  void forgotPasswordClick() {
    if (!keypadLocked) {
      listener.showForgetPasswordDialog();
    }
  }

  @Click(R.id.input_password_delete)
  void deleteInputPassword() {
    if (!keypadLocked) {
      resetPass();
      updateBalls();
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof KeypadListener) {
      listener = (KeypadListener) activity;
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.KeypadDialog);
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.keypad_layout, container, false);
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    resetPass();
    super.onDismiss(dialog);
  }

  @AfterViews
  void initViews() {
    keypadView.setBackground(background);

    for (Button button : keypadButtons) {
      button.setOnClickListener((v) -> {
        if (!keypadLocked) {
          shortPass += button.getText();
          updateBalls();
          if (shortPass.length() == 4) {
            lockKeypad();
            listener.login(shortPass);
          }
        }
      });
    }
  }

  public void setBackgroundImage(BitmapDrawable image) {
    background = image;
  }

  public void resetPass() {
    shortPass = "";
  }

  public void shakeKeypad() {
    final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    keypadPad.startAnimation(shake);
  }

  public void lockKeypad() {
    keypadLocked = true;
    for (final Button btn : keypadButtons) {
      btn.setEnabled(false);
    }
  }

  public void unlockKeypad() {
    keypadLocked = false;
    for (final Button btn : keypadButtons) {
      btn.setEnabled(true);
    }
  }

  public void updateBalls() {
    final int passwordLength = shortPass.length();
    for (int i = 0; i < keypadBalls.size(); ++i) {
      keypadBalls.get(i).setImageResource(i < passwordLength ? R.drawable.round_button_shape_selected : R.drawable.round_button_shape_normal);
    }
  }

  public interface KeypadListener {
    void login(String pin);

    void showForgetPasswordDialog();
  }

}
