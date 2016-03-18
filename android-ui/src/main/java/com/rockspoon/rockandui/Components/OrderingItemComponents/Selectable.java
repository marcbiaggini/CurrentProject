package com.rockspoon.rockandui.Components.OrderingItemComponents;

import com.rockspoon.rockandui.Interfaces.ModifierListener;

/**
 * Created by greenfrvr
 */
public interface Selectable<T> {

  void setViewData(T obj);

  void setPosition(int position);

  void setModifierListener(ModifierListener listener);

  boolean isConfigured();
}
