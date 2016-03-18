package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.user.ImmutableChangePasswordRequest;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.sdk.BuildConfig;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 14/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = MappingJackson2HttpMessageConverter.class, interceptors = {SessionInjectInterceptor.class})
public interface UserService extends RestClientErrorHandling {

  @Post("/user/login")
  Map<String, Object> connect(@Body final ImmutableCredentials credentials);

  @Post("/user/logout")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void disconnect();

  @Get("/user/{userId}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  User fetch(@Path final String userId);

  @Get("/user")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  User fetch();

  @Post("/user")
  User create(@Body final User user);

  @Put("/device/user/profile")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  User update(@Body final User user);

  @Put("/device/user/change-password")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void changePassword(@Body ImmutableChangePasswordRequest request);

  @Put("/user/trigger-email-validation")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void emailValidationTrigger();

  @Put("/user/trigger-phone-validation")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void phoneValidationTrigger();

  @Post("/user/perform-email-validation/{pin}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void emailValidationPerform(@Path final String pin);

  @Post("/user/perform-phone-validation/{pin}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void phoneValidationPerform(@Path final String pin);

  @Get("/user/venue")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<Venue> fetchVenues();
}
