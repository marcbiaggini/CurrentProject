package com.rockspoon.interceptors;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.models.session.ImmutableSession;
import com.rockspoon.services.SessionDatabaseService;
import com.rockspoon.services.SessionService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by lucas on 13/01/16.
 */
@EBean
public class SessionInjectInterceptor implements ClientHttpRequestInterceptor {

  public static final String INJECT_USER_SESSION = "{INJECT_USER_SESSION}";
  public static final String INJECT_DEVICE_SESSION = "{INJECT_DEVICE_SESSION}";
  public static final String HEADER_AUTHORIZATION = "Authorization";

  @Bean
  SessionService ss;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    final HttpHeaders headers = request.getHeaders();
    final List<String> authorizationInject = headers.get(HEADER_AUTHORIZATION);

    if (authorizationInject != null && authorizationInject.size() > 0) {
      final String authorizationType = authorizationInject.get(0);
      ImmutableSession session = null;

      if (INJECT_DEVICE_SESSION.equals(authorizationType)) {
        session = ss.restoreSession("device");
        if (session == null) throw new Error(ErrorCode.ForbiddenError, "Device must be Logged");
      } else if (INJECT_USER_SESSION.equals(authorizationType)) {
        session = ss.restoreSession("user");
        if (session == null) throw new Error(ErrorCode.ForbiddenError, "User must be Logged");
      }

      if (session != null) {
        headers.remove(HEADER_AUTHORIZATION);
        headers.add(HEADER_AUTHORIZATION, "Bearer " + session.getToken());
      }
    }

    return execution.execute(request, body);
  }
}
