package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * LockableScrollView.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/20/16.
 * <p>
 * Adds capability to enable/disable scrolling on ScrollView.
 * Can be useful when you want to disable scrolling for any item inside a ScrollView
 */
public class LockableScrollView extends ScrollView {

  private boolean enableScrolling = true;

  public boolean isEnableScrolling() {
    return enableScrolling;
  }

  public void setEnableScrolling(boolean enableScrolling) {
    this.enableScrolling = enableScrolling;
  }

  public LockableScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public LockableScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LockableScrollView(Context context) {
    super(context);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {

    if (isEnableScrolling()) {
      return super.onInterceptTouchEvent(event);
    } else {
      return false;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {

    if (isEnableScrolling()) {
      return super.onTouchEvent(event);
    } else {
      return false;
    }
  }
}
