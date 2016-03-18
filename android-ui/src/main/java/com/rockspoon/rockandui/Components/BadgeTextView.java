package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 07/07/15.
 */
public class BadgeTextView extends TextView {
  private final int OVERHEAD;

  private LinearGradient rsGradient;
  private Paint backgroundPaint;

  public BadgeTextView(final Context ctx) {
    this(ctx, null, 0);
  }

  public BadgeTextView(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public BadgeTextView(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
    OVERHEAD = ctx.getResources().getInteger(R.integer.badgetextview_overhead);
    setupBackgroundPaint();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (widthMeasureSpec < heightMeasureSpec) {
      widthMeasureSpec = heightMeasureSpec;
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension((getMeasuredWidth() > getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight()) + OVERHEAD, getMeasuredHeight());
  }

  @Override
  protected void onDraw(Canvas canvas) {
    setupGradient();

    final int height = getMeasuredHeight() - getExtendedPaddingTop() - getExtendedPaddingBottom();

    canvas.save();
    canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
    canvas.drawCircle(height / 2, height / 2, height / 2, backgroundPaint);
    canvas.restore();

    canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop() + getLayout().getHeight() / 8);

    final Paint paint = getPaint();
    paint.setColor(getCurrentTextColor());

    getLayout().draw(canvas);
  }

  private void setupBackgroundPaint() {
    backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    backgroundPaint.setFilterBitmap(true);
    backgroundPaint.setDither(true);
    backgroundPaint.setColor(Color.parseColor("#FF0000"));
    backgroundPaint.setShader(rsGradient);
  }

  private void setupGradient() {
    if (rsGradient == null) {
      rsGradient = new LinearGradient(0, 0, 0, getHeight(),
          new int[]{
              ContextCompat.getColor(getContext(), R.color.rsgradient_red),
              ContextCompat.getColor(getContext(), R.color.rsgradient_magenta),
              ContextCompat.getColor(getContext(), R.color.rsgradient_magenta)
          },
          new float[]{0.f, 0.27f, 1.f},
          Shader.TileMode.MIRROR);
      backgroundPaint.setShader(rsGradient);
    }
  }

}
