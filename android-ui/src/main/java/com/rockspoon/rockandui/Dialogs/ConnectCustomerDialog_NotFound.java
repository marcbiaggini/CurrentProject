package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 07/08/15.
 */
public class ConnectCustomerDialog_NotFound extends RSDialog {

  private Bundle dataCache;

  public static ConnectCustomerDialog_NotFound newInstance(final Bundle args) {
    final ConnectCustomerDialog_NotFound dialog = new ConnectCustomerDialog_NotFound();
    dialog.setArguments(args);
    dialog.setCancelable(false);
    return dialog;
  }

  @Override
  public void setArguments(final Bundle args) {
    dataCache = args;
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_connect_customer_notfound, container, false);

    final Button sendBtn = (Button) v.findViewById(R.id.dialog_connect_customer_sendbtn);
    final Button closeBtn = (Button) v.findViewById(R.id.dialog_connect_customer_closebtn);
    final Button cancelBtn = (Button) v.findViewById(R.id.dialog_connect_customer_cancelbtn);
    final TextView phone = (TextView) v.findViewById(R.id.dialog_connect_customer_phone);

    if (dataCache != null) {
      phone.setText(dataCache.getString("phoneFormat"));
    }

    closeBtn.setOnClickListener((view) -> {
      ConnectCustomerDialog_NotFound.this.dismiss();
    });

    cancelBtn.setOnClickListener((view) -> {
        ConnectCustomerDialog_NotFound.this.dismiss();
    });

    sendBtn.setOnClickListener((view) -> {
        //TODO: Send SMS Request
        ConnectCustomerDialog_NotFound.this.dismiss();
    });

    return v;
  }
}
