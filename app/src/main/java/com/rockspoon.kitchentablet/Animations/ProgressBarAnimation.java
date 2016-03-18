package com.rockspoon.kitchentablet.Animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.github.lzyzsd.circleprogress.ArcProgress;

/**
 * Created by juancamilovilladuarte on 2/5/16.
 */
public class ProgressBarAnimation extends Animation {
  private ArcProgress arcProgress;
  private float from;
  private float to;

  public ProgressBarAnimation(ArcProgress arcProgress, float from, float to) {
    super();
    this.arcProgress = arcProgress;
    this.from = from;
    this.to = to;
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {
    super.applyTransformation(interpolatedTime, t);
    float value = from + (to - from) * interpolatedTime;
    arcProgress.setProgress((int) value);
  }

}
