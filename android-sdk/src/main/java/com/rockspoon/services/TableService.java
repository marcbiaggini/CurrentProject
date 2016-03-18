package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.sdk.BuildConfig;

import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Created by lucas on 14/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = MappingJackson2HttpMessageConverter.class, interceptors = {SessionInjectInterceptor.class})
public interface TableService extends RestClientErrorHandling {
  // TODO: Table Service REST calls
  void setRootUrl(String rootUrl);
}
