package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * SimpleWaitingDialog.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/18/16.
 */
public class SimpleWaitingDialog extends AbstractTitleMessageDialog<SimpleWaitingDialog> {

    private ProgressBar progressBar;

    public SimpleWaitingDialog() {
        setStyle(STYLE_NO_FRAME, R.style.rsDialogFadeInOut);
        setCancelable(false);
        setGravity(Gravity.CENTER);
    }

    /**
     * Create a SimpleWaitingDialog using bundle arguments.<BR/>
     * <BR/>
     * Bundle Arguments:<BR/>
     * title - Dialog Title String - This will set the title of dialog. Default: Please Wait
     *
     * @param args Bundle Arguments
     * @return New SimpleWaitingDialog
     */
    public static SimpleWaitingDialog newInstance(final Bundle args) {
        final SimpleWaitingDialog dialog = new SimpleWaitingDialog();
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Create a SimpleWaitingDialog using bundle arguments.<BR/>
     * @return New SimpleWaitingDialog
     */
    public static SimpleWaitingDialog newInstance() {
        return new SimpleWaitingDialog();
    }

    @Override
    public void setArguments(Bundle args) {
        if (args != null) {
            setTitle(args.getString(PARAM_TITLE));
        }
    }

    @Override
    public SimpleWaitingDialog getThis() {
        return this;
    }

    @Override
    public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_simple_waiting, container, false);
        titleView = (TextView) v.findViewById(R.id.dialog_simple_waiting_title);
        progressBar = (ProgressBar) v.findViewById(R.id.dialog_simple_waiting_progressbar);

        setTitle(title);

        return v;
    }
}
