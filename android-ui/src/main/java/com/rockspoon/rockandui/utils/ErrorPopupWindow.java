package com.rockspoon.rockandui.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by mac-248 on 2/2/16.
 */
public class ErrorPopupWindow extends PopupWindow {

  public ErrorPopupWindow(View popupView, int width, int height) {
    super(popupView, width, height);
  }

  public void showOnRightSide(View anchor) {
    super.showAsDropDown(anchor, 0, -anchor.getHeight(), Gravity.TOP | Gravity.RIGHT);
  }
}
