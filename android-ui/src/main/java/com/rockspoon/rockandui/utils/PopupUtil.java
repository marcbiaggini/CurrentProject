package com.rockspoon.rockandui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by mac-248 on 2/2/16.
 */
public class PopupUtil {

  public static ErrorPopupWindow errorPopupWindow(Context context, String msg) {
    LayoutInflater layoutInflater
        = (LayoutInflater) context
        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    View popupView = layoutInflater.inflate(R.layout.error_message, null);
    ((TextView) popupView.findViewById(R.id.error_message_text)).setText(msg);
    final ErrorPopupWindow popupWindow = new ErrorPopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    return popupWindow;
  }

  public static Rect locateView(View v) {
    int[] loc_int = new int[2];
    if (v == null) return null;
    try {
      v.getLocationOnScreen(loc_int);
    } catch (NullPointerException npe) {
      return null;
    }
    Rect location = new Rect();
    location.left = loc_int[0];
    location.top = loc_int[1];
    location.right = location.left + v.getWidth();
    location.bottom = location.top + v.getHeight();
    return location;
  }

}
