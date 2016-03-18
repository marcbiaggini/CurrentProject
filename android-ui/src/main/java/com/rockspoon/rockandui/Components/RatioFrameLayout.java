package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by greenfrvr
 */
public class RatioFrameLayout extends FrameLayout {

  private static final double DEFAULT_WIDTH_RATIO = 1;
  private static final double DEFAULT_HEIGHT_RATIO = 1;

  private double widthRatio = DEFAULT_WIDTH_RATIO;
  private double heightRatio = DEFAULT_HEIGHT_RATIO;

  public RatioFrameLayout(Context context) {
    super(context, null, 0);
  }

  public RatioFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs, 0);
  }

  public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setRatio(double widthRatio, double heightRatio) {
    this.widthRatio = widthRatio;
    this.heightRatio = heightRatio;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = (int) (widthRatio / heightRatio * widthSize);
    int newHeightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, newHeightSpec);
  }

}
