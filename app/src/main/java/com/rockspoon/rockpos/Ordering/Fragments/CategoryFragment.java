package com.rockspoon.rockpos.Ordering.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.rockandui.Adapters.AbsFoodItemsAdapter;
import com.rockspoon.rockandui.Adapters.FoodItemsAdapter;
import com.rockspoon.rockandui.Adapters.FoodItemsGridAdapter;
import com.rockspoon.rockandui.Components.RSActionFragment;
import com.rockspoon.rockandui.Interfaces.FoodListClickListener;
import com.rockspoon.rockpos.R;

import java.util.List;

import timber.log.Timber;

public class CategoryFragment extends RSActionFragment implements FoodListClickListener {

  public static final String ARGS_INDEX = "items_index";
  public static final String ARGS_GRID_MODE = "view_mode";

  public static final String ACTION_CHANGE_VIEW_MODE = "ordering:change_view_mode";
  public static final String EXTRA_MODE = "ordering:extra_mode";

  private RecyclerView itemsRecyclerView;
  private boolean isInGridMode = false;

  private RVOrderingItemFragment rvOrderingItemFragment;

  private long menuCategoryId = -1L;

  public static CategoryFragment newInstance(boolean viewMode, int pos) {
    Bundle args = new Bundle();
    args.putInt(ARGS_INDEX, pos);
    args.putBoolean(ARGS_GRID_MODE, viewMode);

    CategoryFragment fragment = new CategoryFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    isInGridMode = getArguments().getBoolean(ARGS_GRID_MODE);

    IntentFilter filter = new IntentFilter(ACTION_CHANGE_VIEW_MODE);
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(messageReceiver, filter);
  }

  @Override
  public void onDestroy() {
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(messageReceiver);
    super.onDestroy();
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.oredring_category_items_list;
  }

  @Override
  public String getFragmentTitle() {
    return null;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    itemsRecyclerView = (RecyclerView) view.findViewById(R.id.items_list);

    setAdapter();
    setScrollableView(itemsRecyclerView);
  }

  public void setAdapter() {
    int pos = getArguments().getInt(ARGS_INDEX);

    MenuCategory data = RockServices.getDataService().getMenus().get(0).getCategories().get(pos);
    menuCategoryId = data.getId();

    AbsFoodItemsAdapter adapter = getAdapter(data.getItems());
    adapter.setFoodItemClickListener(this);

    itemsRecyclerView.setAdapter(adapter);
    itemsRecyclerView.setLayoutManager(isInGridMode ? new GridLayoutManager(getActivity(), 4) : new LinearLayoutManager(getActivity()));
  }

  private AbsFoodItemsAdapter getAdapter(List<Item> items) {
    if (isInGridMode) {
      return new FoodItemsGridAdapter(getActivity(), items);
    } else {
      return new FoodItemsAdapter(getActivity(), items);
    }
  }

  @Override
  public void onDestroyView() {
    removeScrollableView(itemsRecyclerView);
    super.onDestroyView();
  }

  @Override
  public void onFoodItemClicked(Item item) {
    Timber.i("ITEM MODIFIERS: " + item.getSettingsAndPrices().toString());
    rvOrderingItemFragment = RVOrderingItemFragment.newInstance(item, menuCategoryId);
    replaceFragment(rvOrderingItemFragment);
  }

  private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      isInGridMode = intent.getBooleanExtra(EXTRA_MODE, false);
      setAdapter();
    }
  };
}