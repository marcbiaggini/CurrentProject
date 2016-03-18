package com.rockspoon.rockandui.Components.ActionList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.Interfaces.OnActionListAction;
import com.rockspoon.rockandui.R;

/**
 * Created by Lucas Teske on 24/07/15.
 */
public class ActionList extends RelativeLayout {

  private final int CONTAINER_PADDING_BOTTOM;
  private final int ITEM_SPACING;
  private final int ANIMATION_TIME;

  private final ImageButton actionButton;
  private final ImageButton backButton;
  private final LinearLayout blackOverlay;
  private final RelativeLayout container;

  private OnActionListAction onActionListAction;
  private ArrayAdapter<String> adapter;
  private boolean isClosed;

  public ActionList(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);
    inflate(ctx, R.layout.action_button, this);

    actionButton = (ImageButton) findViewById(R.id.action_button);
    blackOverlay = (LinearLayout) findViewById(R.id.blackoverlay);
    container = (RelativeLayout) findViewById(R.id.item_container);
    backButton = (ImageButton) findViewById(R.id.action_button_back);

    CONTAINER_PADDING_BOTTOM = ctx.getResources().getDimensionPixelOffset(R.dimen.actionlist_container_padding_bottom);
    ITEM_SPACING = ctx.getResources().getDimensionPixelOffset(R.dimen.actionlist_item_spacing);
    ANIMATION_TIME = ctx.getResources().getInteger(R.integer.actionlist_anim_speed);

    initializeComponent();
  }

  public ActionList(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public ActionList(final Context ctx) {
    this(ctx, null);
  }

  private void initializeComponent() {
    isClosed = true;
    blackOverlay.setClickable(true);
    blackOverlay.setAlpha(0.0f);

    backButton.setOnClickListener(view -> {
      if (onActionListAction != null) {
        onActionListAction.onScrollBackClick();
      }
    });

    actionButton.setOnClickListener(v -> {
      if (isClosed) {
        openActionList();
      } else {
        closeActionList();
      }
    });
  }

  public void setScrollBackButtonVisibility(int visible) {
    backButton.setVisibility(visible);
  }

  public void setActionButtonVisibility(int visible) {
    actionButton.setVisibility(visible);
  }

  public void openActionList() {
    if (isClosed) {
      setActionListVisible(true);
      if (onActionListAction != null) {
        onActionListAction.onOpen();
      }
      isClosed = false;
    }
  }

  public void closeActionList() {
    if (!isClosed) {
      setActionListVisible(false);
      if (onActionListAction != null) {
        onActionListAction.onClose();
      }
      isClosed = true;
    }
  }

  private void setActionListVisible(boolean visible) {
    blackOverlay.setVisibility(View.VISIBLE);

    blackOverlay.animate().alpha(visible ? 1f : 0f).setDuration(ANIMATION_TIME).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        if (blackOverlay.getAlpha() == 0.0f) {
          blackOverlay.setVisibility(View.INVISIBLE);
        }
      }
    });

    if (container.getChildCount() > 0) {
      final int baseHeight = container.getChildAt(0).getHeight();
      final int containerPaddingBottom = CONTAINER_PADDING_BOTTOM;
      final int itemSpacing = ITEM_SPACING;
      for (int i = 0; i < container.getChildCount(); i++) {
        final View itemView = container.getChildAt(i);
        itemView.setVisibility(VISIBLE);
        itemView.animate()
            .y(visible ?
                container.getHeight() - containerPaddingBottom - ((baseHeight + itemSpacing) * i) :
                container.getHeight())
            .alpha(visible ? 1.0f : 0.0f)
            .setDuration(ANIMATION_TIME)
            .setListener(new AnimatorListenerAdapter() {
              @Override
              public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (itemView.getAlpha() == 0.0f) {
                  itemView.setVisibility(GONE);
                }
              }
            });
      }
    }
  }

  public void updateViews() {
    if (adapter != null) {

      for (int i = adapter.getCount(); i < container.getChildCount(); i++) {
        container.getChildAt(i).setVisibility(View.GONE);
      }

      for (int pos = 0; pos < adapter.getCount(); pos++) {
        final int fpos = pos;

        View itemView = (pos < container.getChildCount()) ? container.getChildAt(pos) : null;
        itemView = adapter.getView(pos, itemView, this);

        itemView.setOnClickListener((v) -> {
          closeActionList();
          if (onActionListAction != null) {
            onActionListAction.onClick(fpos, adapter.getItem(fpos));
          }
        });
        ((TextView) itemView).setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        if (pos >= container.getChildCount()) {
          container.addView(itemView);
        } else if (container.getChildAt(pos) != itemView) {
          container.removeView(container.getChildAt(pos));
          container.addView(itemView, pos);
        }

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT, TRUE);
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        itemView.setVisibility(VISIBLE);
        itemView.setLayoutParams(params);
        itemView.setAlpha(0.0f);
        itemView.setY(container.getHeight());
        itemView.setVisibility(INVISIBLE);
      }
    }
  }

  public void setOnActionListAction(OnActionListAction listener) {
    this.onActionListAction = listener;
  }

  public void setAdapter(ArrayAdapter<String> adapter) {
    this.adapter = adapter;
    updateViews();
  }

}