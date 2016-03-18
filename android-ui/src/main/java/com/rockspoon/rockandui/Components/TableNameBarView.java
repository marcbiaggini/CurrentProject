package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.rockspoon.rockandui.R;

/**
 * Created by Eugen K. on 2/17/16.
 */
public class TableNameBarView extends LinearLayout {

  private final LinearLayout nameContainer;
  private Context context;
  private final CheckBox checkBox;

  public TableNameBarView(Context context) {
    this(context, null, 0);
  }

  public TableNameBarView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TableNameBarView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs);
    this.context = context;
    inflate(context, R.layout.table_name_bar_view, this);
    nameContainer = (LinearLayout) findViewById(R.id.table_name_container);
    checkBox = (CheckBox) findViewById(R.id.table_select_checkbox);
    checkBox.setChecked(false);
  }

  public void setTableName(String[] names) {
    if (names.length == 0) {
      addLabel(getResources().getString(R.string.placeholder_table_name));
      return;
    }
    nameContainer.removeAllViews();
    if (names.length > 1) {
      checkBox.setBackgroundColor(getResources().getColor(R.color.bordercolor));
      for (int i = 0; i < names.length; i++) {
        addLabel(names[i], R.color.bordercolor);
        if (i != names.length - 1) {
          addMergeIcon();
        }
      }
    } else {
      addLabel(names[0]);
    }
  }

  public void setCheckBoxChecked(boolean isChecked) {
    checkBox.setChecked(isChecked);
  }

  private void addMergeIcon() {
    View view = LayoutInflater.from(context).inflate(R.layout.tables_name_merge_icon, null);
    nameContainer.addView(view);
  }

  private void addLabel(String name) {
    addLabel(name, -1);
  }

  private void addLabel(String name, int bgColorId) {
    VerticalText nameView = (VerticalText) LayoutInflater.from(context).inflate(R.layout.vertical_text_view, null);
    nameView.setText(name);
    nameView.setLayoutParams(new LinearLayout.LayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT, 1.0f));
    if (bgColorId != -1) {
      nameView.setBackgroundColor(getResources().getColor(bgColorId));
    }
    nameContainer.addView(nameView);
  }

  public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChecngeListener) {
    checkBox.setOnCheckedChangeListener(onCheckedChecngeListener);
  }

  public boolean isSelected() {
    return checkBox.isChecked() && checkBox.getVisibility() == View.VISIBLE;
  }

}
