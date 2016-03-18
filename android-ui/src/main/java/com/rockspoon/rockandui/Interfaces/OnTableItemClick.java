package com.rockspoon.rockandui.Interfaces;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.Objects.TableData;

/**
 * Created by lucas on 30/07/15.
 */
public interface OnTableItemClick {
  void onItemClick(DinerSession session, DiningParty party);
}
