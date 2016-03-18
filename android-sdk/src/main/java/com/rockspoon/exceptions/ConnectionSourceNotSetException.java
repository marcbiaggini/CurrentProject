package com.rockspoon.exceptions;

public class ConnectionSourceNotSetException extends RuntimeException {
  private static final long serialVersionUID = -4489853978532065135L;

  public ConnectionSourceNotSetException() {
    super("ORMLite ConnectionSource must be injected by ServiceManager.getInstance().setConnectionSourceFactory(...)");
  }
}
