package com.rockspoon.rockpos.Planing.Adapters.Holders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanHeaderAdapter;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.HeaderHolderWrapper;
import com.rockspoon.rockpos.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
  private EditText nameTextView;
  private TextView isDraftTextView;
  private LinearLayout headerContainer;
  private ImageButton mCloseButton;
  private HeaderHolderWrapper currentItem;
  private InputMethodManager imm;
  FloorPlanHeaderAdapter.FloorPlansTabsListener delegate;
  FloorPlanHeaderAdapter adapter;

  public HeaderViewHolder(View itemView) {
    super(itemView);

    nameTextView = (EditText) itemView.findViewById(R.id.floor_plan_name);
    mCloseButton = (ImageButton) itemView.findViewById(R.id.close_button);
    isDraftTextView = (TextView) itemView.findViewById(R.id.is_draft_label);
    headerContainer = (LinearLayout) itemView.findViewById(R.id.header_floorplan_container);
    mCloseButton.setOnClickListener(this);
    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
    nameTextView.setOnLongClickListener(this);
    nameTextView.setOnClickListener(this);


    imm = (InputMethodManager) itemView.getContext().getSystemService(
        Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(nameTextView, InputMethodManager.SHOW_IMPLICIT);
  }

  public void bindData(HolderWrapper currentSelectedItem, HeaderHolderWrapper item, FloorPlanHeaderAdapter.FloorPlansTabsListener delegate, FloorPlanHeaderAdapter adapter) {
    Context context = nameTextView.getContext();
    currentItem = item;
    nameTextView.setText(item.getVenuePlan().getName());
    isDraftTextView.setText(item.getVenuePlan().getIsDraft() ? context.getString(R.string.draft) : context.getString(R.string.published));
    nameTextView.setFocusableInTouchMode(false);
    if (currentSelectedItem != item) {
      nameTextView.setTextColor(ContextCompat.getColor(context, R.color.textcolor_lightblue));
      isDraftTextView.setTextColor(ContextCompat.getColor(context, R.color.textcolor_lightblue));
      mCloseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rsclosebluebutton));
      headerContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.rsfloor_plan_header_white));
    } else {
      nameTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
      isDraftTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
      mCloseButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rsclosewhitebutton));
      headerContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.rsfloor_plan_header_blue));
    }

    this.adapter = adapter;
    this.delegate = delegate;
  }

  @Override
  public void onClick(View view) {
    if (delegate != null) {
      if (view.getId() == mCloseButton.getId()) {
        delegate.onCloseItemPressed(currentItem);
      } else {
        delegate.onItemPressed(currentItem);
      }
    }
  }

  @Override
  public boolean onLongClick(View view) {
    if (nameTextView.isFocusable()) {
      return false;
    }
    nameTextView.setFocusableInTouchMode(true);
    nameTextView.setFocusable(true);
    nameTextView.requestFocus();
    imm.showSoftInput(nameTextView, 0);

    nameTextView.setOnEditorActionListener((v, actionId, event) -> {
      if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
        finishNameEditing();
        return true;
      }
      return false;
    });

    nameTextView.setOnFocusChangeListener((view1, b) -> {
      if (!b) {
        finishNameEditing();
      }
    });

    if (delegate != null) {
      delegate.onItemLongPressed(currentItem);
    }
    return true;
  }

  private void finishNameEditing() {
    imm.hideSoftInputFromWindow(nameTextView.getWindowToken(), 0);
    currentItem.getVenuePlan().setName(nameTextView.getText().toString());
    nameTextView.setFocusable(false);
    nameTextView.setFocusableInTouchMode(false);
    adapter.setAddHeaderItemVisibility(true);
  }
}
