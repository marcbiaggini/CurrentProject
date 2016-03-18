package com.rockspoon.rockpos.PaymentCompleted.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.rockspoon.rockandui.CountryTools;
import com.rockspoon.rockandui.Dialogs.ChooseCountryDialog;
import com.rockspoon.exceptions.CountryNotFoundException;
import com.rockspoon.exceptions.CountryToolsNotInitializedException;
import com.rockspoon.rockandui.Objects.PhoneCountryData;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import timber.log.Timber;

/**
 * Created by Eugen K. on 2/4/16.
 */
@EFragment(R.layout.send_link_fragment)
public class SendLinkFragment extends Fragment implements AdapterView.OnItemClickListener {

  public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
  private PhoneNumberUtil phoneUtil;

  public interface SendLinkFragmentListener {
    void onSendLinkFragmentReady();

    void onPaymentDone();

    void onSendLinkCancel();
  }

  private static final String TAG_DROPDOWN_DIALOG = "dropDownDialog";
  private static final String TAG = "SendLinkFragment";

  private SendLinkFragmentListener listener;
  private ChooseCountryDialog dialog;
  private InputMethodManager keyboard;

  @ViewById(R.id.send_link_view)
  LinearLayout sendLinkView;

  @ViewById(R.id.link_sent_view)
  LinearLayout linkSentView;

  @ViewById(R.id.country_drop_down)
  LinearLayout countryDropDownView;

  @ViewById(R.id.phobe_number)
  EditText phoneNumberView;

  @ViewById(R.id.country_prefix)
  EditText countryPrefixView;

  @ViewById(R.id.email_address)
  EditText emailAddressView;

  @ViewById(R.id.send_link_button)
  Button sendLinkButton;

  @ViewById(R.id.cancel_button)
  Button cancelButton;

  @ViewById(R.id.link_sent_label)
  TextView linkSentLabelView;

  @ViewById(R.id.link_address_label)
  TextView linkAddressView;

  @ViewById(R.id.send_link_done)
  Button linkSentDoneView;

  @ViewById(R.id.country_flag_icon)
  ImageView countryFlagView;

  private PhoneCountryData selectedCountry;

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    try {
      selectedCountry = CountryTools.getItem(position);
      countryFlagView.setImageResource(selectedCountry.imageResource);
      countryPrefixView.setText(String.format(getString(R.string.format_country_prefix_format), selectedCountry.countryPrefix));
      checkFieldsValidation();
    } catch (CountryNotFoundException e) {
      Timber.e(TAG + " - " + e.getMessage());
    } catch (CountryToolsNotInitializedException e) {
      Timber.e(TAG + " - " + e.getMessage());
    }

    dialog.dismiss();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (dialog != null) {
      dialog.dismiss();
    }
  }

  @AfterViews
  void setUpUi() {
    phoneUtil = PhoneNumberUtil.getInstance();
    emailAddressView.setOnFocusChangeListener((v1, hasFocus) -> {
      if (hasFocus) {
        onEmailClick();
      }
    });
    emailAddressView.setOnEditorActionListener(onEditorActionListener);

    phoneNumberView.setOnFocusChangeListener((v1, hasFocus) -> {
      if (hasFocus) {
        onPhoneNumberClick();
      }
    });
    phoneNumberView.setOnEditorActionListener(onEditorActionListener);

    countryDropDownView.setOnClickListener(v -> {
      onCountryDropDownClick();
    });
    keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
  }

  @Click(R.id.send_link_button)
  void onSendClick() {
    //TODO send email or text message
    sendLinkView.setVisibility(View.GONE);
    linkSentView.setVisibility(View.VISIBLE);
    setLinkSentViewUi();
  }

  @Click(R.id.cancel_button)
  void onCancel() {
    if (listener != null) {
      listener.onSendLinkCancel();
    }
  }

  @Click(R.id.send_link_done)
  void onLinkSentDoneButton() {
    if (listener != null) {
      listener.onPaymentDone();
    }
  }

  @AfterTextChange({R.id.phobe_number, R.id.email_address})
  void afterTextChangedPhoneNumber(Editable text, TextView hello) {
    if (isPhoneNumberValidValid(phoneNumberView.getText().toString()) || isEmailValid(emailAddressView.getText().toString())) {
      sendLinkButton.setEnabled(true);
    } else {
      sendLinkButton.setEnabled(false);
    }
  }


  private void onPhoneNumberClick() {
    phoneNumberView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));
    countryPrefixView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));
    countryDropDownView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));

    emailAddressView.setBackground(getResources().getDrawable(R.drawable.border_background_transparent));
    emailAddressView.setText("");
  }

  private void onCountryDropDownClick() {
    dialog = ChooseCountryDialog.newInstance();
    dialog.setItemClickListener(this);
    dialog.show(getFragmentManager(), TAG_DROPDOWN_DIALOG);

    phoneNumberView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));
    countryPrefixView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));
    countryDropDownView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));

    emailAddressView.setBackground(getResources().getDrawable(R.drawable.border_background_transparent));
    emailAddressView.setText("");
  }

  private void onEmailClick() {
    phoneNumberView.setBackground(getResources().getDrawable(R.drawable.border_background_transparent));
    countryPrefixView.setBackground(getResources().getDrawable(R.drawable.border_background_transparent));
    countryDropDownView.setBackground(getResources().getDrawable(R.drawable.border_background_transparent));
    countryPrefixView.setText("");
    phoneNumberView.setText("");

    emailAddressView.setBackground(getResources().getDrawable(R.drawable.border_black_background_transparent));
  }

  public void setListener(SendLinkFragmentListener listener) {
    this.listener = listener;
  }

  private void setLinkSentViewUi() {
    if (emailAddressView.length() > 0) {
      linkSentLabelView.setText(getString(R.string.email_sent));
      linkAddressView.setText(emailAddressView.getText());
    } else if (phoneNumberView.length() > 0) {
      linkSentLabelView.setText(getString(R.string.text_message_sent));
      linkAddressView.setText(String.format(getString(R.string.format_phone_number_format), countryPrefixView.getText().toString(), phoneNumberView.getText().toString()));
    }
  }

  private TextView.OnEditorActionListener onEditorActionListener = (v, actionId, event) -> {
    checkFieldsValidation();

    if (actionId == EditorInfo.IME_ACTION_DONE) {
      keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    return true;
  };

  private void checkFieldsValidation() {
    if (isPhoneNumberValidValid(phoneNumberView.getText().toString()) || isEmailValid(emailAddressView.getText().toString())) {
      sendLinkButton.setEnabled(true);
    } else {
      sendLinkButton.setEnabled(false);
    }
  }

  private boolean isEmailValid(String email) {
    email = email.trim();
    if (email.matches(EMAIL_PATTERN) && email.length() > 0) {
      return true;
    }
    return false;
  }

  private boolean isPhoneNumberValidValid(String phone) {
    if (selectedCountry != null && !phone.isEmpty()) {
      try {
        String fullCountryNumber = "+" + selectedCountry.countryPrefix + phone;
        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(fullCountryNumber, selectedCountry.countryCode);
        return PhoneNumberUtil.getInstance().isValidNumber(numberProto);
      } catch (NumberParseException e) {
        return false;
      }
    }
    return false;
  }

}

