package com.rockspoon.rockandui.Objects;

/**
 * Created by lucas on 09/07/15.
 */
public class PhoneCountryData {
  public final String countryName;
  public final int countryPrefix;
  public final String countryCode;
  public final int imageResource;

  public PhoneCountryData(String countryName, String countryCode, int countryPrefix, int imageResource) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.countryPrefix = countryPrefix;
    this.imageResource = imageResource;
  }
}
