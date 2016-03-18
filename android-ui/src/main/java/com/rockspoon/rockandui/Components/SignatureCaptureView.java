package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * SignatureCaptureView.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/21/16.
 */
public class SignatureCaptureView extends View {

  private static final float TOUCH_TOLERANCE = 2.0f;
  private static final float LINE_THICKNESS = 2.0f;

  private Bitmap bitmap;
  private Canvas canvas;
  private Path path;
  private Paint bitmapPaint;
  private Paint paint;
  private float touchX;
  private float touchY;
  private int fillColor;
  private int lineColor;

  public SignatureCaptureView(Context context) {
    this(context, null, 0);
  }

  public SignatureCaptureView(Context context, AttributeSet attr) {
    this(context, attr, 0);
  }

  public SignatureCaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    fillColor = Color.TRANSPARENT;
    lineColor = Color.BLACK;

    path = new Path();
    bitmapPaint = new Paint(Paint.DITHER_FLAG);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setDither(true);
    paint.setColor(lineColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(LINE_THICKNESS);
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);

    if (bitmap == null) {
      int bitmapHeight = height > 0 ? height : ((View) this.getParent()).getHeight();
      bitmap = Bitmap.createBitmap(width, bitmapHeight, Bitmap.Config.ARGB_8888);
    }

    canvas = new Canvas(bitmap);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(fillColor);
    canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
    canvas.drawPath(path, paint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    super.onTouchEvent(e);
    float x = e.getX();
    float y = e.getY();

    switch (e.getAction()) {
      case MotionEvent.ACTION_DOWN:
        touchDown(x, y);
        invalidate();
        break;
      case MotionEvent.ACTION_MOVE:
        touchMove(x, y);
        invalidate();
        break;
      case MotionEvent.ACTION_UP:
        touchUp();
        invalidate();
        break;
    }

    return true;
  }

  private void touchDown(float x, float y) {
    path.reset();
    path.moveTo(x, y);
    touchX = x;
    touchY = y;
  }

  private void touchMove(float x, float y) {
    float dx = Math.abs(x - touchX);
    float dy = Math.abs(y - touchY);

    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
      path.quadTo(touchX, touchY, (x + touchX) / 2, (y + touchY) / 2);
      touchX = x;
      touchY = y;
    }
  }

  private void touchUp() {
    if (!path.isEmpty()) {
      path.lineTo(touchX, touchY);
      canvas.drawPath(path, paint);
    } else {
      canvas.drawPoint(touchX, touchY, paint);
    }

    path.reset();
  }

  public void clearView() {
    canvas.drawColor(fillColor, PorterDuff.Mode.CLEAR);
    invalidate();
  }

  public int getFillColor() {
    return fillColor;
  }

  public void setFillColor(int fillColor) {
    this.fillColor = fillColor;
    invalidate();
  }

  public int getLineColor() {
    return lineColor;
  }

  public void setLineColor(int lineColor) {
    this.lineColor = lineColor;
    paint.setColor(lineColor);
    invalidate();
  }

  public byte[] getSignatureAsBytes() {
    Bitmap b = getSignatureAsBitmap();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    b.compress(Bitmap.CompressFormat.JPEG, 90, baos);
    return baos.toByteArray();
  }

  public Bitmap getSignatureAsBitmap() {
    View rootView = (View) this.getParent();
    Bitmap b = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    rootView.layout(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom());
    rootView.draw(c);

    return b;
  }

  public void setSignature(Bitmap signatureBitmap) {
    bitmap = signatureBitmap;
  }

  public void setSignature(byte[] bytes) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inMutable = true;
    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
  }
}
