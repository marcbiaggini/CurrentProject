package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.device.UpdateDeviceGCMTokenRequest;
import com.rockspoon.models.venue.network.Network;
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
 * Created by lucas on 13/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = {MappingJackson2HttpMessageConverter.class}, interceptors = {SessionInjectInterceptor.class})
public interface DeviceService extends RestClientErrorHandling {

  @Get("/venue/{venueId}/device")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<Device> list(@Path final String venueId);

  @Get("/venue/{venueId}/device/whoami")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  Device whoAmI(@Path final String venueId);

  @Post("/venue/{venueId}/device/{deviceId}/register")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Device register(@Path final String deviceId, @Path final String venueId);

  @Put("/venue/{venueId}/network")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  void addWIFINetwork(@Path final String venueId, @Body final Network network);

  @Get("/venue/{venueId}/network")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  List<Network> listWIFINetworks(@Path final String venueId);

  @Put("/venue/{venueId}/device/metadata")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  Map<String, Object> updateMetadata(@Path final String venueId, @Body final Map<String, Object> metadata);

  @Post("/venue/{venueId}/device/gcm-token")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  void updateGCMToken(@Path final String venueId, @Body final UpdateDeviceGCMTokenRequest request);

  @Get("/venue/{venueId}/device/cache-topics")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  List<String> getCacheTopics(@Path final String venueId);
}
