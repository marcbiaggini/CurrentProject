package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Objects.FloorPlanItemData;
import com.rockspoon.rockandui.R;

import java.util.HashSet;

public class DrawGridView extends View {
  private static final String TAG = "DrawGridView";

  public static final int ZOOM_MAX_CONSTANT = 3;
  public static final int ZOOM_MIN_CONSTANT = 1;
  private static final float SCALE_FACTOR_CONSTANT_INCREASE = 0.25f;
  private static final float SCALE_FACTOR_CONSTANT_DECREASE = -0.25f;
  private static final int BITMAP_OBJECTS_COUNT = 4;

  private Bitmap[] objectsBitmap = new Bitmap[BITMAP_OBJECTS_COUNT];
  private static final int[] bitmapResIds = {R.drawable.vec_table_first, R.drawable.vec_table_second,
      R.drawable.vec_table_third, R.drawable.vec_table_fouth};

  private Rect measuredRect;
  private HScroll horizontalScroll;
  private VScroll verticalScroll;

  private float touchX, touchY;
  float curTouchX, curTouchY;

  private HashSet<CircleArea> circles = new HashSet<>();
  private SparseArray<CircleArea> circlePointer = new SparseArray<>();
  private float scaleFactor = 1.f;

  public DrawGridView(final Context ct) {
    super(ct);

    init(ct);
  }

  public DrawGridView(final Context ct, final AttributeSet attrs) {
    super(ct, attrs);

    init(ct);
  }

  public DrawGridView(final Context ct, final AttributeSet attrs, final int defStyle) {
    super(ct, attrs, defStyle);

    init(ct);
  }

  private void init(final Context ct) {
    Drawable drawable;

    for (int index = 0; index < BITMAP_OBJECTS_COUNT; index++) {
      drawable = ContextCompat.getDrawable(ct, bitmapResIds[index]);
      objectsBitmap[index] = BitmapTools.drawableToBitmap(drawable);
    }
  }

  public void addNewObject(float x, float y, FloorPlanItemData item) {
    CircleArea touchedCircle = new CircleArea((int) (x / scaleFactor), (int) (y / scaleFactor), item);

    circles.add(touchedCircle);
    invalidate();
  }

  @Override
  public void onDraw(final Canvas canvas) {
    canvas.scale(scaleFactor, scaleFactor);

    for (CircleArea circle : circles) {
      canvas.drawBitmap(objectsBitmap[circle.itemData.spotShape.ordinal()], null, circle.getRect(), null);
    }
  }

  @Override
  public boolean onTouchEvent(final MotionEvent event) {
    CircleArea touchedCircle;
    int xTouch;
    int yTouch;
    int pointerId;

    getParent().requestDisallowInterceptTouchEvent(true);

    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN:
        Log.w(TAG, "Down");
        xTouch = (int) event.getX();
        yTouch = (int) event.getY();

        // check if we've touched inside some circle
        touchedCircle = getTouchedCircle(xTouch, yTouch);
        if (touchedCircle != null) {
          touchedCircle.centerX = (int) (xTouch / scaleFactor);
          touchedCircle.centerY = (int) (yTouch / scaleFactor);
          circlePointer.put(event.getPointerId(0), touchedCircle);

          invalidate();
        } else {
          touchX = event.getRawX();
          touchY = event.getRawY();
        }

        break;
      case MotionEvent.ACTION_MOVE:
        Log.w(TAG, "Move");
        pointerId = event.getPointerId(0);

        xTouch = (int) event.getX();
        yTouch = (int) event.getY();

        touchedCircle = circlePointer.get(pointerId);

        if (touchedCircle != null) {
          touchedCircle.centerX = (int) (xTouch / scaleFactor);
          touchedCircle.centerY = (int) (yTouch / scaleFactor);
          invalidate();
        } else {
          curTouchX = event.getRawX();
          curTouchY = event.getRawY();
          verticalScroll.scrollBy((int) (touchX - curTouchX), (int) (touchY - curTouchY));
          horizontalScroll.scrollBy((int) (touchX - curTouchX), (int) (touchY - curTouchY));
          touchX = curTouchX;
          touchY = curTouchY;
        }

        break;
      case MotionEvent.ACTION_UP:
        if (circlePointer.size() > 0) {
          circlePointer.clear();
          invalidate();
        } else {
          curTouchX = event.getRawX();
          curTouchY = event.getRawY();
          verticalScroll.scrollBy((int) (touchX - curTouchX), (int) (touchY - curTouchY));
          horizontalScroll.scrollBy((int) (touchX - curTouchX), (int) (touchY - curTouchY));
        }

        break;
      default:
        break;
    }

    return true;
  }

  private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
    CircleArea touched = null;

    for (CircleArea circle : circles) {
      if (circle.rect.left <= xTouch / scaleFactor && circle.rect.top <= yTouch / scaleFactor && circle.rect.right >= xTouch / scaleFactor && circle.rect.bottom >= yTouch / scaleFactor) {
        touched = circle;
        break;
      }
    }

    return touched;
  }

  public void increaseZoom() {
    changeZoom(SCALE_FACTOR_CONSTANT_INCREASE);
  }

  public void decreaseZoom() {
    changeZoom(SCALE_FACTOR_CONSTANT_DECREASE);
  }

  public void changeZoom(float scaleChange) {
    float newScale = scaleFactor + scaleChange;
    if (newScale >= ZOOM_MIN_CONSTANT && newScale <= ZOOM_MAX_CONSTANT) {
      applyZoom(newScale);
    }
  }

  public void setZoom(float zoom) {
    zoom = zoom > ZOOM_MAX_CONSTANT ? ZOOM_MAX_CONSTANT : zoom;
    zoom = zoom < ZOOM_MIN_CONSTANT ? ZOOM_MIN_CONSTANT : zoom;
    applyZoom(zoom);
  }

  private void applyZoom(float zoom) {
    scaleFactor = zoom;
    if (measuredRect == null) {
      measuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
    ViewGroup.LayoutParams params = getLayoutParams();
    params.height = (int) (measuredRect.height() * scaleFactor);
    params.width = (int) (measuredRect.width() * scaleFactor);
    requestLayout();
    invalidate();
  }

  public void setHorizontalScroll(HScroll horizontalScroll) {
    this.horizontalScroll = horizontalScroll;
  }

  public void setVerticalScroll(VScroll verticalScroll) {
    this.verticalScroll = verticalScroll;
  }

  private static class CircleArea {
    int centerX;
    int centerY;
    FloorPlanItemData itemData;
    Rect rect;

    CircleArea(int centerX, int centerY, FloorPlanItemData itemData) {
      rect = new Rect();
      this.centerX = centerX;
      this.centerY = centerY;
      rect.set(centerX - 25, centerY - 25, centerX + 25, centerY + 25);
      this.itemData = itemData;
    }

    public Rect getRect() {
      rect.set(centerX - 25, centerY - 25, centerX + 25, centerY + 25);
      return rect;
    }
  }
}