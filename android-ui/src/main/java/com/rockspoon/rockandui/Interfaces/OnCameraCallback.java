package com.rockspoon.rockandui.Interfaces;

import android.graphics.Bitmap;

import com.rockspoon.rockandui.Components.RSCamera;

/**
 * Created by lucas on 23/07/15.
 */
public interface OnCameraCallback {
  void onPictureTaken(Bitmap image);

  void onError(RSCamera.ERROR errorCode);
}
