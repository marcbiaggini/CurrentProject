package com.rockspoon.rockandui.Components;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by raphael.pinheiro on 18/12/2015.
 */
public class DualEditText {

  private TextView view;

  private EditText edit;

  public DualEditText(final TextView view, final EditText edit) {
    this.view = view;
    this.edit = edit;
    makeItReadOnly();
  }

  public void setText(final String text) {
    this.view.setText(text);
    this.edit.setText(text);
  }

  public void makeItEditable() {
    this.edit.setVisibility(View.VISIBLE);
    this.view.setVisibility(View.GONE);
  }

  public void makeItReadOnly() {
    this.view.setVisibility(View.VISIBLE);
    this.edit.setVisibility(View.GONE);
  }

  public void cancelEdition() {
    this.edit.setText(this.view.getText());
  }

  public void commitEdition() {
    this.view.setText(this.edit.getText());
  }
}
