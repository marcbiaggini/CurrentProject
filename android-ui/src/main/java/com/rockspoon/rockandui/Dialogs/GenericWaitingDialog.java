package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by Lucas Teske on 31/08/15.
 */
public class GenericWaitingDialog extends AbstractTitleMessageDialog<GenericWaitingDialog> {

  public static String PARAM_PROGRESS_INDETERMINATE = "progressIndeterminate";
  public static String PARAM_CANCELABLE = "cancelable";

  private boolean progressIndeterminate = true;
  private int progressValue = 0;

  private ProgressBar progressBar;

  public GenericWaitingDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitDown);
    setCancelable(false);
  }

  /**
   * Create a GenericWaitingDialog using bundle arguments.<BR/>
   * <BR/>
   * Bundle Arguments:<BR/>
   * title - Dialog Title String - This will set the title of dialog. Default: Please Wait
   * message - Dialog Content Message String. Default: Nothing
   * progressIndeterminate - Dialog Progress Indeterminate Boolean. -- This will set Progress Bar as Determinate. Default: False
   * cancelable - Dialog Cancelable -- This will set if this dialog is cancelable. Default: False
   *
   * @param args Bundle Arguments
   * @return New GenericWaitingDialog
   */
  public static GenericWaitingDialog newInstance(final Bundle args) {
    final GenericWaitingDialog dialog = new GenericWaitingDialog();
    dialog.setArguments(args);
    return dialog;
  }

  /**
   * Create a GenericWaitingDialog using bundle arguments.<BR/>
   * @return New GenericWaitingDialog
   */
  public static GenericWaitingDialog newInstance() {
    return new GenericWaitingDialog();
  }

  @Override
  public void setArguments(Bundle args) {
    if (args != null) {
      setTitle(args.getString(PARAM_TITLE));
      setMessage(args.getString(PARAM_MESSAGE));
      setProgress(args.getBoolean(PARAM_PROGRESS_INDETERMINATE, true) ? -1 : 0);
      setCancelable(args.getBoolean(PARAM_CANCELABLE, false));
    }
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_generic_waiting, container, false);
    titleView = (TextView) v.findViewById(R.id.dialog_generic_waiting_title);
    messageView = (TextView) v.findViewById(R.id.dialog_generic_waiting_message);
    progressBar = (ProgressBar) v.findViewById(R.id.dialog_generic_waiting_progressbar);

    setProgress(progressIndeterminate ? -1 : progressValue);
    setTitle(title);
    setMessage(message);

    return v;
  }

  @Override
  public GenericWaitingDialog getThis() {
    return this;
  }

  /**
   * Sets the progress of Progress Bar in %.
   * If the Progress Bar was Indeterminate, sets to Determinate.
   * Use -1 to set as Indeterminate
   *
   * @param progress integer %
   * @return this dialog
   */
  public GenericWaitingDialog setProgress(int progress) {

    if (progressBar != null) {
      if (progress == -1)
        progressBar.setIndeterminate(true);
      else {
        if (progressBar.isIndeterminate())
          progressBar.setIndeterminate(false);
        progressBar.setProgress(progress);
      }
    }
    progressValue = progress;
    progressIndeterminate = progress == -1;

    return this;
  }
}
