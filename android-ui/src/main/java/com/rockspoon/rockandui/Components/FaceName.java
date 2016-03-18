package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 03/08/15.
 */
public class FaceName extends LinearLayout implements Checkable { //

  private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

  private final RoundImageView imageView;
  private final TextView textView;

  private boolean checked = false;

  private OnClickListener userOnClickListener;
  private final OnClickListener defaultClickListener = (view) -> {
    toggle();
    if (userOnClickListener != null) {
      userOnClickListener.onClick(view);
    }
  };

  public FaceName(final Context ctx) {
    this(ctx, null);
  }

  public FaceName(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public FaceName(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
    inflate(ctx, R.layout.facename, this);

    setOrientation(VERTICAL);
    setClickable(true);
    setBackgroundResource(R.drawable.facename_back);
    super.setOnClickListener(defaultClickListener);

    int padding = getResources().getDimensionPixelOffset(R.dimen.facename_padding);
    setPadding(padding, padding, padding, padding);
    setMinimumWidth(getResources().getDimensionPixelOffset(R.dimen.facename_min_width));

    imageView = (RoundImageView) findViewById(R.id.facename_image);
    textView = (TextView) findViewById(R.id.facename_text);

    extractAttributes(attrs);
  }

  private void extractAttributes(AttributeSet attrs) {
    if (attrs != null) {
      final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FaceName, 0, 0);
      try {
        imageView.setImageResource(a.getResourceId(R.styleable.FaceName_android_src, R.drawable.thumbnail));
        textView.setText(a.getString(R.styleable.FaceName_android_text));
      } finally {
        a.recycle();
      }
    }
  }

  public void setText(CharSequence text) {
    textView.setText(text);
  }

  public void setImageDrawable(Drawable image) {
    imageView.setImageDrawable(image);
  }

  public void setImageResource(int resource) {
    imageView.setImageResource(resource);
  }

  public void setImageBitmap(Bitmap bitmap) {
    imageView.setImageBitmap(bitmap);
  }

  public RoundImageView getImage() {
    return imageView;
  }

  @Override
  public boolean isChecked() {
    return checked;
  }

  @Override
  public void setChecked(boolean checked) {
    this.checked = checked;
    setSelected(checked);
  }

  @Override
  public void toggle() {
    setChecked(!checked);
  }

  @Override
  public void setOnClickListener(OnClickListener listener) {
    this.userOnClickListener = listener;
  }

  @Override
  protected int[] onCreateDrawableState(int extraSpace) {
    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
    if (isChecked()) {
      mergeDrawableStates(drawableState, CHECKED_STATE_SET);
    }
    return drawableState;
  }
}
