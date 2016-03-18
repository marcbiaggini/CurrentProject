package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

public class TextFieldDialog extends RSDialog {
    public interface TextFieldDialogDelegate {
        void reciveStringFromTextFieldDialog(String text);
    }

    public static final String ARG_DIALOG_TITLE = "ARG_DIALOG_TITLE";
    public static final String ARG_TEXT_FIELD_HINT = "ARG_TEXT_FIELD_HINT";

    private TextFieldDialogDelegate textFieldDialog;
    private View.OnClickListener onOkClickListener;
    private View.OnClickListener onCancelClickListener;

    public static TextFieldDialog newInstance(final Bundle args) {
        final TextFieldDialog dialog = new TextFieldDialog();
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_with_text_field, container, false);

        Bundle args = getArguments();

        final TextView titleTextView = (TextView) v.findViewById(R.id.dialog_title);
        final EditText editText = (EditText) v.findViewById(R.id.dialog_edit_text);
        final Button okButton = (Button) v.findViewById(R.id.dialog_ok);
        final Button cancelButton = (Button) v.findViewById(R.id.dialog_cancel);

        titleTextView.setText(args.getString(ARG_DIALOG_TITLE, ""));
        editText.setHint(args.getString(ARG_TEXT_FIELD_HINT, ""));

        okButton.setOnClickListener(view -> {
            dismiss();

            if (onOkClickListener != null) {
                onOkClickListener.onClick(view);
            }

            if (textFieldDialog != null) {
                textFieldDialog.reciveStringFromTextFieldDialog(editText.getText().toString());
            }
            editText.setText("");
        });
        cancelButton.setOnClickListener(view -> {
            dismiss();
            if (onOkClickListener != null) {
                onCancelClickListener.onClick(view);
            }
        });
        return v;
    }

    public View.OnClickListener getOnOkClickListener() {
        return onOkClickListener;
    }

    public void setOnOkClickListener(View.OnClickListener onOkClickListener) {
        this.onOkClickListener = onOkClickListener;
    }

    public View.OnClickListener getOnCancelClickListener() {
        return onCancelClickListener;
    }

    public void setOnCancelClickListener(View.OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public TextFieldDialogDelegate getTextFieldDialogDelegate() {
        return textFieldDialog;
    }

    public void setTextFieldDialogDelegate(TextFieldDialogDelegate textFieldDialog) {
        this.textFieldDialog = textFieldDialog;
    }
}
