package com.rockspoon.rockpos.pos.test.scenarios.Resources;

import android.support.test.espresso.IdlingResource;

/**
 * Created by juancamilovilladuarte on 3/11/16.
 */
public class TimeIdlingResource implements IdlingResource {
  private final long startTime;
  private final long waitingTime;
  private ResourceCallback resourceCallback;

  public TimeIdlingResource(long waitingTime) {
    this.startTime = System.currentTimeMillis();
    this.waitingTime = waitingTime;
  }

  @Override
  public String getName() {
    return TimeIdlingResource.class.getName() + ":" + waitingTime;
  }

  @Override
  public boolean isIdleNow() {
    long elapsed = System.currentTimeMillis() - startTime;
    boolean idle = (elapsed >= waitingTime);
    if (idle) {
      resourceCallback.onTransitionToIdle();
    }
    return idle;
  }

  @Override
  public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
    this.resourceCallback = resourceCallback;
  }
}
