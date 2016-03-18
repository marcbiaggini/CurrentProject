package com.rockspoon.helpers.events;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by lucas on 18/02/16.
 */
public class GCMEvent implements Serializable, CacheBusEvent {
  private String from;
  private Bundle data;

  public GCMEvent(String from, Bundle data) {
    this.from = from;
    this.data = data;
  }

  public String getFrom() {
    return from;
  }

  public Bundle getData() {
    return data;
  }
}
