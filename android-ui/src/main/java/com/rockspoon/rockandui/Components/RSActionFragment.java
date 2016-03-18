package com.rockspoon.rockandui.Components;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.rockandui.Components.ActionList.ActionList;
import com.rockspoon.rockandui.Components.ActionList.ActionRecyclerListener;
import com.rockspoon.rockandui.Components.ActionList.ActionScrollViewListener;
import com.rockspoon.rockandui.R;

/**
 * Created by greenfrvr
 */
public abstract class RSActionFragment extends RSBaseFragment {

  private ActionList actionList;

  private int yOffset = 0;

  private final RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      yOffset += dy;
      adjustBackButtonVisibility();
    }
  };

  private final NestedScrollView.OnScrollChangeListener scrollviewScrollListener = (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
    yOffset += scrollY - oldScrollY;
    adjustBackButtonVisibility();
  };

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    if (view != null) {
      actionList = (ActionList) view.findViewById(R.id.action_list);
    }
    return view;
  }

  public void setScrollableView(RecyclerView view) {
    if (actionList != null) {
      view.addOnScrollListener(recyclerScrollListener);
      actionList.setOnActionListAction(new ActionRecyclerListener(view));
    }
  }

  public void removeScrollableView(RecyclerView view) {
    view.removeOnScrollListener(recyclerScrollListener);
  }

  public void setScrollableView(NestedScrollView view) {
    if (actionList != null) {
      view.setOnScrollChangeListener(scrollviewScrollListener);
      actionList.setOnActionListAction(new ActionScrollViewListener(view));
    }
  }

  private void adjustBackButtonVisibility() {
    if (yOffset < getResources().getDimensionPixelOffset(R.dimen.scrollable_offset)) {
      actionList.setScrollBackButtonVisibility(View.GONE);
    } else {
      actionList.setScrollBackButtonVisibility(View.VISIBLE);
    }
  }

}
