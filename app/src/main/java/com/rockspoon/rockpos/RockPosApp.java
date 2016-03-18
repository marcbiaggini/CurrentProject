package com.rockspoon.rockpos;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.rockspoon.error.RSRestErrorHandler;
import com.rockspoon.helpers.CachedHTTP;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.printer.Session;
import com.rockspoon.rockandui.CountryTools;
import com.rockspoon.rockandui.Tools;
import com.rockspoon.services.SessionDatabaseService;
import com.rockspoon.services.SessionService;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import timber.log.Timber;

/**
 * Created by lucas on 09/07/15.
 */
@EApplication
@ReportsCrashes(
    formUri = com.rockspoon.sdk.BuildConfig.API_URL + "/device/acra",
    reportType = org.acra.sender.HttpSender.Type.JSON,
    mode = ReportingInteractionMode.DIALOG,
    resToastText = R.string.message_crash_toast_text,
    resDialogText = R.string.message_crash_toast_text)
public class RockPosApp extends Application {

  @Bean
  SessionDatabaseService sessionDatabaseService;

  @Bean
  SessionService sessionService;

  @Bean
  RSRestErrorHandler errorHandler;

  public static Context getAppContext() {
    return RockPosApp_.getInstance();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    ACRA.init(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    Session.initSession(this);
    CountryTools.InitCountries(this);
    CachedHTTP.enableHTTPResponseCache(this);

    RockServices.BUILD_VERSION = BuildConfig.BUILD_VERSION;
    RockServices.VERSION_CODE = BuildConfig.VERSION_CODE;
    RockServices.VERSION_NAME = BuildConfig.VERSION_NAME;
    RockServices.setSessionService(sessionService);
    RockServices.initializeServices(this, errorHandler);

    Tools.replaceApplicationFont("DEFAULT", Typeface.createFromAsset(getAssets(), "fonts/Helvetica_Regular.ttf"));
  }

}
