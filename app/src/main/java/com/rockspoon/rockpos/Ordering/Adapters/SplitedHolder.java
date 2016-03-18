package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.rockpos.R;

/**
 * Created by greenfrvr
 */
public class SplitedHolder extends OrderViewHolder<Menu> {

  private CheckBox selectAll;
  private Button menuButton;

  public SplitedHolder(View itemView) {
    super(itemView);
    selectAll = (CheckBox) itemView.findViewById(R.id.order_select_all);
    menuButton = (Button) itemView.findViewById(R.id.order_menu_button);
  }

  public void setOnSelectAllListener(CompoundButton.OnCheckedChangeListener listener) {
    selectAll.setOnCheckedChangeListener(listener);
  }

  public void setOnMenuClickListener(View.OnClickListener listener) {
    menuButton.setOnClickListener(listener);
  }

  @Override
  void bind(Menu menu) {
    menuButton.setText(menu.getTitle());
  }
}
