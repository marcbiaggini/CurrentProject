package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.pin.PinRequest;
import com.rockspoon.models.user.User;
import com.rockspoon.models.user.ImmutableUserPinPasswordRequest;
import com.rockspoon.sdk.BuildConfig;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

/**
 * Created by lucas on 14/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = MappingJackson2HttpMessageConverter.class, interceptors = {SessionInjectInterceptor.class})
public interface ForgotPasswordService extends RestClientErrorHandling {

  @Get("/user/forgot-password/search/{criteria}")
  List<User> searchUser(@Path final String criteria);

  @Post("/user/forgot-password/request-pin")
  String requestPinByEmail(@Body final PinRequest request);

  @Put("/user/forgot-password/check-pin")
  String checkPin(@Body final ImmutableUserPinPasswordRequest pinRequest);

  @Put("/user/forgot-password/reset-password")
  String resetPassword(@Body final ImmutableUserPinPasswordRequest pinRequest);

  void setRootUrl(String rootUrl);
}
