package com.rockspoon.exceptions;

public class SSLKeyReaderNotSetException extends RuntimeException {
  private static final long serialVersionUID = 8804598302546447999L;

  public SSLKeyReaderNotSetException() {
    super("SSLKeyReader must be injected by ServicesManager.getInstance().setSSLKeyReader(...)");
  }
}
