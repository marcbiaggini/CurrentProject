package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 25/11/15.
 */
public class CircleProgressBar extends View {
  /**
   * ProgressBar's line thickness
   */
  private float strokeWidth = 4;
  private int progress = 0;
  private int max = 100;

  private int lastWidth = 0;
  private int lastHeight = 0;
  private int minSize = 0;

  private int startAngle = -90;
  private int gradientStartingAngle = 0;

  private int color = Color.TRANSPARENT;
  private int colorStart = Color.DKGRAY;
  private int colorEnd = -1;

  private String text = "";
  private int textSize = 10;
  private int textColor = Color.BLACK;

  private RectF rectF;
  private Rect textBounds;
  private Paint backgroundPaint;
  private Paint foregroundPaint;
  private Paint textPaint;

  private SweepGradient gradient;
  private Matrix gradientMatrix;

  public CircleProgressBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    rectF = new RectF();
    gradientMatrix = new Matrix();
    textBounds = new Rect();
    TypedArray typedArray = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.CircleProgressBar,
        0, 0);

    try {
      strokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, strokeWidth);
      progress = typedArray.getInt(R.styleable.CircleProgressBar_progress, progress);
      colorStart = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColorStart, colorStart);
      colorEnd = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColorEnd, colorEnd);
      color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color);
      max = typedArray.getInt(R.styleable.CircleProgressBar_max, max);
      startAngle = typedArray.getInt(R.styleable.CircleProgressBar_startingAngle, startAngle);
      gradientStartingAngle = typedArray.getInt(R.styleable.CircleProgressBar_gradientStartingAngle, gradientStartingAngle);
      text = typedArray.getString(R.styleable.CircleProgressBar_android_text);
      textSize = typedArray.getDimensionPixelSize(R.styleable.CircleProgressBar_android_textSize, 10);
      textColor = typedArray.getColor(R.styleable.CircleProgressBar_android_textColor, textColor);
    } finally {
      typedArray.recycle();
    }

    backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    backgroundPaint.setColor(color);
    backgroundPaint.setStyle(Paint.Style.STROKE);
    backgroundPaint.setStrokeWidth(strokeWidth);

    foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    if (colorEnd != -1)
      updateGradient(0, 0);
    else
      foregroundPaint.setColor(colorStart);

    foregroundPaint.setStyle(Paint.Style.STROKE);
    foregroundPaint.setStrokeWidth(strokeWidth);

    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(textColor);
    textPaint.setTextSize(textSize);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int min = Math.min(width, height);
    setMeasuredDimension(min, min);
    if (gradient != null && (lastWidth != width || lastHeight != height)) {
      updateGradient(min, min);
      lastWidth = width;
      lastHeight = height;
      minSize = min;
    }
    rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
  }

  private void updateGradient(int width, int height) {
    gradient = new SweepGradient(width / 2.f, height / 2.f, colorStart, colorEnd);
    gradient.getLocalMatrix(gradientMatrix);
    gradientMatrix.setRotate(gradientStartingAngle);
    gradient.setLocalMatrix(gradientMatrix);
    foregroundPaint.setShader(gradient);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawOval(rectF, backgroundPaint);
    float angle = 360 * progress / max;
    canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);

    if (text != null && text.length() > 0) {
      float textWidth = textPaint.measureText(text);
      textPaint.getTextBounds("a", 0, 1, textBounds);
      canvas.drawText(text, (minSize - textWidth) / 2, (minSize + textBounds.height() / 2) / 2, textPaint);
    }
  }

  /**
   * Set progress in minutes
   *
   * @param progress integer
   */
  public void setProgress(int progress) {
    this.progress = progress;
    int hours = this.progress / 60;
    int minutes = (this.progress - hours * 60);

    this.text = String.format(getContext().getString(R.string.format_time_holder), hours, minutes);
    invalidate();
  }

  public void setMax(int max) {
    this.max = max;
    invalidate();
  }

}
