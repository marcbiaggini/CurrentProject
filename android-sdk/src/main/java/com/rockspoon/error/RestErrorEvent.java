package com.rockspoon.error;

/**
 * Created by Eugen K. on 3/16/16.
 */
public class RestErrorEvent {

  private ErrorCode errorCode;
  private String message;

  public RestErrorEvent(ErrorCode errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }

}
