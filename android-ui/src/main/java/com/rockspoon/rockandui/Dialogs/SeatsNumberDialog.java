package com.rockspoon.rockandui.Dialogs;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rockspoon.rockandui.Adapters.NumberOfSeatsAdapter;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugen K. on 2/9/16.
 */
public class SeatsNumberDialog extends RSDialog implements View.OnClickListener {

  public interface SeatsNumberDialogListener {
    void onNumberClick(int number);
  }

  private static final String TAG_SEATS_NUMBER_DIALOG = "SeatsNumberDialog";
  private static final String EXTRA_MAX_SEATS_NUMBER = "MaxSeatsNumber";
  private static final String EXTRA_IS_SEATED = "IsSeated";

  private ViewHolder holder;
  private SeatsNumberDialogListener listener;
  private int maxSeats;

  public static SeatsNumberDialog newInstance(int maxSeats) {
    SeatsNumberDialog dialog = new SeatsNumberDialog();

    Bundle args = new Bundle();
    args.putInt(EXTRA_MAX_SEATS_NUMBER, maxSeats);
    dialog.setArguments(args);

    return dialog;
  }

  public static SeatsNumberDialog newInstanceSeated(int maxSeats) {
    SeatsNumberDialog dialog = new SeatsNumberDialog();

    Bundle args = new Bundle();
    args.putInt(EXTRA_MAX_SEATS_NUMBER, maxSeats);
    args.putBoolean(EXTRA_IS_SEATED, true);
    dialog.setArguments(args);

    return dialog;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    maxSeats = getArguments().getInt(EXTRA_MAX_SEATS_NUMBER);
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_seats_number, container, false);
    holder = new ViewHolder(view);
    initUi();
    return view;
  }

  @Override
  public void onClick(View v) {
    if (listener != null) {
      final int itemPosition = holder.numberOfSeatsView.getChildAdapterPosition(v) + 1;
      listener.onNumberClick(itemPosition);
    }
    getDialog().dismiss();
  }

  public void show(FragmentManager fragmentManager) {
    super.show(fragmentManager, TAG_SEATS_NUMBER_DIALOG);
  }

  private void initUi() {
    getDialog().setTitle(getString(R.string.table_num_of_seats));
    List<String> list = new ArrayList<>();
    for (int i = 1; i <= maxSeats; i++) {
      list.add("" + i);
    }
    NumberOfSeatsAdapter adapter = new NumberOfSeatsAdapter(list, this);
    holder.numberOfSeatsView.setAdapter(adapter);
    holder.numberOfSeatsView.setLayoutManager(new LinearLayoutManager(getActivity()));
    holder.closeDialogView.setOnClickListener(v -> {
      getDialog().dismiss();
    });
  }

  private class ViewHolder {
    RecyclerView numberOfSeatsView;
    Button closeDialogView;

    ViewHolder(View view) {
      closeDialogView = (Button) view.findViewById(R.id.close_dialog_button);
      numberOfSeatsView = (RecyclerView) view.findViewById(R.id.number_of_seats);
    }
  }

  public void setOnNumberOfSeatsClick(SeatsNumberDialogListener listener) {
    this.listener = listener;
  }

}
