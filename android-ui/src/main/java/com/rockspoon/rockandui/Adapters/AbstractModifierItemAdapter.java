package com.rockspoon.rockandui.Adapters;

import android.widget.BaseAdapter;

import com.rockspoon.rockandui.Interfaces.OnModifierItemSelected;

/**
 * Created by lucas on 13/08/15.
 */
public abstract class AbstractModifierItemAdapter extends BaseAdapter {

  protected OnModifierItemSelected onModifierItemSelected;

  public void setOnModifierItemSelected(OnModifierItemSelected listener) {
    this.onModifierItemSelected = listener;
  }
}
