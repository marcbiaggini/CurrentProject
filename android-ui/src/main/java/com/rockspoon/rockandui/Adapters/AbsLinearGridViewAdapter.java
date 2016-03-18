package com.rockspoon.rockandui.Adapters;

import android.content.Context;

import com.rockspoon.rockandui.Interfaces.LinearGridViewAdapterInterface;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lucas on 30/06/15.
 */
public abstract class AbsLinearGridViewAdapter<T> extends FilterAdapter<T> implements LinearGridViewAdapterInterface {

  public static final int NO_ITEM = -1;
  private final HashMap<Object, Integer> idMap = new HashMap<>();
  private int nextStableId = 0;

  public AbsLinearGridViewAdapter(Context ctx) {
    super(ctx);
  }

  public AbsLinearGridViewAdapter(final Context ctx, final List<T> data) {
    super(ctx, data);
  }

  @Override
  public final boolean hasStableIds() {
    return true;
  }

  protected void addStableId(Object item) {
    idMap.put(item, nextStableId++);
  }

  protected void addAllStableId(List<?> items) {
    for (final Object item : items)
      addStableId(item);
  }

  @Override
  public final long getItemId(int position) {
    if (position < 0 || position >= idMap.size())
      return NO_ITEM;

    final Object item = getItem(position);
    return idMap.get(item);
  }

  protected void clearStableIdMap() {
    idMap.clear();
  }

  protected void removeStableID(Object item) {
    idMap.remove(item);
  }
}
