package com.rockspoon.rockandui.Objects.PhotoFilters;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.ContrastFilter;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.HSBAdjustFilter;
import com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters.LevelsFilter;

/**
 * Created by mordonez on 1/14/14.
 */
public class EarlyBird extends Filter {

  public EarlyBird() {

  }

  @Override
  public Bitmap transform(Bitmap image) {
    width = image.getWidth();
    height = image.getHeight();
    mColors = BitmapTools.bitmapToIntArray(image);

    Bitmap softLayer = createSoftLayer();

    Bitmap fusionSoftLayer = fusionSoftLayer(image, softLayer);

    // These operations drastically reduce performance
//    Bitmap tonesLayer = changeSaturationAndTone(fusionSoftLayer);
//
//    Bitmap brightnessContrastLayer = changeContrastAndBrightness(tonesLayer);

    Bitmap levels = changeLevels(fusionSoftLayer);

    Bitmap sepia = setSepiaColorFilter(levels);

    Bitmap gradient = createRadialGradient();

    return combineGradientAndImage(adjustOpacity(gradient, 100), sepia, PorterDuff.Mode.DARKEN);
  }

  private Bitmap createSoftLayer() {
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.eraseColor(0xFFFCF3D6);
    return bitmap;
  }

  private Bitmap fusionSoftLayer(Bitmap image, Bitmap layer) {
    Bitmap result = image.copy(Bitmap.Config.ARGB_8888, true);

    Paint paint = new Paint();
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    paint.setShader(new BitmapShader(layer, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

    Canvas canvas = new Canvas();
    canvas.setBitmap(result);
    canvas.drawBitmap(image, 0, 0, null);
    canvas.drawRect(0, 0, image.getWidth(), image.getHeight(), paint);

    return result;
  }

  private Bitmap changeSaturationAndTone(Bitmap image) {
    mColors = BitmapTools.bitmapToIntArray(image);

    HSBAdjustFilter hsbFilter = new HSBAdjustFilter();
    hsbFilter.setSFactor(-0.03f);
    hsbFilter.setBFactor(0.01f);

    mColors = hsbFilter.filter(mColors, width, height);

    return Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);
  }

  private Bitmap changeContrastAndBrightness(Bitmap image) {
    mColors = BitmapTools.bitmapToIntArray(image);

    ContrastFilter contrastFilter = new ContrastFilter();
    contrastFilter.setBrightness(1.2f);
    contrastFilter.setContrast(1.1f);

    mColors = contrastFilter.filter(mColors, width, height);

    return Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);
  }

  private Bitmap changeLevels(Bitmap image) {
    mColors = BitmapTools.bitmapToIntArray(image);

    LevelsFilter levelsFilter = new LevelsFilter();
    float highLevelValue = (237 * 100 / 255) * 100 / 100;
    levelsFilter.setHighLevel(highLevelValue / 100);
    levelsFilter.setLowLevel(0.086f);

    mColors = levelsFilter.filter(mColors, width, height);

    return Bitmap.createBitmap(mColors, 0, width, width, height, Bitmap.Config.ARGB_8888);
  }
}
