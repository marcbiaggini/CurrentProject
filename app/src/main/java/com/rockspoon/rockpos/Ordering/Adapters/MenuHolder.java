package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.Button;

import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.rockpos.R;

/**
 * Created by greenfrvr
 */
class MenuHolder extends OrderViewHolder<Menu> {

  private Button menuButton;

  public MenuHolder(View v) {
    super(v);
    menuButton = (Button) v.findViewById(R.id.order_menu_button);
  }

  @Override
  void bind(Menu menu) {
    menuButton.setText(menu.getTitle());
  }

}
