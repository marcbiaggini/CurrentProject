package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 15/07/15.
 */
public class TableEmpty extends Table {

  public TableEmpty(final Context ctx) {
    super(ctx, R.layout.table_empty);
  }

  public TableEmpty(final Context ctx, final AttributeSet attrs) {
    super(ctx, attrs, R.layout.table_empty);
  }

  public void updateOccupiedSeats() {
    occupiedNumSeats = 0;
  }

  @Override
  public TableType getType() {
    return TableType.EMPTY;
  }
}
