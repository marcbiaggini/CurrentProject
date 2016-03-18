package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.rockspoon.rockandui.Interfaces.OnSecondActionButtonClick;

/**
 * Created by Lucas Teske on 29/07/15.
 */
public class DualActionButton extends Button {

  private OnSecondActionButtonClick onSecondActionButtonClick;
  private OnClickListener onClickListener;

  public DualActionButton(final Context ctx) {
    this(ctx, null);
  }

  public DualActionButton(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public DualActionButton(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
    super.setOnClickListener((view) -> {
      if (onSecondActionButtonClick != null)
        onSecondActionButtonClick.onClick(view);

      if (onClickListener != null)
        onClickListener.onClick(view);
    });
  }

  public void setOnSecondActionButtonClick(OnSecondActionButtonClick listener) {
    this.onSecondActionButtonClick = listener;
  }

  @Override
  public void setOnClickListener(OnClickListener listener) {
    this.onClickListener = listener;
  }
}
