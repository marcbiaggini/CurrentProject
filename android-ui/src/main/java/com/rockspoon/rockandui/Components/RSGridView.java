package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Eugen K. on 2/18/16.
 */
public class RSGridView extends GridView {


  public interface OnChangeListener {
    public void onChange(int from, int to);
  }


  private boolean isDrag;
  private android.os.Vibrator vibrator;
  private View startDragItemView;
  private Bitmap dragBitmap;
  private int downX;
  private int downY;
  private int moveY;
  private int moveX;
  private int speed;

  private Handler mHandler = new Handler();
  private WindowManager.LayoutParams windowLayoutParams;
  private ImageView dragImageView;
  private WindowManager windowManager;
  private int point2ItemLeft;
  private int offset2Left;
  private int point2ItemTop;
  private int offset2Top;
  private int statusHeight;
  private OnChangeListener onChanageListener;
  private long dragResponseMS;
  private int dragPosition;
  private int downScrollBorder;
  private int upScrollBorder;

  public RSGridView(Context context) {
    this(context, null);
  }

  public RSGridView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RSGridView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightSpec;
    heightSpec = getLayoutParams().height == LayoutParams.WRAP_CONTENT ? MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST) : heightMeasureSpec;
    super.onMeasure(widthMeasureSpec, heightSpec);
  }

}
