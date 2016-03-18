package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.rockspoon.rockandui.Interfaces.OnLinearViewItemClick;

/**
 * Created by lucas on 10/07/15.
 * <p>
 * This is needed because we don't want to use ListView (that have Scroll) inside a ScrollView
 */
public class LinearListView extends LinearLayout {

  private static final String TAG = "LinearListView";

  private BaseAdapter viewAdapter;
  private DataSetObserver dataObserver;
  private OnLinearViewItemClick onLinearViewItemClick;

  public LinearListView(final Context ctx) {
    this(ctx, null);
  }

  public LinearListView(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public LinearListView(final Context ctx, final AttributeSet attrs, int defStyle) {
    super(ctx, attrs, defStyle);
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
  }

  public void setOnLinearViewItemClick(OnLinearViewItemClick listener) {
    this.onLinearViewItemClick = listener;
  }

  private void updateViews() {
    if (viewAdapter != null) {

      for (int i = viewAdapter.getCount(); i < getChildCount(); i++) {
        getChildAt(i).setVisibility(View.GONE);
      }
      for (int pos = 0; pos < viewAdapter.getCount(); pos++) {
        final int fpos = pos;

        View itemView = (pos < getChildCount()) ? getChildAt(pos) : null;
        itemView = viewAdapter.getView(pos, itemView, this);
        itemView.setVisibility(VISIBLE);

        itemView.setOnClickListener((v) -> {
          if (onLinearViewItemClick != null) {
            onLinearViewItemClick.onItemClick(v, fpos, getAdapter().getItem(fpos));
          }
        });

        if (pos >= getChildCount()) {
          addView(itemView);
        } else if (getChildAt(pos) != itemView) {
          removeView(getChildAt(pos));
          addView(itemView, pos);
        }
      }
    }
  }

  public BaseAdapter getAdapter() {
    return viewAdapter;
  }

  public void setAdapter(final BaseAdapter adapter) {
    if (this.viewAdapter != null) {
      this.viewAdapter.unregisterDataSetObserver(dataObserver);
    }
    this.viewAdapter = adapter;
    this.viewAdapter.registerDataSetObserver(dataObserver);
    updateViews();
  }
}

