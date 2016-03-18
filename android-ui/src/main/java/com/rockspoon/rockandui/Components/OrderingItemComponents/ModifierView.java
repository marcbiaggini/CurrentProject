package com.rockspoon.rockandui.Components.OrderingItemComponents;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.rockspoon.rockandui.Interfaces.ModifierListener;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;

/**
 * Created by greenfrvr
 */
public abstract class ModifierView<T> extends LinearLayout implements Selectable<T> {

  protected T viewData;
  protected ModifierListener listener;
  protected String size;
  protected BigDecimal price = BigDecimal.ZERO;
  protected int position;
  protected boolean isMandatory;

  public ModifierView(Context context) {
    super(context);
  }

  public ModifierView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ModifierView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setBackground(ContextCompat.getDrawable(context, R.drawable.border_background));
  }

  public void setSize(String size) {
    this.size = size;
  }

  public BigDecimal getPrice() {
    return price;
  }

  @Override
  public void setViewData(T data) {
    this.viewData = data;
  }

  @Override
  public void setPosition(int position) {
    this.position = position;
  }

  @Override
  public void setModifierListener(ModifierListener listener) {
    this.listener = listener;
  }

  @Override
  abstract public boolean isConfigured();

  abstract protected void initViews();

}

