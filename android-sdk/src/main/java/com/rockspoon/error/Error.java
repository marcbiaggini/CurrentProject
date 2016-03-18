package com.rockspoon.error;

import org.springframework.web.client.RestClientException;

public final class Error extends RestClientException {
  private static final long serialVersionUID = -710488268845379700L;

  private final ErrorCode errorCode;

  public Error(final ErrorCode errorCode, final Throwable exception) {
    super("", exception);
    this.errorCode = errorCode;
  }

  public Error(final ErrorCode errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return this.errorCode;
  }
}
