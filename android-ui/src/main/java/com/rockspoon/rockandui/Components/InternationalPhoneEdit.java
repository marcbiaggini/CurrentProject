package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rockspoon.rockandui.CountryTools;
import com.rockspoon.exceptions.CountryNotFoundException;
import com.rockspoon.exceptions.CountryToolsNotInitializedException;
import com.rockspoon.rockandui.Objects.PhoneCountryData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.Map;

/**
 * Created by lucas on 09/07/15.
 */
@EViewGroup(resName = "intl_phone_edit")
public class InternationalPhoneEdit extends LinearLayout {

  @ViewById(resName = "intl_phone_country")
  Spinner countryCode;

  @ViewById(resName = "intl_phone_country_view")
  ImageView countryCodeView;

  @ViewById(resName = "intl_phone_area_code_view")
  TextView areaCodeView;

  @ViewById(resName = "intl_phone_area_code")
  EditText areaCode;

  @ViewById(resName = "intl_phone_phone_view")
  TextView phoneNumberView;

  @ViewById(resName = "intl_phone_phone")
  EditText phoneNumber;

  @ViewById(resName = "intl_phone_view")
  LinearLayout viewLayout;

  @ViewById(resName = "intl_phone")
  LinearLayout editLayout;

  private int selectedCountry;

  private int newSelectedCountry;

  public InternationalPhoneEdit(final Context ctx) {
    this(ctx, null);
  }

  public InternationalPhoneEdit(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public InternationalPhoneEdit(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
  }

  @AfterViews
  public void initComponent() {
    countryCode.setAdapter(CountryTools.getAdapter());
    // US by default
    if (this.selectedCountry <= 0) {
      this.selectedCountry = 1;
      this.newSelectedCountry = 1;
      try {
        setCountry(this.selectedCountry);
      } catch (CountryNotFoundException | CountryToolsNotInitializedException e) {
        Log.wtf("InternationalPhoneEdit", "No USA on the country list! DAFUQ?");
      }
    }
  }

  public boolean isEmpty() {
    return areaCode.getText().toString().isEmpty() || phoneNumber.getText().toString().isEmpty();
  }

  public void setCountry(int countryPrefix) throws CountryNotFoundException, CountryToolsNotInitializedException {
    setCountry(countryPrefix, false);
  }

  public void setCountry(int countryPrefix, boolean updateView) throws CountryNotFoundException, CountryToolsNotInitializedException {
    this.newSelectedCountry = countryPrefix;
    final int idx = CountryTools.getDataIdxByPrefix(countryPrefix);
    countryCode.setSelection(idx, false);
    if (updateView)
      countryCodeView.setImageResource(CountryTools.getImageByPrefix(countryPrefix));
  }

  public void setPhoneNumber(final int phoneNumber) {
    this.phoneNumber.setText(phoneNumber);
  }

  public int getCountryPrefix() {
    return ((PhoneCountryData) this.countryCode.getSelectedItem()).countryPrefix;
  }

  public String getPhoneString() {
    return "+" + getCountryPrefix() + "" + areaCode.getText() + phoneNumber.getText();
  }

  public String getFormattedPhoneString() {
    return "+" + getCountryPrefix() + " " + areaCode.getText() + " " + phoneNumber.getText();
  }

  public String getPhone() {
    return phoneNumber.getText().toString();
  }

  public String getAreaCode() {
    return areaCode.getText().toString();
  }

  public void setAreaCode(final int areaCode) {
    this.areaCode.setText(Integer.toString(areaCode));
  }

  public void setPhoneMap(final Map<String, String> phones) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (phones != null && phones.containsKey("phone")) {
      String mobile = phones.get("phone");
      if (mobile != null && mobile.contains(" ")) {
        String[] phoneData = mobile.split(" ");
        if (phoneData[0].startsWith("+")) {
          this.selectedCountry = Integer.parseInt(phoneData[0].substring(1));
          setCountry(this.selectedCountry, true);
        }
        if (phoneData.length == 3) {
          this.areaCode.setText(phoneData[1]);
          this.areaCodeView.setText(phoneData[1]);
        }
        if (phoneData.length >= 2) {
          this.phoneNumber.setText(phoneData[2]);
          this.phoneNumberView.setText(phoneData[2]);
        }
      }
    }
  }

  public void cancelEdition() {
    this.phoneNumber.setText(this.phoneNumberView.getText());
    this.areaCode.setText(this.areaCodeView.getText());
    try {
      this.setCountry(this.selectedCountry, false);
    } catch (Exception e) {
    }
  }

  public void commitEdition() {
    try {
      this.phoneNumberView.setText(this.phoneNumber.getText().toString());
      this.areaCodeView.setText(this.areaCode.getText().toString());
      countryCodeView.setImageResource(CountryTools.getImageByPrefix(this.newSelectedCountry));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public void makeItReadOnly() {
    this.editLayout.setVisibility(View.GONE);
    this.viewLayout.setVisibility(View.VISIBLE);
  }

  public void makeItEditable() {
    this.editLayout.setVisibility(View.VISIBLE);
    this.viewLayout.setVisibility(View.GONE);
  }

}
