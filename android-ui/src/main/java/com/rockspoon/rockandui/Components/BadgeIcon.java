package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 08/07/15.
 */
public class BadgeIcon extends FrameLayout {

  private final TextView countView;
  private final ImageView badgeIconImage;
  private int count;

  public BadgeIcon(final Context ctx) {
    this(ctx, null);
  }

  public BadgeIcon(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public BadgeIcon(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);

    inflate(ctx, R.layout.badge_icon, this);

    countView = (TextView) findViewById(R.id.badgeCount);
    badgeIconImage = (ImageView) findViewById(R.id.badgeIconImage);
    initializeComponent(ctx, attrs);
  }

  private void initializeComponent(final Context ctx, final AttributeSet attrs) {
    if (attrs != null) {
      final TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.BadgeIcon, 0, 0);
      try {
        setCount(a.getInt(R.styleable.BadgeIcon_count, 0));
        setImageResource(a.getResourceId(R.styleable.BadgeIcon_imageSrc, R.drawable.menu_icon_contact));
      } finally {
        a.recycle();
      }
    }
  }

  public void setImageResource(int resId) {
    badgeIconImage.setImageResource(resId);
  }

  public void setImageDrawable(final Drawable drawable) {
    badgeIconImage.setImageDrawable(drawable);
  }

  public void setImageBitmap(final Bitmap bmp) {
    badgeIconImage.setImageBitmap(bmp);
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
    if (count > 99) {
      countView.setText(R.string.placeholder_badge_count);
      countView.setVisibility(View.VISIBLE);
    } else if (count <= 0) {
      countView.setVisibility(View.GONE);
    } else {
      countView.setText(String.valueOf(count));
      countView.setVisibility(View.VISIBLE);
    }
  }
}
