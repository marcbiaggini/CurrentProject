package com.rockspoon.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.rockspoon.sdk.R;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lucas on 13/07/15.
 */
public class CachedHTTP {

  private static final String TAG = "CachedHTTP";

  public static void enableHTTPResponseCache(final Context ctx) {
    final long cacheSize = ctx.getResources().getInteger(R.integer.cachedhttp_cache_size);
    try {
      Log.d(TAG, "Enabling HTTP Response Cache");
      final long httpCacheSize = cacheSize * 1024 * 1024;
      final File httpCacheDir = new File(ctx.getCacheDir(), "http");
      Class.forName("android.net.http.HttpResponseCache")
          .getMethod("install", File.class, long.class)
          .invoke(null, httpCacheDir, httpCacheSize);
    } catch (Exception httpResponseCacheNotAvailable) {
      Log.d(TAG, "HTTP response cache is unavailable.");
    }
  }

  public static Bitmap getCachedImageFromURL(final String surl) {
    try {
      final URL url = new URL(surl);
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setUseCaches(true);
      connection.setDoInput(true);
      connection.connect();
      if (connection.getResponseCode() == 200) {
        return BitmapFactory.decodeStream(connection.getInputStream());
      } else {
        Log.e(TAG, "Response Code " + connection.getResponseCode() + " when getting " + surl);
        return null;
      }
    } catch (final IOException e) {
      Log.d(TAG, "IO Exception getting " + surl + ": " + e.getLocalizedMessage());
    }
    return null;
  }
}
