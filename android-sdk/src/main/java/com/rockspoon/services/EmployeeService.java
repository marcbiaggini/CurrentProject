package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.user.clockin.ClockEventType;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.sdk.BuildConfig;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

/**
 * Created by lucas on 13/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = MappingJackson2HttpMessageConverter.class, interceptors = {SessionInjectInterceptor.class})
public interface EmployeeService extends RestClientErrorHandling {

  @Get("/venue/{venueId}/employee")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  List<Employee> getVenueEmployees(@Path final String venueId);

  @Post("/venue/{venueId}/employee/{type}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  UserClockEvent clockAction(@Path final String venueId, @Path final ClockEventType type);

  @Get("/venue/{venueId}/employee/clock-events")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<UserClockEvent> getUserOpenClockEvents(@Path final String venueId);

  void setRootUrl(String rootUrl);
}
