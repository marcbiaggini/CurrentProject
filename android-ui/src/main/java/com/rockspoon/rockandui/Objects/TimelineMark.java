package com.rockspoon.rockandui.Objects;

import android.graphics.Color;

/**
 * Created by lucas on 08/12/15.
 */
public class TimelineMark {

  private float startPos;
  private float endPos;
  private int color = Color.BLACK;

  public TimelineMark(float startPos, float endPos) {
    this.startPos = startPos;
    this.endPos = endPos;
  }

  public TimelineMark(float startPos, float endPos, int color) {
    this(startPos, endPos);
    this.color = color;
  }

  public float getStartPos() {
    return startPos;
  }

  public void setStartPos(float startPos) {
    this.startPos = startPos;
  }

  public float getEndPos() {
    return endPos;
  }

  public void setEndPos(float endPos) {
    this.endPos = endPos;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }
}
