package com.rockspoon.rockandui.Components;

    import android.content.Context;
    import android.content.res.TypedArray;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.DashPathEffect;
    import android.graphics.Paint;
    import android.graphics.RectF;
    import android.text.TextPaint;
    import android.util.AttributeSet;
    import android.view.View;
    import com.rockspoon.rockandui.Objects.TimelineMark;
    import com.rockspoon.rockandui.R;
    import java.util.ArrayList;
    import java.util.LinkedList;
    import java.util.List;

/**
 * Created by lucas on 08/12/15.
 */
public class Timeline extends View {

  private final List<List<TimelineMark>> markerLines = new ArrayList<>(2);
  private final float lineThickness;
  private final int startTime = 0;
  private final int endTime = 23;
  private boolean isTablet = false;

  private final float screenDensity;
  private final float timeLineBarTop;
  private final float textSize;
  private final float timeLineBarTextMargin;
  private final float minimumTimelineBarHeight;

  // Paints
  private final Paint guidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint guideDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
  private final Paint timeBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  // Guides
  private final RectF guideBox = new RectF();
  private final RectF timeBox = new RectF();

  public Timeline(Context ctx) {
    this(ctx, null);
  }

  public Timeline(Context ctx, AttributeSet attrs) {
    super(ctx, attrs);

    screenDensity = ctx.getResources().getDisplayMetrics().density;
    timeLineBarTop = 40 * screenDensity;
    textSize = 14 * screenDensity;
    timeLineBarTextMargin = 5 * screenDensity;
    minimumTimelineBarHeight = 60 * screenDensity;

    if (attrs != null) {
      final TypedArray typedArray = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.Timeline, 0, 0);
      try {
        lineThickness = typedArray.getDimension(R.styleable.Timeline_lineThickness, 10 * screenDensity);
      } finally {
        typedArray.recycle();
      }
    } else {
      lineThickness = 10 * screenDensity;
    }

    guidePaint.setColor(Color.GRAY);
    textPaint.setColor(Color.BLACK);
    textPaint.setTextSize(textSize);

    guideDotPaint.setStyle(Paint.Style.STROKE);
    guideDotPaint.setPathEffect(new DashPathEffect(new float[]{1 * screenDensity, 2 * screenDensity}, 0));

    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  public void addMarker(final int line, final TimelineMark mark) {
    for (int i = markerLines.size(); i <= line; i++)
      markerLines.add(new LinkedList<TimelineMark>());

    markerLines.get(line).add(mark);
  }

  public void addMarker(final int line, final float startPos, final float endPos, final int color) {
    addMarker(line, new TimelineMark(startPos, endPos, color));
  }

  public void addMarker(final int line, final float startPos, final float endPos) {
    addMarker(line, startPos, endPos, Color.BLACK);
  }

  public TimelineMark getMarker(final int line, final int position) {
    if (markerLines.size() < line)
      return null;

    final List<TimelineMark> marks = markerLines.get(line);
    return marks.size() < position ? null : marks.get(position);
  }

  public TimelineMark getMarker(final int line, final float startPos, final float endPos) {
    if (markerLines.size() < line)
      return null;

    final List<TimelineMark> marks = markerLines.get(line);
    for (final TimelineMark mark : marks) {
      if (mark.getStartPos() == startPos && mark.getEndPos() == endPos)
        return mark;
    }

    return null;
  }

  public void updateMarker(final int line, final float startPos, final float endPos, final float newStartPos, final float newEndPos, final int newColor) {
    final TimelineMark mark = getMarker(line, startPos, endPos);
    if (mark == null)
      return;

    mark.setStartPos(newStartPos);
    mark.setEndPos(newEndPos);
    if (newColor != -1)
      mark.setColor(newColor);
    invalidate();
  }

  public void updateMarker(final int line, final int startPos, final float endPos, final float newStartPos, final float newEndPos) {
    updateMarker(line, startPos, endPos, newStartPos, newEndPos, -1);
  }

  public void updateLastMarker(final float newEndPos) {
    final int lastLine = markerLines.size() - 1;
    if (lastLine == -1)
      return;

    final int lastMarker = markerLines.get(lastLine).size() - 1;
    if (lastMarker == -1)
      return;

    markerLines.get(lastLine).get(lastMarker).setEndPos(newEndPos);
    invalidate();
  }

  public void clear() {
    this.markerLines.clear();
    invalidate();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int minimumHeight = (int) ((minimumTimelineBarHeight + timeLineBarTop) / screenDensity);
    int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    height = height < minimumHeight ? minimumHeight : height;

    setMeasuredDimension(width, height);

    guideBox.set(0, timeLineBarTop, width, height - timeLineBarTop);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    final int width = getMeasuredWidth() + (getMeasuredWidth() * 2) / 100;
    final int numBars = 1 + endTime - startTime;
    final int barWidth = width / numBars;
    final float timeBoxTop = guideBox.top + guideBox.height() / 2 - (((markerLines.size() * 2) - 1) * lineThickness) / 2;

    canvas.drawLine(guideBox.left, guideBox.top, guideBox.right, guideBox.top, guidePaint);
    canvas.drawLine(guideBox.left, guideBox.bottom, guideBox.right, guideBox.bottom, guidePaint);

    int hI;
    float hWidth;

    hWidth = textPaint.measureText("AM", 0, 2);
    canvas.drawText("AM", guideBox.left + width / 4 - (hWidth / 2), guideBox.top - textSize - 2 * timeLineBarTextMargin, textPaint);

    hWidth = textPaint.measureText("PM", 0, 2);
    canvas.drawText("PM", guideBox.left + 3 * (width / 4) - (hWidth / 2), guideBox.top - textSize - 2 * timeLineBarTextMargin, textPaint);

    for (int i = 0; i < numBars; i++) {
      hI = i;

      if (hI == 12) {
        canvas.drawLine(guideBox.left + i * barWidth, guideBox.top, guideBox.left + i * barWidth, guideBox.bottom, textPaint);
        canvas.drawLine(guideBox.left + i * barWidth, 0, guideBox.left + i * barWidth, guideBox.top - textSize - timeLineBarTextMargin, textPaint);
      } else
        canvas.drawLine(guideBox.left + i * barWidth, guideBox.top, guideBox.left + i * barWidth, guideBox.bottom, guideDotPaint);

      if(hI%3==0||isTablet) {
        String h = Integer.toString(hI > 12 ? hI - 12 : hI);
        hWidth = textPaint.measureText(h, 0, h.length());
        canvas.drawText(h, guideBox.left + i * barWidth - hWidth / 2, guideBox.top - timeLineBarTextMargin, textPaint);
      }
    }

    for (int line = 0; line < markerLines.size(); line++) {
      final List<TimelineMark> markers = markerLines.get(line);
      final float lineOffset = line * lineThickness * 2;
      for (final TimelineMark mark : markers) {
        timeBoxPaint.setColor(mark.getColor());
        timeBox.set((mark.getStartPos()) * barWidth, timeBoxTop + lineOffset, (mark.getEndPos()) * barWidth, timeBoxTop + lineOffset + lineThickness);
        canvas.drawRect(timeBox, timeBoxPaint);
      }
    }
  }

  public boolean isTablet() {
    return isTablet;
  }

  public void setIsTablet(boolean isTablet) {
    this.isTablet = isTablet;
  }
}
