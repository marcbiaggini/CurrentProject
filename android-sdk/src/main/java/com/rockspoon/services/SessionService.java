package com.rockspoon.services;

import com.rockspoon.error.*;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.session.ImmutableSession;
import com.rockspoon.models.user.User;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import com.rockspoon.error.Error;
import com.rockspoon.models.venue.device.Device;

import java.sql.SQLException;

/**
 * Created by lucas on 14/01/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class SessionService {

  @Bean
  SessionDatabaseService sds;

  @RestService
  UserService us;

  @RestService
  DeviceService ds;

  @Bean
  RSRestErrorHandler errorHandler;

  @AfterInject
  void afterInject() {
    us.setRestErrorHandler(errorHandler);
    ds.setRestErrorHandler(errorHandler);
  }

  public ImmutableSession restoreSession(final String sessionType) {
    try {
      final ImmutableSession session = sds.restoreSession(sessionType);
      return session;
    } catch (SQLException e) {
      //  Do nothing
    }

    return null;
  }

  public User userLogin(ImmutableCredentials credentials) throws Error {
    try {
      ImmutableSession deviceSession = sds.restoreSession("device");
      if (deviceSession != null) {
        credentials = credentials.withDeviceUID(deviceSession.getToken());
      }
      String token = (String) us.connect(credentials).get("token");
      if (token != null) {
        final ImmutableSession session = new ImmutableSession(token, "user");
        sds.saveOrUpdateSession(session);
        return us.fetch();
      }
    } catch (SQLException e) {
      throw new Error(ErrorCode.InternalError, "Error writting session.");
    }

    return null;
  }

  public Device deviceLogin(final String deviceId, final String venueId) throws Error {
    try {
      Device d = ds.register(deviceId, venueId);
      sds.saveOrUpdateSession(new ImmutableSession(d.getUid(), "device"));
      return d;
    } catch (SQLException e) {
      throw new Error(ErrorCode.InternalError, "Error writting session.");
    }
  }

  public void userLogout() throws Error {
    try {
      ImmutableSession userSession = sds.restoreSession("user");
      if (userSession != null) {
        us.disconnect();
        sds.removeSession(userSession);
      }
    } catch (SQLException e) {
      throw new Error(ErrorCode.InternalError, "Error removing session.");
    }
  }
}
