package com.rockspoon.rockandui.Interfaces;

import android.view.View;

/**
 * Created by lucas on 13/07/15.
 */
public interface OnSelectedItemBitmapCreationListener {
  void onPreSelectedItemBitmapCreation(View selectedView, int position, long itemId);

  void onPostSelectedItemBitmapCreation(View selectedView, int position, long itemId);
}