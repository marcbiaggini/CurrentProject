package com.rockspoon.rockandui.Interfaces;

/**
 * Created by Lucas Teske on 24/07/15.
 */
public interface OnActionListAction {
  void onClick(int pos, String title);

  void onOpen();

  void onClose();

  void onScrollBackClick();
}