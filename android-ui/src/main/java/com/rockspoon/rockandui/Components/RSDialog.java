package com.rockspoon.rockandui.Components;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 06/08/15.
 */
public abstract class RSDialog extends DialogFragment {

  private int gravity = Gravity.TOP;
  private int dialogMarginTop = -1;
  private int dialogMarginBottom = -1;
  private int dialogMarginStart = -1;
  private int dialogMarginEnd = -1;

  private LinearLayout root;

  public RSDialog() {
    setStyle(STYLE_NO_FRAME, R.style.rsDialogExitUp);
  }

  public void setGravity(int gravity) {
    this.gravity = gravity;
  }

  public void setDialogMarginTop(int marginTop) {
    this.dialogMarginTop = marginTop;
  }

  public void setDialogMarginBottom(int marginBottom) {
    this.dialogMarginBottom = marginBottom;
  }

  public void setDialogMarginStart(int marginStart) {
    this.dialogMarginStart = marginStart;
  }

  public void setDialogMarginEnd(int marginEnd) {
    this.dialogMarginEnd = marginEnd;
  }

  @Override
  public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    root = new LinearLayout(getActivity());
    root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    dialogMarginTop = dialogMarginTop == -1 ? getResources().getDimensionPixelOffset(R.dimen.rsdialog_marginTop) : dialogMarginTop;
    dialogMarginBottom = dialogMarginBottom == -1 ? getResources().getDimensionPixelOffset(R.dimen.rsdialog_marginBottom) : dialogMarginBottom;
    dialogMarginStart = dialogMarginStart == -1 ? getResources().getDimensionPixelOffset(R.dimen.rsdialog_marginStart) : dialogMarginStart;
    dialogMarginEnd = dialogMarginEnd == -1 ? getResources().getDimensionPixelOffset(R.dimen.rsdialog_marginEnd) : dialogMarginEnd;

    root.setPadding(dialogMarginStart, dialogMarginTop, dialogMarginEnd, dialogMarginBottom);
    root.setBackground(new ColorDrawable(Color.TRANSPARENT));

    final View child = onRSCreateView(inflater, container, savedInstanceState);
    child.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    root.addView(child);

    return root;
  }

  public abstract View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

  @Override
  public void onStart() {
    super.onStart();

    if (getDialog() == null)
      return;

    getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    getDialog().getWindow().setGravity(gravity);
  }

  public void shake() {
    final Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
    root.startAnimation(shake);
  }

}
