package com.rockspoon.models.venue.ordering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.models.property.PaymentMethod;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 03/02/16.
 */
@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPayment implements Serializable {
  private Long userPaymentId;
  private BigDecimal amount;
  private Map<String, Object> cardInfo;
  private PaymentMethod method;
  private TabEntry tabEntry;
}
