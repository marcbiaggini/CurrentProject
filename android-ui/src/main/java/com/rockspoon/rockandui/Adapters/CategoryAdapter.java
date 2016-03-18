package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.rockandui.Components.DualActionButton;
import com.rockspoon.rockandui.Interfaces.OnCategoryItemClick;
import com.rockspoon.rockandui.Objects.FoodCategoryHolder;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Teske on 27/07/15.
 */
public class CategoryAdapter extends FilterAdapter<FoodCategoryHolder> {

  private final List<DualActionButton> buttonCache = new ArrayList<>();

  private final int buttonSpacing;
  private final int buttonSize;
  private final LinearLayout.LayoutParams buttonParams;

  private OnCategoryItemClick onCategoryItemClick;

  public CategoryAdapter(final Context ctx) {
    super(ctx);
    buttonSize = ctx.getResources().getDimensionPixelSize(R.dimen.category_button_size);
    buttonSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.category_button_spacing);
    buttonParams = new LinearLayout.LayoutParams(buttonSize, buttonSize);
    buttonParams.setMargins(buttonSpacing, buttonSpacing, 0, buttonSpacing);
    buttonParams.gravity = Gravity.CENTER;
  }

  public CategoryAdapter(final Context ctx, final List<MenuCategory> items) {
    super(ctx);
    setCategoryData(items);
    buttonSize = ctx.getResources().getDimensionPixelSize(R.dimen.category_button_size);
    buttonSpacing = ctx.getResources().getDimensionPixelSize(R.dimen.category_button_spacing);
    buttonParams = new LinearLayout.LayoutParams(buttonSize, buttonSize);
    buttonParams.setMargins(buttonSpacing, buttonSpacing, 0, buttonSpacing);
    buttonParams.gravity = Gravity.CENTER;
  }

  public void setOnCategoryItemClick(OnCategoryItemClick listener) {
    this.onCategoryItemClick = listener;
  }

  public void setCategoryData(final List<MenuCategory> items) {
    setCategoryData(items, false);
  }

  public void setCategoryData(final List<MenuCategory> items, Boolean showItemLogo) {
    List<FoodCategoryHolder> foodCategoryHolders = new ArrayList<>();

    for (MenuCategory category: items) {
      FoodCategoryHolder holder = new FoodCategoryHolder();
      holder.category = category;
      holder.foodAdapter = new FoodItemsAdapter(ctx, category.getItems());
      holder.foodAdapter.setShowLogo(showItemLogo);
      foodCategoryHolders.add(holder);
    }

    setData(foodCategoryHolders);
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup viewGroup) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final FoodCategoryHolder foodCategoryHolder = getItem(position);
    final View v = (convertView == null) ? inflater.inflate(R.layout.round_button, null) : convertView;
    final DualActionButton dualActionButton = (DualActionButton) v;

    dualActionButton.setWidth(ctx.getResources().getDimensionPixelSize(R.dimen.category_button_size));
    dualActionButton.setHeight(ctx.getResources().getDimensionPixelSize(R.dimen.category_button_size));

    dualActionButton.setText(String.format(ctx.getResources().getString(R.string.button_food_category), foodCategoryHolder.category.getTitle(), foodCategoryHolder.foodAdapter.getItemCount()));
    dualActionButton.setLayoutParams(buttonParams);
    dualActionButton.setGravity(Gravity.CENTER);

    dualActionButton.setOnSecondActionButtonClick((view) -> {
      Log.d("CategoryAdapter", "Round Button Click: " + position + " " + foodCategoryHolder.category.getTitle());
      resetButtonsState();
      dualActionButton.setEnabled(false);
      dualActionButton.setBackgroundResource(R.drawable.round_button_white_shape_marked);
      dualActionButton.setTextColor(ctx.getResources().getColor(R.color.white));
      if (onCategoryItemClick != null)
        onCategoryItemClick.onClick(position, foodCategoryHolder.category);
    });

    if (position >= buttonCache.size()) {
      buttonCache.add(dualActionButton);
    } else if (buttonCache.get(position) != dualActionButton) {
      buttonCache.remove(position);
      buttonCache.add(position, dualActionButton);
    }

    return v;
  }

  public FoodItemsAdapter getItemAdapter(int position) {
    return getItem(position).foodAdapter;
  }

  public void clickItem(int pos) {
    buttonCache.get(pos).callOnClick();
  }

  private void resetButtonsState() {
    for (final Button b : buttonCache) {
      b.setEnabled(true);
      b.setBackgroundResource(R.drawable.round_button_white);
      b.setTextColor(ctx.getResources().getColor(R.color.textcolor_lightblue));
    }
  }
}
