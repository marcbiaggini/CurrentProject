package com.rockspoon.rockandui.Interfaces;

import com.rockspoon.models.venue.ordering.DiningParty;

/**
 * Created by Lucas Teske on 27/07/15.
 */
public interface OnTableClick {
  void onClick(int pos, DiningParty party);
}
