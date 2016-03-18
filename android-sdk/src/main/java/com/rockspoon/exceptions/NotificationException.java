package com.rockspoon.exceptions;

public class NotificationException extends RuntimeException {
  private static final long serialVersionUID = 1048785793160737975L;

  public NotificationException(final Throwable cause) {
    super(cause);
  }
}
