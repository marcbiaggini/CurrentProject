package com.rockspoon.rockandui.Dialogs;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSDialog;

/**
 * Created by lucas on 23/11/15.
 */
public abstract class AbstractTitleMessageDialog<T> extends RSDialog {

  // Bundle Argument Parameters
  public static String PARAM_TITLE = "title";
  public static String PARAM_MESSAGE = "message";

  protected String title = null;
  protected String message = null;

  protected TextView titleView;
  protected TextView messageView;

  public abstract T getThis();

  /**
   * Sets dialog title
   *
   * @param title String
   * @return this dialog
   */
  public T setTitle(final String title) {
    if (title != null) {
      this.title = title;

      if (titleView != null)
        titleView.setText(title);
    }
    return getThis();
  }

  /**
   * Sets the content message on the Dialog
   *
   * @param message String
   * @return this dialog
   */
  public T setMessage(final String message) {
    if (message != null) {
      this.message = message;
      Log.d("SettingMessage", " " + message);
      if (messageView != null) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
      }
    }
    return getThis();
  }
}
