package com.rockspoon.rockpos.Planing.Adapters.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.rockpos.Planing.Adapters.Wrappers.ListSectionHolderWrapper;
import com.rockspoon.rockpos.R;

public class ListSectionViewHolder extends RecyclerView.ViewHolder {
  private TextView headerTitle;
  private ImageView arrowImageView;
  private View currentItemView;

  public ListSectionViewHolder(View itemView) {
    super(itemView);
    currentItemView = itemView;
    headerTitle = ((TextView) itemView.findViewById(R.id.headerTitle));
    arrowImageView = ((ImageView) itemView.findViewById(R.id.arrowImageView));
  }

  public void bindData(ListSectionHolderWrapper holderWrapper) {
    headerTitle.setText(holderWrapper.getTitle());
    setIsRecyclable(false);
    arrowImageView.setRotation(holderWrapper.isExpanded ? 180 : 0);
    currentItemView.setOnClickListener(view -> {
      RotateAnimation animation = new RotateAnimation(!holderWrapper.isExpanded ? 180 : 0, !holderWrapper.isExpanded ? 360 : 180, arrowImageView.getWidth() / 2, arrowImageView.getHeight() / 2);

      holderWrapper.isExpanded = !holderWrapper.isExpanded;
      animation.setDuration(300);
      animation.setInterpolator(new AccelerateDecelerateInterpolator());
      animation.setFillAfter(true);
      arrowImageView.startAnimation(animation);

      holderWrapper.listener.onClick(view);
    });
  }
}
