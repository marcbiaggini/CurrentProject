package com.rockspoon.rockandui.Objects;

import android.util.Pair;

import com.rockspoon.models.venue.ordering.DinerSession;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eugen K. on 2/24/16.
 */
@Getter
@Setter
public class OrderingItemSettings implements Serializable {
  private int quantity;
  private int selectedSizeId;
  private String specialInstructions;
  private BigDecimal additionalPrice;
  private List<Pair<Integer, DinerSession>> seats;

}
