package com.rockspoon.rockandui.Components.ActionList;

import android.support.v7.widget.RecyclerView;

import com.rockspoon.rockandui.Interfaces.OnActionListAction;

public class ActionRecyclerListener implements OnActionListAction {

    public RecyclerView view;

    public ActionRecyclerListener(RecyclerView view) {
      this.view = view;
    }

    @Override
    public void onClick(int pos, String title) {

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onScrollBackClick() {
      view.smoothScrollToPosition(0);
    }
  }