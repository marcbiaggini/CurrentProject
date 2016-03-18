package com.rockspoon.rockandui.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.rockspoon.rockandui.Components.FlowLayout;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.Tools;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yury Minich on 1/21/16.
 */
public class SplitBillItemDialog extends DialogFragment {

  private static final String SPLIT_BY_ARG = "split_by";
  private static final String ITEM_POS_ARG = "item_pos";
  private static final String SEAT_NUMB_ARG = "seat_number";

  private static final int DEFAULT_CHOICE = 2;

  private int seatsCount = 0;
  private int itemPos;
  private int seatNumber = -1;
  private SplitByListener listener;

  private List<RadioButton> allChoice;

  public static SplitBillItemDialog newInstance(int position, int seat, int seatsCount) {
    final SplitBillItemDialog dialog = new SplitBillItemDialog();
    Bundle args = new Bundle();
    args.putInt(SPLIT_BY_ARG, seatsCount);
    args.putInt(ITEM_POS_ARG, position);
    args.putInt(SEAT_NUMB_ARG, seat);
    dialog.setArguments(args);
    dialog.setCancelable(true);

    return dialog;
  }

  @Override
  public void setArguments(final Bundle args) {
    seatsCount = args.getInt(SPLIT_BY_ARG, 0);
    itemPos = args.getInt(ITEM_POS_ARG);
    seatNumber = args.getInt(SEAT_NUMB_ARG, -1);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();
    final View dialogLayout = inflater.inflate(R.layout.dialog_split_bill_item, null);

    Button cancelDialog = (Button) dialogLayout.findViewById(R.id.split_item_cancel_dialog);
    cancelDialog.setOnClickListener(cancelDialogListener);
    Button doneButton = (Button) dialogLayout.findViewById(R.id.split_item_done);
    doneButton.setOnClickListener(doneButtonListener);
    FlowLayout flowLayout = (FlowLayout) dialogLayout.findViewById(R.id.split_by_flow);
    allChoice = new LinkedList<>();

    for (int i = 0; i < seatsCount - 1; i++) {
      LinearLayout buttonContainer = (LinearLayout) inflater.inflate(R.layout.round_radio_button, null);
      RadioButton view = (RadioButton) buttonContainer.findViewById(R.id.radio_button);
      view.setId(i);

      int viewSize = Tools.dpToPx(getActivity(), 90);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(viewSize, viewSize);
      buttonContainer.setLayoutParams(lp);

      if (i == 0) {
        view.setChecked(true);
      }

      String label = String.valueOf(i + 2);
      view.setText(label);
      view.setOnClickListener(checkRadioButtonListener);

      flowLayout.addView(buttonContainer);
      allChoice.add(view);
    }
    builder.setView(dialogLayout);

    return builder.create();

  }


  View.OnClickListener cancelDialogListener = v -> {
    Dialog dialog = SplitBillItemDialog.this.getDialog();
    if (dialog != null) {
      dialog.dismiss();
    }
  };

  View.OnClickListener doneButtonListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Dialog dialog = SplitBillItemDialog.this.getDialog();
      if (listener != null) {
        int count = DEFAULT_CHOICE;
        for (RadioButton rb : allChoice) {
          if (rb.isChecked()) {
            count = Integer.parseInt(rb.getText().toString());
            break;
          }
        }
        listener.onSplitBy(itemPos, seatNumber, count);
      }
      if (dialog != null) {
        dialog.dismiss();
      }
    }
  };

  View.OnClickListener checkRadioButtonListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      for (RadioButton rb : allChoice) {
        rb.setChecked(false);
      }
      RadioButton view = (RadioButton) v;
      view.setChecked(true);
    }
  };

  public void setListener(SplitByListener listener) {
    this.listener = listener;
  }

  public interface SplitByListener {
    void onSplitBy(int pos, int seatNumber, int count);
  }


}
