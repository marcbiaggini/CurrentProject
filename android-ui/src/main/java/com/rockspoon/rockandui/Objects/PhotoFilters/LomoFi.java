package com.rockspoon.rockandui.Objects.PhotoFilters;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.CurvesFilter;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.GrayscaleFilter;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.Support.Curve;

/**
 * Created by mordonez on 1/14/14.
 */
public class LomoFi extends Filter {

  @Override
  public Bitmap transform(Bitmap image) {
    width = image.getWidth();
    height = image.getHeight();

    Bitmap curves = changeCurves(image);
    Bitmap desaturatedImage = desaturateImage(image);

    return combineWithOverlay(desaturatedImage, curves);
  }

  private Bitmap changeCurves(Bitmap image) {
    mColors = BitmapTools.bitmapToIntArray(image);

    final CurvesFilter curvesFilter = new CurvesFilter();

    Curve[] curves = new Curve[]{new Curve(), new Curve(), new Curve()};

    // Red
    curves[0].y = new float[]{0f, 47.0f, 206.f, 255f};
    curves[0].x = new float[]{0f, 62f, 189f, 255f};

    // Green
    curves[1].y = new float[]{0f, 61f, 199f, 255f};
    curves[1].x = new float[]{0f, 75f, 187f, 255f};

    // Blue
    curves[2].y = new float[]{0f, 66f, 191f, 255f};
    curves[2].x = new float[]{0f, 58f, 200f, 255f};

    for (Curve curve : curves) {
      for (int i = 0; i < curve.x.length; i++) {
        curve.x[i] = (curve.x[i] * 100) / 255 / 100;
        curve.y[i] = (curve.y[i] * 100) / 255 / 100;
      }
    }

    curvesFilter.setCurves(curves);
    mColors = curvesFilter.filter(mColors, width, height);

    return Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);
  }

  private Bitmap desaturateImage(Bitmap image) {
    Bitmap result = image.copy(Bitmap.Config.ARGB_8888, true);

    GrayscaleFilter grayscaleFilter = new GrayscaleFilter();

    mColors = BitmapTools.bitmapToIntArray(result);
    mColors = grayscaleFilter.filter(mColors, width, height);

    return Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);

  }

  protected Bitmap combineWithOverlay(Bitmap desaturated, Bitmap image) {
    Bitmap result = image.copy(Bitmap.Config.ARGB_8888, true);

    Paint p = new Paint();
    p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
    BitmapShader gradientShader = new BitmapShader(desaturated, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    p.setShader(gradientShader);

    Canvas c = new Canvas();
    c.setBitmap(result);
    c.drawBitmap(image, 0, 0, null);
    c.drawRect(0, 0, image.getWidth(), image.getHeight(), p);

    return result;
  }
}
