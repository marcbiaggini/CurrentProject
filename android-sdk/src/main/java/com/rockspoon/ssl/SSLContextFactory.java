package com.rockspoon.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.rockspoon.exceptions.SSLContextFactoryException;

public final class SSLContextFactory {
  private static final char[] PASSPHRASE = "MySecretPassword".toCharArray();
  private static final char[] TRUST_PASSPHRASE = "MySecretPassword".toCharArray();

  private SSLContextFactory() {
  }

  public static SSLContext getContext(final SSLKeyFactory reader) {
    final KeyManagerFactory kmf = keyManagerFactory(reader);
    final TrustManagerFactory tmf = trustManagerFactory(reader);

    try {
      SSLContext c = SSLContext.getInstance("TLSv1.1");
      c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

      return c;
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      throw new SSLContextFactoryException(e);
    }
  }

  private static KeyManagerFactory keyManagerFactory(final SSLKeyFactory reader) {
    try (final InputStream key = reader.getKeyCert()) {
      final KeyStore ks = KeyStore.getInstance("PKCS12");
      final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

      ks.load(key, PASSPHRASE);
      kmf.init(ks, PASSPHRASE);

      return kmf;
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
      throw new SSLContextFactoryException(e);
    }
  }

  private static TrustManagerFactory trustManagerFactory(final SSLKeyFactory reader) {
    try (final InputStream key = reader.getCA();) {
      final KeyStore ks = KeyStore.getInstance(reader.caType());
      final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

      ks.load(key, TRUST_PASSPHRASE);
      tmf.init(ks);

      return tmf;
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new SSLContextFactoryException(e);
    }
  }
}
