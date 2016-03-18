package com.rockspoon.rockandui.Dialogs;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by Lucas Teske on 31/08/15.
 */
public class GenericMessageDialog extends AbstractTitleMessageDialog<GenericMessageDialog> {

  // Bundle Argument Parameters
  public static String PARAM_DUAL_BUTTON_MODE = "dualButtonMode";
  public static String PARAM_OK_BUTTON_TEXT = "okButtonText";
  public static String PARAM_CANCEL_BUTTON_TEXT = "cancelButtonText";
  public static String PARAM_CANCELABLE = "cancelable";

  private String okBtnText = null;
  private String cancelBtnText = null;
  private boolean dualButtonMode = false;

  private Button single_okBtn;
  private Button dual_okBtn;
  private Button dual_cancelBtn;
  private LinearLayout dualBtnHolder;

  private View.OnClickListener okButtonClick;
  private View.OnClickListener cancelButtonClick;

  public GenericMessageDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitDown);
  }

  /**
   * Create a GenericMessageDialog using bundle arguments.<BR/>
   * <BR/>
   * Bundle Arguments:<BR/>
   * <B>PARAM_TITLE</B> - Dialog Title String - This will set the title of dialog. Default: Please Wait
   * <B>PARAM_MESSAGE</B> - Dialog Content Message String. Default: Lorem Ipsum
   * <B>PARAM_OK_BUTTON_TEXT</B> - Dialog Button OK Text. Default: OK
   * <B>PARAM_CANCEL_BUTTON_TEXT</B> - Dialog Button CANCEL Text. Default: Cancel
   * <B>PARAM_DUAL_BUTTON_MODE</B> - Boolean DualButton Mode (Cancel + OK) Default: false
   * <B>PARAM_CANCELABLE</B> - Dialog Cancelable -- This will set if this dialog is cancelable. Default: False
   *
   * @param args Bundle Arguments
   * @return New GenericMessageDialog
   */
  public static GenericMessageDialog newInstance(final Bundle args) {
    final GenericMessageDialog dialog = new GenericMessageDialog();
    dialog.setArguments(args);
    return dialog;
  }

  /**
   * Create a GenericMessageDialog using bundle arguments.<BR/>
   * @return New GenericMessageDialog
   */
  public static GenericMessageDialog newInstance() {
    return new GenericMessageDialog();
  }

  /**
   * Shows an dialog
   *
   * @param title   The dialog title
   * @param message The dialog Message
   */
  public static GenericMessageDialog showDialog(FragmentManager manager, String title, String message) {
    final GenericMessageDialog dialog = (new GenericMessageDialog())
        .setMessage(message)
        .setTitle(title);
    dialog.show(manager, "errorDialog-" + title + "-" + message);

    return dialog;
  }

  @Override
  public void setArguments(Bundle args) {
    if (args != null) {
      setTitle(args.getString(PARAM_TITLE));
      setMessage(args.getString(PARAM_MESSAGE));
      setOKButtonText(args.getString(PARAM_OK_BUTTON_TEXT));
      setCancelButtonText(args.getString(PARAM_CANCEL_BUTTON_TEXT));
      setCancelable(args.getBoolean(PARAM_CANCELABLE, false));
      setDualButtonMode(args.getBoolean(PARAM_DUAL_BUTTON_MODE, false));
    }
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_generic_message, container, false);

    titleView = (TextView) v.findViewById(R.id.dialog_generic_message_title);
    messageView = (TextView) v.findViewById(R.id.dialog_generic_message_message);

    dualBtnHolder = (LinearLayout) v.findViewById(R.id.dialog_generic_message_dualbtnholder);
    single_okBtn = (Button) v.findViewById(R.id.dialog_generic_message_single_okbtn);
    dual_okBtn = (Button) v.findViewById(R.id.dialog_generic_message_dual_okbtn);
    dual_cancelBtn = (Button) v.findViewById(R.id.dialog_generic_message_dual_cancelbtn);

    setOKButtonText(okBtnText);
    setCancelButtonText(cancelBtnText);
    setDualButtonMode(dualButtonMode);
    setTitle(title);
    setMessage(message);

    final View.OnClickListener okClick = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (okButtonClick != null)
          okButtonClick.onClick(view);
        else
          dismiss();
      }
    };

    single_okBtn.setOnClickListener(okClick);
    dual_okBtn.setOnClickListener(okClick);

    dual_cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (cancelButtonClick != null)
          cancelButtonClick.onClick(view);
        else
          dismiss();
      }
    });

    return v;
  }

  /**
   * Sets the Click Listener for OK Button
   *
   * @param okBtnClick the listener
   * @return this dialog
   */
  public GenericMessageDialog setOnOKButtonClick(View.OnClickListener okBtnClick) {
    okButtonClick = okBtnClick;
    return this;
  }

  /**
   * Sets the Click Listener for OK Button
   *
   * @param cancelBtnClick the listener
   * @return this dialog
   */
  public GenericMessageDialog setOnCancelButtonClick(View.OnClickListener cancelBtnClick) {
    cancelButtonClick = cancelBtnClick;
    return this;
  }

  /**
   * Enable / Disable dual button mode
   *
   * @param dualButton if Dual Button Mode is Enabled
   * @return this dialog
   */
  public GenericMessageDialog setDualButtonMode(boolean dualButton) {
    if (dualBtnHolder != null)
      dualBtnHolder.setVisibility(dualButton ? View.VISIBLE : View.GONE);
    if (single_okBtn != null)
      single_okBtn.setVisibility(dualButton ? View.GONE : View.VISIBLE);

    dualButtonMode = dualButton;
    return this;
  }

  /**
   * Sets Cancel Button Text
   *
   * @param cancelBtnText String Text
   * @return this dialog
   */
  public GenericMessageDialog setCancelButtonText(String cancelBtnText) {
    if(cancelBtnText != null) {
      this.cancelBtnText = cancelBtnText;
      if (dual_cancelBtn != null)
        dual_cancelBtn.setText(this.cancelBtnText);
    }
    return this;
  }

  /**
   * Sets OK Button Text
   *
   * @param okBtnText String Text
   * @return this dialog
   */
  public GenericMessageDialog setOKButtonText(String okBtnText) {
    if(okBtnText != null) {
      this.okBtnText = okBtnText;

      if (single_okBtn != null)
        single_okBtn.setText(this.okBtnText);

      if (dual_okBtn != null)
        dual_okBtn.setText(this.okBtnText);
    }
    return this;
  }

  @Override
  public GenericMessageDialog getThis() {
    return this;
  }
}
