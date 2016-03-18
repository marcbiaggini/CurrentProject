package com.rockspoon.rockandui.Adapters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 30/06/15.
 */
public abstract class LinearGridViewAdapter<T> extends AbsLinearGridViewAdapter<T> {
  private int columnCount;

  protected LinearGridViewAdapter(final Context ctx, final int columnCount) {
    super(ctx);
    this.columnCount = columnCount;
  }

  public LinearGridViewAdapter(final Context ctx, final int columnCount, final List<T> items) {
    super(ctx, items);
    this.columnCount = columnCount;
    addAllStableId(items);
  }

  public abstract boolean itemIsSelected(int position);

  @Override
  public void setData(final List<T> items) {
    clear();
    super.setData(items);
    addAllStableId(items);
    notifyDataSetInvalidated();
  }

  protected void clear() {
    clearStableIdMap();
    originalList.clear();
    filteredList.clear();
  }

  public void add(int pos, T item) {
    addStableId(item);
    originalList.add(pos, item);
    refresh();
  }

  public void add(List<T> items) {
    addAllStableId(items);
    this.originalList.addAll(items);
    this.filteredList.addAll(items);
    refresh();
  }

  public void remove(T item) {
    this.originalList.remove(item);
    this.filteredList.remove(item);
    removeStableID(item);
    refresh();
  }

  @Override
  public int getColCount() {
    return columnCount;
  }

  @Override
  public void setColCount(int colCount) {
    this.columnCount = colCount;
    refresh();
  }

  @Override
  public void reorderItems(int orgPos, int newPos) {
    // TODO: We are not using reordering with filter, but if we start using, this will get complicated.
    if (newPos < getCount()) {
      final T obj = originalList.remove(orgPos);
      originalList.add(newPos, obj);
      filteredList.clear();
      filteredList.addAll(originalList);
      refresh();
    }
  }

  @Override
  public boolean canReorder(int pos) {
    return true;
  }

  public Context getContext() {
    return ctx;
  }
}
