package com.rockspoon.rockandui.Interfaces;

/**
 * Created by lucas on 30/06/15.
 */
public interface LinearGridViewAdapterInterface {
  void reorderItems(int orgPos, int newPos);

  int getColCount();

  void setColCount(int colCount);

  boolean canReorder(int pos);
}
