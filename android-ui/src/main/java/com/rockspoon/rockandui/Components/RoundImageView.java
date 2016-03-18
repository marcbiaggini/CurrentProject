package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.rockspoon.rockandui.Tools;

/**
 * Created by lucas on 27/06/15.
 */
public class RoundImageView extends ImageView {

  public RoundImageView(final Context ctx) {
    this(ctx, null);
  }

  public RoundImageView(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public RoundImageView(final Context ctx, final AttributeSet attrs, int defStyle) {
    super(ctx, attrs, defStyle);
  }

  @Override
  protected void onDraw(Canvas canvas) {

    final Drawable drawable = getDrawable();

    if (canvas == null) {
      return;
    }

    if (drawable == null) {
      return;
    }

    if (getWidth() == 0 || getHeight() == 0) {
      return;
    }

    final Bitmap bitmap = BitmapDrawable.class.cast(drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
    canvas.drawBitmap(Tools.getRoundCroppedBitmap(bitmap, getWidth()), 0, 0, null);
  }

}