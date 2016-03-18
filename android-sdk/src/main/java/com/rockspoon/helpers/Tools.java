package com.rockspoon.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import java.sql.Timestamp;
import java.util.List;

import timber.log.Timber;

/**
 * Created by lucas on 10/03/16.
 */
public class Tools {

  /**
   * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
   *
   * @return long ms
   */
  public static long getTimeStamp() {
    return System.currentTimeMillis();
  }

  /**
   * Returns if the ScanResult is encrypted or public
   *
   * @param result Wireless ScanResult
   *
   * @return boolean True for Encrypted, False for Public
   */
  public static boolean isWirelessSecured(final ScanResult result) {
    return result.capabilities.contains("WEP") | result.capabilities.contains("PSK") | result.capabilities.contains("EAP");
  }

  /**
   * Gets the dBm input and return a value from 0 to 4 (Signal bars)<BR>
   * Its based on percent: <B>((110.0 + dBm) / (110.0 - 60.0)) * 100.0</B><BR>
   * Then the steps are 20%
   *
   * @param dbm
   *
   * @return
   */
  public static int getSignalStrengthBar4(final int dbm) {
    return getSignalPercent(dbm) / 20;
  }

  public static int getSignalPercent(final int dbm) {
    return (int) ((((110.0 + dbm) / (110.0 - 60.0)) * 100.0) > 100 ? 100 : (((110.0 + dbm) / (110.0 - 60.0)) * 100.0));
  }

  /**
   * Tests for the signal quality<BR>
   * Just for dBm reference:
   * <ul>
   * <li>-105 to -100 = Bad/drop call</li>
   * <li>-99 to -90 = Getting bad/signal may break up</li>
   * <li>-89 to -80 = OK/shouldn't have problems, but maybe</li>
   * <li>-79 to -65 = Good</li>
   * <li>Over -65 = Excellent</li>
   * </ul>
   * So I'm considering a -110 to -60 range as 0% to 100%. So:<BR>
   * <ul>
   * <li>0% to 10% = Bad/drop call</li>
   * <li>10% to 40% = Getting bad/signal may break up</li>
   * <li>40% to 60% = OK/shouldn't have problems, but maybe</li>
   * <li>60% to 90% = Good</li>
   * <li>Over 90% = Excellent</li>
   * </ul>
   */
  public static boolean testSignalQuality(final Context ctx, final int minSignal) {

    final TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

    if (telephonyManager == null) {
      Timber.e("Tools::TestSignalQuality - Fail to get TelephonyManager Interface");
      return false;
    }
    final List<CellInfo> list = telephonyManager.getAllCellInfo();
    if (list == null) {
      Timber.e("Tools::TestSignalQuality - Fail to get cell info");
      return false;
    }

    double signal = -9999;

    if (list.get(0) instanceof CellInfoGsm) {
      final CellInfoGsm cellinfogsm = (CellInfoGsm) list.get(0);
      final CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
      signal = ((110.0 + cellSignalStrengthGsm.getDbm()) / (110.0 - 60.0)) * 100.0;
    } else if (list.get(0) instanceof CellInfoWcdma) {
      final CellInfoWcdma cellinfogsm = (CellInfoWcdma) list.get(0);
      final CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
      signal = ((110.0 + cellSignalStrengthGsm.getDbm()) / (110.0 - 60.0)) * 100.0;
    }

    return signal > minSignal;
  }

  /**
   * Gets the connected wireless network name.
   *
   * @return String SSID or null if not connected.
   */
  public static String getConnectedWiFiNetwork(final Context ctx) {
    String ssid = null;
    final ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if (networkInfo.isConnectedOrConnecting()) {
      final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
      final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
      if (connectionInfo != null && !(connectionInfo.getSSID().equals(""))) {
        ssid = connectionInfo.getSSID();
      }
    }
    return ssid;
  }

  /**
   * Corrects the HEX Key for WEP/WPA
   *
   * @param s hex key string
   *
   * @return corrected string
   */
  public static boolean getHexKey(final String s) {
    if (s == null) {
      return false;
    }

    final int len = s.length();
    if (len != 10 && len != 26 && len != 58) {
      return false;
    }

    for (int i = 0; i < len; ++i) {
      char c = s.charAt(i);
      if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
        continue;
      }
      return false;
    }
    return true;
  }

}
