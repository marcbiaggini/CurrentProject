package com.rockspoon.rockandui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.rockspoon.rockandui.Adapters.LinearGridViewAdapter;
import com.rockspoon.rockandui.Adapters.OrderItemSeatAdapter;
import com.rockspoon.rockandui.Interfaces.OnEditModeChangeListener;
import com.rockspoon.rockandui.Interfaces.OnLinearViewItemClick;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.Tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lucas on 13/07/15.
 * <p>
 * This is needed because we don't want to use GridView (that have Scroll) inside a ScrollView
 * <p>
 * FIXME: The HoverView is appearing on the first line, regardless of the original line :EMXIF
 */

public class LinearGridView extends GridLayout {

  private static final int NO_ITEM = -1;
  private static final int INVALID_POSITION = -1;
  private final String TAG = "LinearGridView";
  private final int OVERLAP;
  /**
   * Wobble Stuff
   */
  private final List<ObjectAnimator> wobbleAnimators = new LinkedList<>();
  private final Paint grayPainter = new Paint();
  private int ANIMATION_INTERVAL;
  private int itemHorizontalSpacing = 0;
  private int itemVerticalSpacing = 0;
  private int itemWidth = 0;
  private int itemHeight = 0;
  private int editNumColumns = 0;
  private LinearGridViewAdapter viewAdapter;
  private DataSetObserver dataObserver;
  private int columns = 3;
  private boolean editMode;
  private boolean editModeEnabled;
  private int tOffsetY = 0;
  private int tOffsetX = 0;
  private int activePointer = NO_ITEM;
  /**
   * Hover Bitmap
   */
  private BitmapDrawable hoverBitmap;
  private Rect currentHoverBounds;
  private Rect originalHoverBounds;
  private long mobileItemId = NO_ITEM;
  private boolean isMobile = false;
  private View mobileView;
  private boolean hoverAnimation;
  /**
   * Event data
   */

  private int downX = -1;
  private int downY = -1;
  private int lastEventY = -1;
  private int lastEventX = -1;
  private boolean reorderAnimation;
  private boolean stopAfterTouchEnd;
  private List<Long> ids = new ArrayList<>();
  /**
   * Listeners
   */
  private OnEditModeChangeListener editModeChangeListener;
  private OnLinearViewItemClick userItemClickListener;
  private final OnLinearViewItemClick localItemClickListener = new OnLinearViewItemClick() {
    @Override
    public void onItemClick(View v, int position, Object data) {
      Log.d(TAG, "onItemClick(" + position + ")");
      if (!isEditMode() && isEnabled() && userItemClickListener != null) {
        userItemClickListener.onItemClick(v, position, getAdapter().getItem(position));
      }
    }
  };

  public LinearGridView(final Context ctx) {
    this(ctx, null);
  }

