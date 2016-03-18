package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.brand.Brand;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.models.venue.menu.Menu;
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

/**
 * Created by lucas on 14/01/16.
 */
@Rest(rootUrl = BuildConfig.API_URL, converters = MappingJackson2HttpMessageConverter.class, interceptors = {SessionInjectInterceptor.class})
public interface VenueService extends RestClientErrorHandling {

  @Post("/brand")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Brand createBrand(@Body final Brand brand);

  @Put("/brand")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Brand updateBrand(@Body final Brand brand);

  @Post("/venue/general-information")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Venue createGeneralInfo(@Body final Venue venue);

  @Put("/venue/general-information")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Venue updateGeneralInfo(@Body final Venue venue);

  @Get("/venue/{venueId}/menu?fetchCategories={fetchCategories}&fetchItems={fetchItems}&fetchSettingsAndPrices={fetchSettingsAndPrices}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  List<Menu> fetchMenuList(@Path final String venueId, @Path final boolean fetchCategories, @Path final boolean fetchItems, @Path final boolean fetchSettingsAndPrices);

  @Get("/venue/{venueId}/menu/{menuId}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  Menu fetchMenu(@Path final String venueId, @Path final String menuId);

  @Get("/venue/dining-section") // venueId from token
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_DEVICE_SESSION)})
  VenueFloorPlan fetchFloorPlan();
}
