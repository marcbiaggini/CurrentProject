package com.rockspoon.rockpos.Camera;

import android.graphics.Bitmap;

import com.rockspoon.rockandui.Objects.PhotoFilters.Filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * PhotoFilter.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 2/17/16.
 */
@RequiredArgsConstructor
@Getter
public class PhotoFilter {
  private final String name;
  private final Filter filter;

  @Setter
  private Bitmap previewImage;

  public Bitmap applyFilter(Bitmap image) {
    if (filter != null) {
      return filter.transform(image);
    } else {
      return image;
    }
  }
}
