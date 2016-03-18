package com.rockspoon.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Errors extends RuntimeException {
  private static final long serialVersionUID = -710488268845379700L;

  private final List<ErrorCode> errors;

  public Errors(final Throwable exception, final ErrorCode... codes) {
    super(exception);
    this.errors = new ArrayList<ErrorCode>();
    Collections.addAll(errors, codes);
  }

  public Errors(final String message, final ErrorCode... codes) {
    super(message);
    this.errors = new ArrayList<ErrorCode>();
    Collections.addAll(errors, codes);
  }

  public Errors(final String message) {
    super(message);
    this.errors = new ArrayList<ErrorCode>();
  }

  public List<ErrorCode> getErrorCodes() {
    return this.errors;
  }

  public void appendErrorCode(final ErrorCode... codes) {
    Collections.addAll(errors, codes);
  }

  public ErrorCode getSingleErrorCode() {
    if (errors != null && errors.size() == 1) {
      return errors.get(0);
    } else {
      throw new IllegalAccessError();
    }
  }
}
