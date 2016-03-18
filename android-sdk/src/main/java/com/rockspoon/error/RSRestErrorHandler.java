package com.rockspoon.error;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockspoon.helpers.BusProvider;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.api.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 14/01/16.
 */
@EBean
public class RSRestErrorHandler implements RestErrorHandler {

  protected static final ObjectMapper MAPPER = new ObjectMapper();

  @Override
  public void onRestClientExceptionThrown(NestedRuntimeException e) {
    String message = null;
    String errorString = null;
    if (HttpStatusCodeException.class.isInstance(e)) {
      HttpStatusCodeException e2 = (HttpStatusCodeException) e;
      List<String> contentType = e2.getResponseHeaders().get("Content-Type");
      if (contentType.size() > 0 && contentType.get(0).equals("application/json")) {
        try {
          final Map<String, Object> json = MAPPER.readValue(e2.getResponseBodyAsString(), new TypeReference<Map<String, Object>>() {
          });
          errorString = (String) json.get("error");
          message = (String) json.get("message");
          ErrorCode error = ErrorCode.valueOf(errorString);
//          throw new Error(error, message);
          BusProvider.getInstance().post(new RestErrorEvent(error, message));
        } catch (IllegalArgumentException iae) {
//          throw new Error(ErrorCode.UnknownError, "Unknown Error from Backend (" + errorString + "): " + message);
          BusProvider.getInstance().post(new RestErrorEvent(ErrorCode.UnknownError, "Unknown Error from Backend (" + errorString + "): " + message));
        } catch (IOException ioe) {
//          throw new Error(ErrorCode.InternalError, "Cannot convert JSON: " + ioe.getLocalizedMessage());
          BusProvider.getInstance().post(new RestErrorEvent(ErrorCode.InternalError, "Cannot convert JSON: " + ioe.getLocalizedMessage()));
        }
      }
    } else {
//      throw new Error(ErrorCode.UnknownError, "Unknown Error(" + e.getClass().getName() + "): " + e.getLocalizedMessage());
      BusProvider.getInstance().post(new RestErrorEvent(ErrorCode.UnknownError, "Unknown Error(" + e.getClass().getName() + "): " + e.getLocalizedMessage()));
    }
  }
}
