package com.rockspoon.rockandui.Components.ActionList;

import android.support.v4.widget.NestedScrollView;

import com.rockspoon.rockandui.Interfaces.OnActionListAction;

public class ActionScrollViewListener implements OnActionListAction {

    public NestedScrollView view;

    public ActionScrollViewListener(NestedScrollView view) {
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
      view.smoothScrollTo(0, 0);
    }
  }
