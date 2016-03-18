package com.rockspoon.ssl;

import java.io.InputStream;

public final class JKSKeyFactory implements SSLKeyFactory {
  @Override
  public InputStream getKeyCert() {
    return JKSKeyFactory.class.getClass().getResourceAsStream("/raw/keycert.p12");
  }

  @Override
  public InputStream getCA() {
    return JKSKeyFactory.class.getClass().getResourceAsStream("/raw/ca.jks");
  }

  @Override
  public String caType() {
    return "JKS";
  }
}
