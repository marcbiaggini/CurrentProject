package com.rockspoon.rockandui.Objects.PhotoFilters;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.CurvesFilter;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.Support.Curve;

/**
 * Created by mordonez on 1/14/14.
 */
public class Amaro extends Filter {

  public Amaro() {

  }

  @Override
  public Bitmap transform(Bitmap image) {
    width = image.getWidth();
    height = image.getHeight();

    mColors = BitmapTools.bitmapToIntArray(image);

    mColors = getLevelsFilter().filter(mColors, width, height);

    levels = Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);

    Bitmap gradient = createRadialGradient();

    return combineGradientAndImage(gradient, levels, PorterDuff.Mode.OVERLAY);
  }

  private CurvesFilter getLevelsFilter() {
        /*This method contains Photoshop values, since values are 0 to 1 range we need to
        find the value based on the scale (that's the reason of for loops).*/

    final CurvesFilter curvesFilter = new CurvesFilter();

    Curve[] curves = new Curve[]{new Curve(), new Curve(), new Curve()};

    // Red
    curves[0].y = new float[]{26.0f, 43.f, 64f, 114f, 148f, 177f, 193f, 202f, 208f, 218f, 229f, 241f, 251f};
    curves[0].x = new float[]{0f, 15f, 32f, 65f, 83f, 109f, 127f, 146f, 170f, 195f, 215f, 234f, 255f};

    // Green
    curves[1].y = new float[]{0f, 32f, 72f, 123f, 147f, 188f, 205f, 210f, 222f, 224f, 235f, 246f, 255f};
    curves[1].x = new float[]{0f, 26f, 49f, 72f, 89f, 115f, 147f, 160f, 177f, 189f, 215f, 234f, 255f};

    // Blue
    curves[2].y = new float[]{29f, 72f, 124f, 147f, 162f, 175f, 184f, 189f, 195f, 203f, 216f, 237f, 247f};
    curves[2].x = new float[]{1f, 30f, 57f, 74f, 87f, 108f, 130f, 152f, 172f, 187f, 215f, 239f, 255f};

    for (Curve curve : curves) {
      for (int i = 0; i < curve.x.length; i++) {
        curve.x[i] = (curve.x[i] * 100) / 255 / 100;
        curve.y[i] = (curve.y[i] * 100) / 255 / 100;
      }
    }

    curvesFilter.setCurves(curves);
    return curvesFilter;
  }

}
