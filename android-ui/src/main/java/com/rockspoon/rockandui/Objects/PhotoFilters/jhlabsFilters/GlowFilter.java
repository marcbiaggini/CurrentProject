/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.rockspoon.rockandui.Objects.PhotoFilters.JhlabsFilters;

import com.rockspoon.rockandui.utils.PixelUtil;

/**
 * A filter which adds Gaussian blur to an image, producing a glowing effect.
 *
 * @author Jerry Huxtable
 */
public class GlowFilter extends GaussianFilter {

  private float amount = 0.5f;

  public GlowFilter() {
    radius = 2;
  }

  /**
   * Get the amount of glow.
   *
   * @return the amount
   * @see #setAmount
   */
  public float getAmount() {
    return amount;
  }

  /**
   * Set the amount of glow.
   *
   * @param amount the amount
   * @min-value 0
   * @max-value 1
   * @see #getAmount
   */
  public void setAmount(float amount) {
    this.amount = amount;
  }

  public int[] filter(int[] src, int w, int h) {
    int[] inPixels = new int[w * h];
    int[] outPixels = new int[w * h];

    inPixels = src;

    if (radius > 0) {
      convolveAndTranspose(kernel, inPixels, outPixels, w, h, alpha, alpha && premultiplyAlpha, false, CLAMP_EDGES);
      convolveAndTranspose(kernel, outPixels, inPixels, h, w, alpha, false, alpha && premultiplyAlpha, CLAMP_EDGES);
    }

    outPixels = src;
    float a = 4 * amount;
    int index = 0;

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int rgb1 = outPixels[index];
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;

        int rgb2 = inPixels[index];
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;

        r1 = PixelUtil.clamp((int) (r1 + a * r2));
        g1 = PixelUtil.clamp((int) (g1 + a * g2));
        b1 = PixelUtil.clamp((int) (b1 + a * b2));

        inPixels[index] = (rgb1 & 0xff000000) | (r1 << 16) | (g1 << 8) | b1;
        index++;
      }
    }

    return inPixels;
  }

  public String toString() {
    return "Blur/Glow...";
  }
}
