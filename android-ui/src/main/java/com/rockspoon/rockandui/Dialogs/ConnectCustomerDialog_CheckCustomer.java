package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 06/08/15.
 */
public class ConnectCustomerDialog_CheckCustomer extends RSDialog {

  private Bundle dataCache;

  public static ConnectCustomerDialog_CheckCustomer newInstance(final Bundle args) {
    final ConnectCustomerDialog_CheckCustomer dialog = new ConnectCustomerDialog_CheckCustomer();
    dialog.setArguments(args);
    dialog.setCancelable(false);
    return dialog;
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_connect_customer_progress, container, false);
    //TODO: All the processes
    new Handler().postDelayed(() -> {
        final ConnectCustomerDialog_NotFound dialog = ConnectCustomerDialog_NotFound.newInstance(dataCache);
        ConnectCustomerDialog_CheckCustomer.this.dismiss();
        dialog.show(getFragmentManager(), "dialog2");
    }, 3000);
    return v;
  }

  @Override
  public void setArguments(final Bundle args) {
    dataCache = args;
  }
}
