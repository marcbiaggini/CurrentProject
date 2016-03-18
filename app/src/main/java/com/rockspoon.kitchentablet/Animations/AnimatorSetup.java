package com.rockspoon.kitchentablet.Animations;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

/**
 * Created by juancamilovilladuarte on 2/10/16.
 */
public class AnimatorSetup {

  public boolean setGrowAnimation(View view, long delay) {
    view.setScaleX(0.5f);
    view.setScaleY(0.5f);
    view.animate()
        .scaleX(1)
        .scaleY(1)
        .setDuration(3000)
        .setStartDelay(delay)
        .setInterpolator(new OvershootInterpolator())
        .start();
    return true;
  }

  private Animation FadeIn(int t) {
    AlphaAnimation localAlpha = new AlphaAnimation(1.0f, 0.15f);
    localAlpha.setDuration(800l);
    localAlpha.setStartOffset(100l);
    localAlpha.setFillEnabled(true);
    localAlpha.setFillAfter(true);
    localAlpha.setInterpolator(new AccelerateInterpolator());
    return localAlpha;
  }

  private Animation FadeOut(int t) {
    Animation fade;
    fade = new AlphaAnimation(0.15f, 1.0f);
    fade.setDuration(800l);
    fade.setFillEnabled(true);
    fade.setFillAfter(true);
    fade.setInterpolator(new AccelerateInterpolator());
    return fade;
  }
}
