package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rockspoon.rockandui.Components.InternationalPhoneEdit;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 06/08/15.
 */
public class ConnectCustomerDialog extends RSDialog {

  public static final String ARGS_SEAT = "seat_num";

  private int seatNum;

  public ConnectCustomerDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitUp);
  }

  public static ConnectCustomerDialog newInstance(int seat) {

    Bundle args = new Bundle();
    args.putInt(ARGS_SEAT, seat);

    ConnectCustomerDialog dialog = new ConnectCustomerDialog();
    dialog.setArguments(args);
    dialog.setCancelable(false);
    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    seatNum = getArguments().getInt(ARGS_SEAT);
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View v = inflater.inflate(R.layout.dialog_connect_customer, container, false);

    final Button doneBtn = (Button) v.findViewById(R.id.dialog_connect_customer_donebtn);
    final Button closeBtn = (Button) v.findViewById(R.id.dialog_connect_customer_closebtn);
    final TextView seatNumber = (TextView) v.findViewById(R.id.dialog_connect_customer_seatn);
    final InternationalPhoneEdit phone = (InternationalPhoneEdit) v.findViewById(R.id.dialog_connect_customer_phone);

    seatNumber.setText(getString(R.string.format_seat_n, seatNum + 1));

    closeBtn.setOnClickListener((view) -> dismiss());

    doneBtn.setOnClickListener((view) -> {
        if (phone.isEmpty()) {
          Toast.makeText(getActivity(), getString(R.string.message_please_fill_all_fields), Toast.LENGTH_LONG).show();
          shake();
        } else {
          final Bundle args = new Bundle();
          args.putString("phone", phone.getPhoneString());
          args.putString("phoneFormat", phone.getFormattedPhoneString());
          final ConnectCustomerDialog_CheckCustomer dialog = ConnectCustomerDialog_CheckCustomer.newInstance(args);
          ConnectCustomerDialog.this.dismiss();
          dialog.show(getFragmentManager(), "dialog2");
          //TODO
        }
    });

    return v;
  }
}
