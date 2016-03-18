package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rockspoon.rockandui.Interfaces.OnSwipe;
import com.rockspoon.rockandui.Interfaces.OnSwipeButtonClick;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 20/07/15.
 * <p>
 * TODO: Partial Swipe Animation
 */
public class SwipeItem extends LinearLayout {

  private final int ANIMATION_DURATION;

  private final List<View> leftButtons = new ArrayList<>();
  private final List<View> rightButtons = new ArrayList<>();

  private View content;
  private GestureDetector gestureDetector;
  private SwipeListener swipeListener;
  private OnSwipe swipeAction;
  private OnSwipeButtonClick onClick;
  private ViewTreeObserver vto;

  private int leftSize = 0;
  private int rightSize = 0;

  public SwipeItem(final Context ctx) {
    this(ctx, null);
  }

  public SwipeItem(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public SwipeItem(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
    ANIMATION_DURATION = ctx.getResources().getInteger(R.integer.swipeitem_anim_speed);
    if (attrs != null) {
      final TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeItem, 0, 0);
      int centerRes = a.getResourceId(R.styleable.SwipeItem_baselayout, 0);
      if (centerRes != 0)
        content = inflate(ctx, centerRes, null);
      else
        content = new LinearLayout(ctx);
      addView(content);
      a.recycle();
    } else {
      content = new LinearLayout(ctx);
      addView(content);
    }
  }

  private void swipeBar(boolean left) {
    if (rightSize > 0 || leftSize > 0) {
      final int leftMargin = ((RelativeLayout.LayoutParams) SwipeItem.this.getLayoutParams()).leftMargin;
      final int offsetTo = (left) ? (leftMargin < -leftSize) ? -leftSize : -leftSize - rightSize : ((leftMargin < 0) ? 0 : -leftSize);

      final Animation a = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
          RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) SwipeItem.this.getLayoutParams();
          final float delta = (offsetTo - params.leftMargin) * interpolatedTime;
          params.leftMargin += delta;
          SwipeItem.this.setLayoutParams(params);
        }
      };
      a.setDuration(ANIMATION_DURATION);
      a.setFillAfter(true);
      SwipeItem.this.startAnimation(a);
    }
  }

  public void initSwipe(Context ctx) {
    setOrientation(LinearLayout.HORIZONTAL);

    swipeAction = new OnSwipe() {
      @Override
      public void onSwipeLeft() {
        swipeBar(true);
      }

      @Override
      public void onSwipeRight() {
        swipeBar(false);
      }
    };

    swipeListener = new SwipeListener(ctx, swipeAction);
    gestureDetector = new GestureDetector(getContext(), swipeListener);

    vto = ((View) this.getParent()).getViewTreeObserver();
    vto.addOnGlobalLayoutListener(() -> {
      int parentWidth = ((View) SwipeItem.this.getParent()).getWidth();
      if (content.getLayoutParams().width != parentWidth) {
        content.getLayoutParams().width = parentWidth;
        content.requestLayout();
      }
    });
  }

  private void updateSizes() {
    leftSize = 0;
    rightSize = 0;

    for (final View v : leftButtons) {
      final LinearLayout.LayoutParams p = ((LinearLayout.LayoutParams) v.getLayoutParams());
      leftSize += p.width + p.leftMargin + p.rightMargin;
    }

    for (final View v : rightButtons) {
      final LinearLayout.LayoutParams p = ((LinearLayout.LayoutParams) v.getLayoutParams());
      rightSize += p.width + p.leftMargin + p.rightMargin;
    }

    leftSize *= 2;
    rightSize *= 2;

    final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) SwipeItem.this.getLayoutParams();
    params.leftMargin = -leftSize;
    SwipeItem.this.setLayoutParams(params);
    SwipeItem.this.requestLayout();
  }

  private void resetSwipe() {
    final int offsetTo = -leftSize;
    final Animation a = new Animation() {
      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) SwipeItem.this.getLayoutParams();
        final float delta = (offsetTo - params.leftMargin) * interpolatedTime;
        params.leftMargin += delta;
        SwipeItem.this.setLayoutParams(params);
      }
    };
    a.setDuration(ANIMATION_DURATION);
    a.setFillAfter(true);
    SwipeItem.this.startAnimation(a);
  }

  public void setOnButtonClickListener(OnSwipeButtonClick listener) {
    onClick = listener;
  }

  public void addLeftView(final View v) {
    final int pos = leftButtons.size();
    addView(v, pos);
    leftButtons.add(v);

    v.setOnClickListener((v2) -> {
      if (onClick != null)
        onClick.onClick(v2, pos, true, SwipeItem.this);
      resetSwipe();
    });

    updateSizes();
  }

  public void addRightView(final View v) {
    final int pos = rightButtons.size();
    addView(v, leftButtons.size() + 1 + pos);
    rightButtons.add(v);

    v.setOnClickListener((v2) -> {
      if (onClick != null)
        onClick.onClick(v2, pos, false, SwipeItem.this);
      resetSwipe();
    });

    updateSizes();
  }

  public void setContent(final View v) {
    if (content != null)
      removeView(content);
    content = v;
    addView(content);

    content.setOnTouchListener((v2, event) -> true);

    int parentWidth = ((View) SwipeItem.this.getParent()).getWidth();
    if (content.getLayoutParams().width != parentWidth) {
      content.getLayoutParams().width = parentWidth;
      content.requestLayout();
    }
  }

  public View getContent() {
    return content;
  }

  public void setContent(int resource) {
    content = inflate(getContext(), resource, null);
    setContent(content);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    final int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN)
      swipeListener.updateMotion((int) event.getX(), (int) event.getY());

    final int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    final int x = (int) event.getX();
    final int y = (int) event.getY();
    final int diffX = Math.abs(x - swipeListener.getLastMotionX());
    final int diffY = Math.abs(y - swipeListener.getLastMotionY());
    final boolean isSwipingSideways = diffX > scaledTouchSlop && diffX > diffY;

    return (action == MotionEvent.ACTION_MOVE && isSwipingSideways);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return gestureDetector.onTouchEvent(ev);
  }
}
