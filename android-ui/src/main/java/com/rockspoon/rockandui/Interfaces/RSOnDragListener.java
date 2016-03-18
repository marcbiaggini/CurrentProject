package com.rockspoon.rockandui.Interfaces;

/**
 * Created by lucas on 13/07/15.
 */
public interface RSOnDragListener {
  void onDragStarted(int position);

  void onDragPositionsChanged(int oldPosition, int newPosition);
}