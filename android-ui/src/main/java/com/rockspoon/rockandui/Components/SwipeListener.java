package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.rockspoon.rockandui.Interfaces.OnSwipe;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 22/07/15.
 */
public class SwipeListener extends GestureDetector.SimpleOnGestureListener {
  private final int SWIPE_MIN_DISTANCE;
  private final int SWIPE_THRESHOLD_VELOCITY;
  private OnSwipe listener;
  private int lastMotionX;
  private int lastMotionY;

  public SwipeListener(final Context ctx, final OnSwipe listener) {
    this.listener = listener;
    SWIPE_MIN_DISTANCE = ctx.getResources().getInteger(R.integer.swipelistener_min_distance);
    SWIPE_THRESHOLD_VELOCITY = ctx.getResources().getInteger(R.integer.swipelistener_threshold_velocity);
  }

  public void updateMotion(int motionX, int motionY) {
    lastMotionX = motionX;
    lastMotionY = motionY;
  }

  public int getLastMotionX() {
    return lastMotionX;
  }

  public int getLastMotionY() {
    return lastMotionY;
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    if (lastMotionX - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      if (listener != null) {
        listener.onSwipeLeft();
        return true;
      }
    }

    if (e2.getX() - lastMotionX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      if (listener != null) {
        listener.onSwipeRight();
        return true;
      }
    }

    return false;
  }
}
