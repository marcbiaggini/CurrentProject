package com.rockspoon.rockandui;

import android.content.Context;
import android.util.Log;

import com.rockspoon.rockandui.Adapters.PhoneCountryDataAdapter;
import com.rockspoon.exceptions.CountryNotFoundException;
import com.rockspoon.exceptions.CountryToolsNotInitializedException;
import com.rockspoon.rockandui.Objects.PhoneCountryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lucas on 09/07/15.
 */
public class CountryTools {

  private static List<PhoneCountryData> countryList = null;
  private static HashMap<String, Integer> countryListByCode = null;
  private static HashMap<Integer, Integer> countryListByPrefix = null;
  private static PhoneCountryDataAdapter adapter = null;

  public static void InitCountries(final Context ctx) {
    Log.d("CountryTools", "Initializing Country Data");

    final String[] countryData = ctx.getResources().getStringArray(R.array.country_codes);

    countryList = new ArrayList<>(countryData.length);
    countryListByCode = new HashMap<>(countryData.length);
    countryListByPrefix = new HashMap<>(countryData.length);

    for (final String country : countryData) {
      try {
        final String[] data = country.split("\\|");
        int resId = Tools.getResourceByName(ctx, "drawable", "country_" + data[1].trim());
        resId = resId == 0 ? R.drawable.no_country : resId;

        countryListByCode.put(data[1].trim(), countryList.size());
        countryListByPrefix.put(Integer.parseInt(data[0]), countryList.size());
        countryList.add(new PhoneCountryData(data[2].trim(), data[1].trim(), Integer.parseInt(data[0].trim()), resId));

      } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
        //  Ignore this intentionally. If the data on arrays.xml is wrong, we cannot add this entry
      }
    }
    adapter = new PhoneCountryDataAdapter(ctx);
    Log.d("CountryTools", "Initialized " + countryList.size() + " countries.");
  }

  public static PhoneCountryData getCountryByCode(final String code) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (countryListByCode != null) {
      if (countryListByCode.containsKey(code))
        return countryList.get(countryListByCode.get(code));
      else
        throw new CountryNotFoundException();   //  TODO Exception with the Code Parameter
    } else {
      Log.e("CountryTools", "CountryTools not Initialized!");
      throw new CountryToolsNotInitializedException();
    }
  }

  public static PhoneCountryData getCountryByPrefix(final int prefix) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (countryListByCode != null) {
      if (countryListByPrefix.containsKey(prefix))
        return countryList.get(countryListByPrefix.get(prefix));
      else
        throw new CountryNotFoundException();   //  TODO Exception with the Prefix Parameter
    } else {
      Log.e("CountryTools", "CountryTools not Initialized!");
      throw new CountryToolsNotInitializedException();
    }
  }

  public static int getPrefixByCode(final String code) throws CountryNotFoundException, CountryToolsNotInitializedException {
    return getCountryByCode(code).countryPrefix;
  }

  public static String getCodeByPrefix(final int prefix) throws CountryNotFoundException, CountryToolsNotInitializedException {
    return getCountryByPrefix(prefix).countryCode;
  }

  public static int getImageByPrefix(final int prefix) throws CountryNotFoundException, CountryToolsNotInitializedException {
    return getCountryByPrefix(prefix).imageResource;
  }

  public static int getImageByCode(String code) throws CountryNotFoundException, CountryToolsNotInitializedException {
    return getCountryByCode(code).imageResource;
  }

  public static int getDataIdxByCode(String code) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (countryListByCode != null) {
      if (countryListByCode.containsKey(code))
        return countryListByCode.get(code);
      else
        throw new CountryNotFoundException();
    } else
      throw new CountryToolsNotInitializedException();
  }

  public static int getDataIdxByPrefix(int prefix) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (countryListByPrefix != null) {
      if (countryListByPrefix.containsKey(prefix))
        return countryListByPrefix.get(prefix);
      else
        throw new CountryNotFoundException();
    } else
      throw new CountryToolsNotInitializedException();
  }

  public static PhoneCountryData getItem(int position) throws CountryNotFoundException, CountryToolsNotInitializedException {
    if (countryListByPrefix != null) {
      if (countryList.size() > position)
        return countryList.get(position);
      else
        throw new CountryNotFoundException();
    } else
      throw new CountryToolsNotInitializedException();
  }

  public static List<PhoneCountryData> getData() {
    return countryList;
  }

  public static PhoneCountryDataAdapter getAdapter() {
    return adapter;
  }

}
