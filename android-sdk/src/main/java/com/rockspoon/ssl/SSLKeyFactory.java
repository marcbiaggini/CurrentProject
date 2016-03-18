package com.rockspoon.ssl;

import java.io.InputStream;

public interface SSLKeyFactory {
  InputStream getKeyCert();

  InputStream getCA();

  String caType();
}
