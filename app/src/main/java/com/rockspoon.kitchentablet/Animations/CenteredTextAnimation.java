package com.rockspoon.kitchentablet.Animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by juancamilovilladuarte on 2/24/16.
 */
public class CenteredTextAnimation extends Animation {
  private TextView text;
  private boolean isCenter;

  public CenteredTextAnimation(TextView text, Boolean isCenter) {
    super();
    this.text = text;
    this.isCenter = isCenter;
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {
    super.applyTransformation(interpolatedTime, t);
    if (isCenter) {
      RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) text.getLayoutParams();
      lp.removeRule(RelativeLayout.CENTER_VERTICAL);
      text.setLayoutParams(lp);
    } else {
      RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) text.getLayoutParams();
      lp.addRule(RelativeLayout.CENTER_VERTICAL);
      text.setLayoutParams(lp);
    }
  }
}
