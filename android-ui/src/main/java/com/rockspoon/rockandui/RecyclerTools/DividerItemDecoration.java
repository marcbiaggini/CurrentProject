package com.rockspoon.rockandui.RecyclerTools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Yury Minich on 1/14/16.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  @IntDef({VERTICAL, HORIZONTAL})
  @Retention(RetentionPolicy.SOURCE) @interface ListOrientation {
  }

  public static final int VERTICAL = LinearLayoutManager.VERTICAL;
  public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;

  private static final int[] ATTRS = new int[]{
      android.R.attr.listDivider
  };

  private Drawable divider;

  private int dividerHeightOffset = 0;
  private int orientation;

  public DividerItemDecoration(Context context, @ListOrientation int orientation) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    divider = a.getDrawable(0);
    a.recycle();
    setOrientation(orientation);
  }

  public DividerItemDecoration(Context context, @ListOrientation int orientation, @DimenRes int offsetRes) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    divider = a.getDrawable(0);
    a.recycle();
    setOrientation(orientation);
    dividerHeightOffset = context.getResources().getDimensionPixelOffset(offsetRes);
  }

  public void setOrientation(@ListOrientation int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("invalid orientation");
    }
    this.orientation = orientation;
  }


  public void setDividerHeightOffset(int dividerHeightOffset) {
    this.dividerHeightOffset = dividerHeightOffset;
  }

  @Override
  public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
    if (orientation == VERTICAL) {
      outRect.set(0, 0, 0, divider.getIntrinsicHeight() + dividerHeightOffset);
    } else {
      outRect.set(0, 0, divider.getIntrinsicWidth() + dividerHeightOffset, 0);
    }
  }
}
