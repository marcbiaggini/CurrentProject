package com.rockspoon.services;

import com.rockspoon.interceptors.SessionInjectInterceptor;
import com.rockspoon.models.venue.layout.DiningSpotList;
import com.rockspoon.models.venue.layout.DiningSpotsListRequest;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.models.venue.ordering.DiningPartyCreateRequest;
import com.rockspoon.models.venue.ordering.DiningPartyListRequest;
import com.rockspoon.models.venue.ordering.DiningPartyUpdateRequest;
import com.rockspoon.models.venue.ordering.item.ItemsFired;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.models.venue.ordering.item.ItemInstance;
import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;
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
public interface OrderService extends RestClientErrorHandling {

  @Post("/venue/ordering/diningparty/create")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  DiningParty createDiningParty(@Body DiningPartyCreateRequest request);

  @Post("/venue/ordering/diningparty/update/{diningPartyId}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  DiningParty updateDiningParty(@Path String diningPartyId, @Body DiningPartyUpdateRequest request);

  @Post("/venue/ordering/diningparty/list")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<DiningParty> listDiningParty(@Body DiningPartyListRequest request);

  @Post("/venue/ordering/diningsectionspot/list")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  DiningSpotList spotsListBySectionId(@Body DiningSpotsListRequest request);

  @Get("/venue/ordering/itemsfired/list_active_items_fired")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<ItemsFired> listItemsFired();

  @Get("/venue/ordering/itemsfired/list_active_items_fired?productionAreaStationId={productionAreaStationId}")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<ItemsFired> listItemsFired(@Path Long productionAreaStationId);

  @Post("/venue/ordering/itemsfired/update")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<ItemsFired> updateListItems(@Body List<ItemsFired> itemsUpdated);

  @Get("/venue/ordering/cart/{partyId}/list_items")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<ItemInstance> listCart(@Path Long partyId);

  @Get("/venue/ordering/cart/{partyId}/list_item_details")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  List<ListCartDetails> listCartDetails(@Path Long partyId);

  @Post("/venue/ordering/cart/{partyId}/update")
  @Headers({@Header(name = SessionInjectInterceptor.HEADER_AUTHORIZATION, value = SessionInjectInterceptor.INJECT_USER_SESSION)})
  void updateOrderingCart(@Path Long partyId, @Body ItemInstanceRequest itemInstanceRequest);
}
