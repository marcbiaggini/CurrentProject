package com.rockspoon.rockandui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 30/06/15.
 */

public class Tools {
  public static boolean belowLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
  }

  public static boolean belowRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
  }

  public static boolean aboveLeft(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
  }

  public static boolean aboveRight(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
  }

  public static boolean above(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y < mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
  }

  public static boolean below(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y > mobileColumnRowPair.y && targetColumnRowPair.x == mobileColumnRowPair.x;
  }

  public static boolean right(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x > mobileColumnRowPair.x;
  }

  public static boolean left(Point targetColumnRowPair, Point mobileColumnRowPair) {
    return targetColumnRowPair.y == mobileColumnRowPair.y && targetColumnRowPair.x < mobileColumnRowPair.x;
  }

  public static Bitmap blur(final View v) {
    return blur(v.getContext(), getScreenshot(v));
  }

  public static float getViewX(View view) {
    return Math.abs((view.getRight() - view.getLeft()) / 2);
  }

  public static float getViewY(View view) {
    return Math.abs((view.getBottom() - view.getTop()) / 2);
  }

  public static Bitmap blur(final Context ctx, final Bitmap image) {
    int width = (int) Math.round(image.getWidth() * 0.5);
    int height = (int) Math.round(image.getHeight() * 0.5);

    final Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
    final Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
    final RenderScript rs = RenderScript.create(ctx);
    final ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
    final Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
    final Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

    theIntrinsic.setRadius(25);
    theIntrinsic.setInput(tmpIn);
    theIntrinsic.forEach(tmpOut);
    tmpOut.copyTo(outputBitmap);

    final Canvas c = new Canvas(outputBitmap);
    c.drawARGB(0x65, 0x00, 0x00, 0x00);         //  Gray Tint

    return outputBitmap;
  }

  public static Bitmap getScreenshot(final View v) {
    final Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
    final Canvas c = new Canvas(b);
    v.draw(c);
    return b;
  }

  public static int getResourceByName(final Context ctx, final String type, final String name) {
    String packageName = ctx.getPackageName();
    return ctx.getResources().getIdentifier(name, type, packageName);
  }

  public static Bitmap getRoundCroppedBitmap(final Bitmap bmp, int radius) {
    final Bitmap sbmp = (bmp.getWidth() != radius || bmp.getHeight() != radius) ? Bitmap.createScaledBitmap(bmp, radius, radius, false) : bmp;
    final Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(output);

    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(Color.parseColor("#BAB399"));
    canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(sbmp, rect, rect, paint);

    return output;
  }

  public static boolean isLollipop() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
  }

  public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
    final double ASPECT_TOLERANCE = 0.1;
    double targetRatio = (double) h / w;

    if (sizes == null)
      return null;

    Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    int targetHeight = h;

    for (Camera.Size size : sizes) {
      double ratio = (double) size.height / size.width;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
        continue;

      if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }

    if (optimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (Camera.Size size : sizes) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize = size;
          minDiff = Math.abs(size.height - targetHeight);
        }
      }
    }

    return optimalSize;
  }

  /**
   * Converts time (in seconds) to a string like "45 min"
   *
   * @param time as seconds
   * @param showSeconds whether seconds should be displayed
   * @return time string
   */
  public static String timeToString(int time, boolean showSeconds) {
    final StringBuilder sb = new StringBuilder();
    int minutes = time / 60;
    int hours = minutes / 60;
    int seconds = time - (minutes * 60);
    minutes = minutes - (hours * 60);

    if (hours > 0) {
      sb.append(Integer.toString(hours));
      sb.append("h ");
    }

    if (minutes > 0) {
      sb.append(Integer.toString(minutes));
      sb.append("min ");
    }

    if (seconds > 0 && showSeconds) {
      sb.append(Integer.toString(seconds));
      sb.append("s");
    }

    return sb.toString().trim();
  }

  /**
   * Converts time (in seconds) to a string like "45 min"
   *
   * @param time as seconds
   * @return time string
   */
  public static String timeToString(int time) {
    return timeToString(time, true);
  }

  /**
   * Converts time (in milliseconds) to a string like "45 min"
   *
   * @param time as milliseconds
   * @return time string
   */
  public static String timeMillisToString(long time) {
    return timeToString((int) (time / 1000L), false);
  }

  /**
   * Checks for Password Length.
   * WPA/WPA2 must have a password between 8 and 63 characters.
   * WEP must have 5 <b>OR</b> 13 Characters (its fixed length passwords)
   *
   * @param password     the Wireless Password
   * @param securityType the Security Type (WEP, WPA, WPA2)
   * @return true if the password have the correct length
   */
  public static boolean checkWirelessPasswordLength(String password, String securityType) {
    if (securityType.contains("WPA"))
      return password.length() >= 8 & password.length() <= 63;

    if (securityType.contains("WEP"))
      return password.length() == 5 | password.length() == 13;

    return true;
  }

  /**
   * Executes a command as SuperUser
   *
   * @param cmd The Command
   * @return stdout from Command
   * @throws IOException
   */
  public static String executeCMD_SU(final String cmd) throws IOException {
    final StringBuilder data = new StringBuilder();

    try {
      final Process su = Runtime.getRuntime().exec("su");
      try (final DataOutputStream os = new DataOutputStream(su.getOutputStream());
           final BufferedReader is = new BufferedReader(new InputStreamReader(su.getInputStream()));
      ) {
        os.writeBytes(new StringBuilder().append(cmd).append("\n").toString());
        os.flush();
        os.writeBytes("exit\n");
        os.flush();

        String line;
        while ((line = is.readLine()) != null)
          data.append(line).append("\n");
        su.waitFor();
      }
    } catch (final InterruptedException e) {
    }
    return data.toString();
  }

  /**
   * Reboots the device
   */
  public static void reboot() {
    try {
      executeCMD_SU("reboot");
    } catch (final IOException e) {
      // Device is not rooted. Shouldn't happen.
      Log.wtf("REBOOT", "ERROR: DEVICE NOT ROOTED! IT SHOULD HAPPEN!");
    }
  }

  /**
   * Formats price to string value.
   *
   * @param context Context value
   * @param value   Price value
   * @return Formatted string
   */
  public static String priceToString(final Context context, double value) {
    return String.format(context.getResources().getString(R.string.format_price_only), value);
  }

  public static String priceToString(final Context context, BigDecimal value) {
    return String.format(context.getResources().getString(R.string.format_price_only), value);
  }

  /**
   * Converts dp to pixels
   */
  public static int dpToPx(Context ctx, int dp) {
    DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    return px;
  }

  /**
   * Get Rotation Angle
   *
   * @param activity Activity object
   * @param cameraId Camera id
   * @return angel to rotate
   */
  public static int getCameraRotationAngle(Activity activity, int cameraId) {
    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
    int degrees = 0;

    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    int result;

    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360; // compensate the mirror
    } else {
      // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }

    return result;
  }

  // "brute force" way that doesn't require changes to the layout xml or Activities
  public static void replaceApplicationFont(String staticTypefaceFieldName,
                                            final Typeface newTypeface) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Map<String, Typeface> newMap = new HashMap<String, Typeface>();
      newMap.put("sans-serif", newTypeface);
      try {
        final Field staticField = Typeface.class
            .getDeclaredField("sSystemFontMap");
        staticField.setAccessible(true);
        staticField.set(null, newMap);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    } else {
      try {
        final Field staticField = Typeface.class
            .getDeclaredField(staticTypefaceFieldName);
        staticField.setAccessible(true);
        staticField.set(null, newTypeface);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Hide keyboard on touch outside EditText
   */
  public static void setupHideKeyboardListeners(View view, Activity activity) {
    //Set up touch listener for non-text box views to hide keyboard.
    if (!(view instanceof EditText)) {
      view.setOnTouchListener((v, event) -> {
        hideSoftKeyboard(activity);
        return false;
      });
    }
    //If a layout container, iterate over children and seed recursion.
    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View innerView = ((ViewGroup) view).getChildAt(i);
        setupHideKeyboardListeners(innerView, activity);
      }
    }
  }

  public static void hideSoftKeyboard(Activity activity) {
    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
  }

  public static void hideNavigationBar(Activity activity) {
    if (Build.VERSION.SDK_INT < 19) {//19 or above api
      View v = activity.getWindow().getDecorView();
      v.setSystemUiVisibility(View.GONE);
    } else {
      View decorView = activity.getWindow().getDecorView();
      decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_FULLSCREEN
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
  }

  public static final int REQUEST_COARSE_LOCATION = 1;

  private static String[] PERMISSIONS_WIFI = { Manifest.permission.ACCESS_COARSE_LOCATION };

  /**
   * Checks if the app has permission to access location (needed by WiFi Scanner)
   * If the app does not has permission then the user will be prompted to grant permissions
   *
   * @param activity
   */
  public static boolean verifyLocationPermissions(Activity activity) {
    // Check if we have write permission
    int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      // We don't have permission so prompt the user
      ActivityCompat.requestPermissions(
          activity,
          PERMISSIONS_WIFI,
          REQUEST_COARSE_LOCATION
      );
      return false;
    } else {
      return true;
    }
  }
}
