package com.rockspoon.models.pin;

import com.rockspoon.models.user.User;

/**
 * Created by lucas on 14/01/16.
 */
public class PinRequest {
  protected String method;
  protected String userId;

  public PinRequest(User user, boolean emailPreferred) {
    this.userId = user.getId();
    this.method = emailPreferred ? "reset-email-method" : "reset-phone-method";
  }
}