  public LinearGridView(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public LinearGridView(final Context ctx, final AttributeSet attrs, int defStyle) {
    super(ctx, attrs, defStyle);
    ANIMATION_INTERVAL = ctx.getResources().getInteger(R.integer.lineargridview_anim_speed);
    OVERLAP = ctx.getResources().getDimensionPixelOffset(R.dimen.lineargridview_overlap);
    initializeComponent(ctx, attrs);
  }

  private void initializeComponent(final Context ctx, final AttributeSet attrs) {
    dataObserver = new DataSetObserver() {
      @Override
      public void onChanged() {
        super.onChanged();
        updateViews();
      }

      @Override
      public void onInvalidated() {
        super.onInvalidated();
      }
    };
    editMode = false;
    editModeEnabled = true;
    stopAfterTouchEnd = true;

    final ColorFilter filter = new PorterDuffColorFilter(Color.argb(50, 255, 255, 255), PorterDuff.Mode.MULTIPLY);
    grayPainter.setColorFilter(filter);

    if (attrs != null) {
      final TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.LinearGridView, 0, 0);
      try {
        editNumColumns = a.getInteger(R.styleable.LinearGridView_editNumColumns, -1);
        itemHorizontalSpacing = a.getDimensionPixelOffset(R.styleable.LinearGridView_itemHorizontalSpacing, 0);
        itemVerticalSpacing = a.getDimensionPixelOffset(R.styleable.LinearGridView_itemVerticalSpacing, 0);
        itemHeight = a.getDimensionPixelOffset(R.styleable.LinearGridView_itemHeight, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemWidth = a.getDimensionPixelOffset(R.styleable.LinearGridView_itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
      } finally {
        a.recycle();
      }
    }

  }

  private boolean isEditMode() {
    return editMode;
  }

  private void startEditMode(int pos) {
    if (!editModeEnabled)
      return;

    requestDisallowInterceptTouchEvent(true);
    startWobbleAnimation();

    if (pos != -1)
      startDragAtPosition(pos);

    editMode = true;

    if (editModeChangeListener != null)
      editModeChangeListener.onEditModeChanged(true);
  }

  private void stopEditMode() {
    editMode = false;
    requestDisallowInterceptTouchEvent(false);

    stopWobble(true);

    if (editModeChangeListener != null)
      editModeChangeListener.onEditModeChanged(false);
  }

  public boolean isEditModeEnabled() {
    return editModeEnabled;
  }

  public void setEditModeEnabled(boolean enabled) {
    this.editModeEnabled = enabled;
  }

  public void setOnEditModeChangeListener(OnEditModeChangeListener editModeChangeListener) {
    this.editModeChangeListener = editModeChangeListener;
  }

  public int getColumns() {
    return columns;
  }

  public void setColumns(int columns) {
    this.columns = columns;
    setColumnCount(columns);
    updateViews();
  }

  private void updateViews() {

    if (viewAdapter != null) {
      final int items = viewAdapter.getCount();

      if (getChildCount() > items) {
        for (int i = items; i < getChildCount(); i++) {
          LinearLayout line = (LinearLayout) getChildAt(i);
          line.setVisibility(GONE); //  Lets recycle in future
          for (int z = 0; z < line.getChildCount(); z++)
            line.getChildAt(z).setVisibility(GONE);
        }
      }

      for (int i = 0; i < items; i++) {
        final int x = i % columns;
        final int y = i / columns;
        final int position = i;

        final LayoutParams baseLayout = new LayoutParams(new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        baseLayout.setMargins(0, 0, (x == columns - 1) ? 0 : itemHorizontalSpacing, itemVerticalSpacing);
        baseLayout.rowSpec = GridLayout.spec(y);
        baseLayout.columnSpec = GridLayout.spec(x);
        baseLayout.width = itemWidth;
        baseLayout.height = itemHeight;

        View v = (i < getChildCount()) ? getChildAt(i) : null;
        v = viewAdapter.getView(i, v, this);
        v.setVisibility(VISIBLE);

        if (v.getLayoutParams() != null) {
          final LayoutParams vParams = (LayoutParams) v.getLayoutParams();
          vParams.setMargins(0, 0, (x == columns - 1) ? 0 : itemHorizontalSpacing, itemVerticalSpacing);
          vParams.rowSpec = GridLayout.spec(y);
          vParams.columnSpec = GridLayout.spec(x);
        } else
          v.setLayoutParams(baseLayout);

        if (i >= getChildCount())
          addView(v);
        else if (getChildAt(i) != v) {
          removeView(getChildAt(i));
          addView(v, i);
        }

        v.setOnTouchListener((v2, event) -> {
          return onTouchEvent(event);
        });

        v.setOnLongClickListener((v2) -> {
          if (editModeEnabled) {
            startEditMode(position);
            return true;
          } else
            return false;
        });

        v.setOnClickListener((v2) -> {
          localItemClickListener.onItemClick(v2, position, getAdapter().getItem(position));
        });
      }
    }
  }

  public void setOnItemClickListener(OnLinearViewItemClick listener) {
    this.userItemClickListener = listener;
  }

  public LinearGridViewAdapter getAdapter() {
    return viewAdapter;
  }

  public void setAdapter(final LinearGridViewAdapter adapter) {
    if (this.viewAdapter != null)
      this.viewAdapter.unregisterDataSetObserver(dataObserver);
    this.viewAdapter = adapter;
    this.viewAdapter.registerDataSetObserver(dataObserver);
    setColumns(adapter.getColCount());
    updateViews();
  }

  public int getCount() {
    return viewAdapter.getCount();
  }

  private int getColCount() {
    return viewAdapter.getColCount();
  }

  private void reorderElements(int org, int target) {
    viewAdapter.reorderItems(org, target);
  }

  private Point getColumnAndRowForView(View view) {
    final int pos = getPositionForView(view),
        columns = getColCount(),
        column = pos % columns,
        row = pos / columns;
    return new Point(column, row);
  }

  public void setStopAfterDrag(boolean enable) {
    stopAfterTouchEnd = enable;
  }

  public boolean getStopAfterDrag(boolean enable) {
    return stopAfterTouchEnd;
  }

  /**
   * Maps a point to a position in the list.
   *
   * @param x X in local coordinate
   * @param y Y in local coordinate
   * @return The position of the item which contains the specified point, or
   * {@link #INVALID_POSITION} if the point does not intersect an item.
   */
  public int pointToPosition(int x, int y) {
    Rect frame = new Rect();
    final int lines = getChildCount();
    for (int yt = 0; yt < lines; yt++) {
      final LinearLayout line = (LinearLayout) getChildAt(yt);
      final int childs = line.getChildCount();
      for (int xt = 0; xt < childs; xt++) {
        final View child = line.getChildAt(xt);
        if (child.getVisibility() == VISIBLE) {
          child.getHitRect(frame);
          if (frame.contains(x, y))
            return yt * columns + xt;
        }
      }
    }
    return INVALID_POSITION;
  }

  /**
   * Events
   */

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        downX = (int) event.getX();
        downY = (int) event.getY();
        activePointer = event.getPointerId(0);
        if (editMode && isEnabled()) {
          final int position = pointToPosition(downX, downY);
          updateViews();
          startDragAtPosition(position);
        } else if (!isEnabled())
          return false;
        break;

      case MotionEvent.ACTION_MOVE:
        if (activePointer == NO_ITEM)
          break;

        int pointerIdx = event.findPointerIndex(activePointer);

        lastEventX = (int) event.getX(pointerIdx);
        lastEventY = (int) event.getY(pointerIdx);

        final int deltaX = lastEventX - downX;
        final int deltaY = lastEventY - downY;

        if (isMobile) {
          currentHoverBounds.offsetTo(originalHoverBounds.left + deltaX + tOffsetX, originalHoverBounds.top + deltaY + tOffsetY);
          hoverBitmap.setBounds(currentHoverBounds);
          invalidate();
          handleCellSwitch();
          return false;
        }
        break;

      case MotionEvent.ACTION_UP:
        touchEventsEnded();
        break;

      case MotionEvent.ACTION_CANCEL:
        touchEventsCancelled();
        break;

      case MotionEvent.ACTION_POINTER_UP:
        pointerIdx = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = event.getPointerId(pointerIdx);
        if (pointerId == activePointer)
          touchEventsEnded();
        break;

      default:
        break;
    }

    return super.onTouchEvent(event);
  }

  private void handleCellSwitch() {
    final int deltaY = lastEventY - downY;
    final int deltaX = lastEventX - downX;
    final int deltaYTotal = originalHoverBounds.centerY() + tOffsetY + deltaY;
    final int deltaXTotal = originalHoverBounds.centerX() + tOffsetX + deltaX;
    View targetView = null;
    float vX = 0;
    float vY = 0;
    mobileView = getViewForId(mobileItemId);
    final Point mobileColumnRowPair = getColumnAndRowForView(mobileView);
    for (final Long id : ids) {
      final View view = getViewForId(id);
      if (view != null) {
        Point targetColumnRowPair = getColumnAndRowForView(view);
        if ((Tools.aboveRight(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal < view.getBottom() && deltaXTotal > view.getLeft()
            || Tools.aboveLeft(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal < view.getBottom() && deltaXTotal < view.getRight()
            || Tools.belowRight(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal > view.getTop() && deltaXTotal > view.getLeft()
            || Tools.belowLeft(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal > view.getTop() && deltaXTotal < view.getRight()
            || Tools.above(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal < view.getBottom() - OVERLAP
            || Tools.below(targetColumnRowPair, mobileColumnRowPair)
            && deltaYTotal > view.getTop() + OVERLAP
            || Tools.right(targetColumnRowPair, mobileColumnRowPair)
            && deltaXTotal > view.getLeft() + OVERLAP
            || Tools.left(targetColumnRowPair, mobileColumnRowPair)
            && deltaXTotal < view.getRight() - OVERLAP)) {
          final float xDiff = Math.abs(Tools.getViewX(view) - Tools.getViewX(mobileView));
          final float yDiff = Math.abs(Tools.getViewY(view) - Tools.getViewY(mobileView));
          if (xDiff >= vX && yDiff >= vY) {
            vX = xDiff;
            vY = yDiff;
            targetView = view;
          }
        }
      }
    }
    if (targetView != null) {
      final int originalPosition = getPositionForView(mobileView);
      final int targetPosition = getPositionForView(targetView);

      if (targetPosition == INVALID_POSITION || !viewAdapter.canReorder(originalPosition) || !viewAdapter.canReorder(targetPosition)) {
        updateNeighborViewsForId(mobileItemId);
        return;
      }
      reorderElements(originalPosition, targetPosition);

      downY = lastEventY;
      downX = lastEventX;

      SwitchCellAnimator switchCellAnimator;

      switchCellAnimator = new KitKatSwitchCellAnimator(deltaX, deltaY);

      updateNeighborViewsForId(mobileItemId);

      switchCellAnimator.animateSwitchCell(originalPosition, targetPosition);
    } else
      Log.d(TAG, "Null VIEW!!!");
  }

  private void touchEventsEnded() {
    final View mobileView = getViewForId(mobileItemId);
    if (mobileView != null && (isMobile)) {
      isMobile = false;
      activePointer = NO_ITEM;

      currentHoverBounds.offsetTo(mobileView.getLeft(), mobileView.getTop());

      animateBounds(mobileView);

      if (stopAfterTouchEnd)
        stopEditMode();

    } else
      touchEventsCancelled();
  }

  private void touchEventsCancelled() {
    final View mobileView = getViewForId(mobileItemId);
    if (isMobile)
      reset(mobileView);

    isMobile = false;
    activePointer = NO_ITEM;
  }

  private void animateBounds(final View mobileView) {
    TypeEvaluator<Rect> sBoundEvaluator = new TypeEvaluator<Rect>() {
      public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
        return new Rect(interpolate(startValue.left, endValue.left, fraction),
            interpolate(startValue.top, endValue.top, fraction),
            interpolate(startValue.right, endValue.right, fraction),
            interpolate(startValue.bottom, endValue.bottom, fraction));
      }

      public int interpolate(int start, int end, float fraction) {
        return (int) (start + fraction * (end - start));
      }
    };

    final ObjectAnimator hoverViewAnimator = ObjectAnimator.ofObject(hoverBitmap, "bounds", sBoundEvaluator, currentHoverBounds);
    hoverViewAnimator.addUpdateListener((valueAnimator) -> {
      invalidate();
    });
    hoverViewAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        hoverAnimation = true;
        updateEnableState();
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        hoverAnimation = false;
        updateEnableState();
        reset(mobileView);
      }
    });
    hoverViewAnimator.start();
  }

  private void reset(View mobileView) {
    ids.clear();
    mobileItemId = NO_ITEM;
    if (mobileView != null)
      mobileView.setVisibility(View.VISIBLE);
    hoverBitmap = null;

    if (editMode)
      restartWobble();
    else
      stopWobble(true);

    for (int i = 0; i < getLastVisiblePosition() - getFirstVisiblePosition(); i++) {
      final View child = getChildAt(i);
      if (child != null)
        child.setVisibility(View.VISIBLE);
    }

    invalidate();
  }

  /**
   * GridView Compat
   */

  public int getFirstVisiblePosition() {
    return 0;
  }

  public int getLastVisiblePosition() {
    return viewAdapter.getCount() - 1;
  }

  public int getPositionForView(View v) {
    for (int y = 0; y < getChildCount(); y++) {
      LinearLayout line = (LinearLayout) getChildAt(y);
      for (int x = 0; x < line.getChildCount(); x++)
        if (line.getChildAt(x).equals(v)) return y * columns + x;
    }
    Log.wtf(TAG, "View not in any position!!!!");
    return -1;
  }

  private long getItemId(int position) {
    return viewAdapter.getItemId(position);
  }

  private void updateEnableState() {
    setEnabled(!hoverAnimation && !reorderAnimation);
  }

  /**
   * Drag Functions
   */

  private void startDragAtPosition(int position) {
    tOffsetY = 0;
    tOffsetX = 0;

    Log.d(TAG, "startDragAtPosition at " + position);
    final int x = position % columns,
        y = position / columns;

    final LinearLayout line = (LinearLayout) getChildAt(y);

    final View selectedView = line.getChildAt(x);
    if (selectedView != null) {
      mobileItemId = viewAdapter.getItemId(position);
      hoverBitmap = getHoverView(selectedView);

      //selectedView.setVisibility(View.INVISIBLE);
      isMobile = true;
      updateNeighborViewsForId(mobileItemId);
    }
  }

  private void updateNeighborViewsForId(long itemId) {
    ids.clear();

    int draggedPos = getPositionForID(itemId);
    for (int pos = 0; pos < viewAdapter.getCount(); pos++) {
      if (draggedPos != pos && viewAdapter.canReorder(pos))
        ids.add(getItemId(pos));
    }
  }

  public int getPositionForID(long itemId) {
    final View v = getViewForId(itemId);
    if (v == null)
      return -1;
    else
      return getPositionForView(v);
  }

  public View getViewForId(long itemId) {
    for (int i = 0; i < getChildCount(); i++) {
      LinearLayout line = (LinearLayout) getChildAt(i);
      for (int z = 0; z < line.getChildCount(); z++) {
        final long id = viewAdapter.getItemId(z + i * columns);
        if (id == itemId) return getChildAt(z);
      }
    }
    return null;
  }

  /**
   * Generators
   */

  private BitmapDrawable getHoverView(View v) {
    final int w = v.getWidth(),
        h = v.getHeight(),
        top = v.getTop(),
        left = v.getLeft();

    final Bitmap b = Tools.getScreenshot(v);
    final BitmapDrawable drawable = new BitmapDrawable(getResources(), b);

    originalHoverBounds = new Rect(left, top, left + w, top + h);
    currentHoverBounds = new Rect(originalHoverBounds);

    drawable.setBounds(currentHoverBounds);

    return drawable;
  }

  /**
   * Wobble Functions
   */

  @Override
  protected void dispatchDraw(@NonNull Canvas canvas) {
    if (!isEnabled()) {
      canvas.saveLayer(null, grayPainter, Canvas.ALL_SAVE_FLAG);
      super.dispatchDraw(canvas);
      canvas.restore();
    } else
      super.dispatchDraw(canvas);

    if (hoverBitmap != null)
      hoverBitmap.draw(canvas);
  }

  private void startWobbleAnimation() {
    Log.d(TAG, "startWobbleAnimation");
    for (int i = 0; i < getChildCount(); i++) {
      final LinearLayout line = (LinearLayout) getChildAt(i);
      Log.d(TAG, "startWobbleAnimation - Line " + line.getChildCount());
      for (int c = 0; c < line.getChildCount(); c++) {
        final View v = line.getChildAt(c);
        if (v != null && v.getTag(R.id.wobbletag) != Boolean.TRUE) {
          if (i % 2 == 0)
            animateWobble(v);
          else
            animateWobbleInverse(v);
          v.setTag(R.id.wobbletag, true);
        }
      }
    }
  }

  private void stopWobble(boolean resetRotation) {
    Log.d(TAG, "stopWobbleAnimation");
    for (final Animator wobbleAnimator : wobbleAnimators)
      wobbleAnimator.cancel();

    wobbleAnimators.clear();

    for (int i = 0; i < getChildCount(); i++) {
      final LinearLayout line = (LinearLayout) getChildAt(i);
      for (int c = 0; c < line.getChildCount(); c++) {
        final View v = line.getChildAt(c);
        if (v != null) {
          if (resetRotation) v.setRotation(0);
          v.setTag(R.id.wobbletag, false);
        }
      }
    }

    for (int i = 0; i < getChildCount(); i++) {
      View v = getChildAt(i);
      if (v != null) {
        if (resetRotation) v.setRotation(0);
        v.setTag(R.id.wobbletag, false);
      }
    }
  }

  public void animateWobble(View v) {
    Log.d(TAG, "animateWobble");
    ObjectAnimator animator = createBaseWobble(v);
    animator.setFloatValues(-2, 2);
    animator.start();
    wobbleAnimators.add(animator);
  }

  public void animateWobbleInverse(View v) {
    Log.d(TAG, "animateWobbleInverse");
    ObjectAnimator animator = createBaseWobble(v);
    animator.setFloatValues(2, -2);
    animator.start();
    wobbleAnimators.add(animator);
  }

  private ObjectAnimator createBaseWobble(final View v) {

    Log.d(TAG, "createBaseWobble");

    if (Tools.isLollipop())
      v.setLayerType(LAYER_TYPE_SOFTWARE, null);

    ObjectAnimator animator = new ObjectAnimator();
    animator.setDuration(180);
    animator.setRepeatMode(ValueAnimator.REVERSE);
    animator.setRepeatCount(ValueAnimator.INFINITE);
    animator.setPropertyName("rotation");
    animator.setTarget(v);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        v.setLayerType(LAYER_TYPE_NONE, null);
      }
    });

