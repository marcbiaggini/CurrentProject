package com.rockspoon.rockpos.Camera;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by lucas on 06/01/16.
 */
public class CameraStorage {
  private static Bitmap imageBmp;
  private static String imageB64;

  public static Bitmap getImageBmp() {
    return imageBmp;
  }

  public static String getImageB64() {
    return imageB64;
  }

  public static void clear() {
    if (imageBmp != null && !imageBmp.isRecycled())
      imageBmp.recycle();
    imageBmp = null;
    imageB64 = null;
  }

  public static void setImage(final Bitmap imageBmp) {
    CameraStorage.imageBmp = imageBmp;
    updateB64();
  }

  private static void updateB64() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    imageBmp.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
    byte[] byteArray = byteArrayOutputStream.toByteArray();
    imageB64 = "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  public static void resizeStoredImage(int width, int height) {
    if (CameraStorage.imageBmp != null) {
      final Bitmap newb = Bitmap.createScaledBitmap(CameraStorage.imageBmp, width, height, false);
      imageBmp.recycle();
      imageBmp = newb;
      updateB64();
    }
  }
}
