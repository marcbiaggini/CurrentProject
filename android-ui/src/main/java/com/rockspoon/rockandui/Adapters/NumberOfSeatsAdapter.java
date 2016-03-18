package com.rockspoon.rockandui.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by Eugen K. on 2/9/16.
 */
public class NumberOfSeatsAdapter extends RecyclerView.Adapter<NumberOfSeatsAdapter.ViewHolder> {

  private List<String> list;
  private View.OnClickListener listener;

  public NumberOfSeatsAdapter(List<String> list, View.OnClickListener listener) {
    this.list = list;
    this.listener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.number_of_seats_item, parent, false);
    view.setOnClickListener(listener);
    ViewHolder holder = new ViewHolder(view);
    return holder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    holder.number.setText(list.get(position));
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView number;

    public ViewHolder(View itemView) {
      super(itemView);
      number = (TextView) itemView.findViewById(R.id.number_of_seat_label);
    }
  }

}