    return animator;
  }

  private void restartWobble() {
    Log.d(TAG, "restartWobbleAnimation");
    stopWobble(false);
    //startWobbleAnimation();
  }

  private AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
    final ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
    final ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
    final AnimatorSet animSetXY = new AnimatorSet();
    animSetXY.playTogether(animX, animY);
    return animSetXY;
  }

  private void animateReorder(final int oldPos, final int newPos) {
    final boolean isForward = newPos > oldPos;
    final List<Animator> resultList = new LinkedList<>();
    if (isForward) {
      for (int pos = Math.min(oldPos, newPos); pos < Math.max(oldPos, newPos); pos++) {
        final View view = getViewForId(getItemId(pos));
        if ((pos + 1) % getColCount() == 0)
          resultList.add(createTranslationAnimations(view, -view.getWidth() * (getColCount() - 1), 0, view.getHeight(), 0));
        else
          resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
      }
    } else {
      for (int pos = Math.max(oldPos, newPos); pos > Math.min(oldPos, newPos); pos--) {
        final View view = getViewForId(getItemId(pos));
        if ((pos + getColCount()) % getColCount() == 0) {
          resultList.add(createTranslationAnimations(view, view.getWidth() * (getColCount() - 1), 0, -view.getHeight(), 0));
        } else
          resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
      }
    }

    final AnimatorSet resultSet = new AnimatorSet();
    resultSet.playTogether(resultList);
    resultSet.setDuration(ANIMATION_INTERVAL);
    resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
    resultSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        reorderAnimation = true;
        updateEnableState();
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        reorderAnimation = false;
        updateEnableState();
      }
    });
    resultSet.start();
  }

  /**
   * Interfaces
   */

  private interface SwitchCellAnimator {
    void animateSwitchCell(final int originalPosition, final int targetPosition);
  }

  /**
   * Auxiliar Classes
   */

  private class KitKatSwitchCellAnimator implements SwitchCellAnimator {

    private int deltaY;
    private int deltaX;

    public KitKatSwitchCellAnimator(int deltaX, int deltaY) {
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }

    @Override
    public void animateSwitchCell(final int originalPosition, final int targetPosition) {
      getViewTreeObserver().addOnPreDrawListener(new AnimateSwitchViewOnPreDrawListener(mobileView, originalPosition, targetPosition));
      mobileView = getViewForId(mobileItemId);
    }

    private class AnimateSwitchViewOnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

      private final View previousMobileView;
      private final int originalPosition;
      private final int targetPosition;

      AnimateSwitchViewOnPreDrawListener(final View previousMobileView, final int originalPosition, final int targetPosition) {
        this.previousMobileView = previousMobileView;
        this.originalPosition = originalPosition;
        this.targetPosition = targetPosition;
      }

      @Override
      public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);

        tOffsetY += deltaY;
        tOffsetX += deltaX;

        animateReorder(originalPosition, targetPosition);

        previousMobileView.setVisibility(View.VISIBLE);

        return true;
      }
    }
  }

}
