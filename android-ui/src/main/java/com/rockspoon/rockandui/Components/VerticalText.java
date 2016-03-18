package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by lucas on 27/06/15.
 */
public class VerticalText extends TextView {
  final boolean topDown;

  public VerticalText(final Context ctx) {
    this(ctx, null);
  }

  public VerticalText(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public VerticalText(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs);
    final int gravity = getGravity();
    if (Gravity.isVertical(gravity) && (gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM) {
      setGravity((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) | Gravity.TOP);
      topDown = false;
    } else
      topDown = true;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Intentionally inverted for vertical text
    super.onMeasure(heightMeasureSpec, widthMeasureSpec);
    setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
  }

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    TextPaint textPaint = getPaint();
    textPaint.setColor(getCurrentTextColor());
    textPaint.drawableState = getDrawableState();

    canvas.save();

    if (topDown) {
      canvas.translate(getWidth(), 0);
      canvas.rotate(90);
    } else {
      canvas.translate(0, getHeight());
      canvas.rotate(-90);
    }

    canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

    getLayout().draw(canvas);
    canvas.restore();
  }
}