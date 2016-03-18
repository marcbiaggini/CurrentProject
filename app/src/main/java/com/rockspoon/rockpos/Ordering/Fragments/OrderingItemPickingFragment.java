package com.rockspoon.rockpos.Ordering.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.MenuUpdateEvent;
import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.rockpos.Ordering.BaseOrderingFragment;
import com.rockspoon.rockpos.R;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Objects;

/**
 * Created by Lucas Teske on 27/07/15.
 * TODO: Slow Scroll up the Category Bar (Need to think about how is the best way to do it)
 */
@EFragment
public class OrderingItemPickingFragment extends BaseOrderingFragment {

  private static final String ARGS_MENU_ID = "selected_menu";

  @ViewById(R.id.ordering_category_bar)
  TabLayout categoriesTabs;

  @ViewById(R.id.ordering_items)
  ViewPager foodViewPager;

  @ViewById(R.id.view_mode_button)
  ImageView viewModeButton;

  private CategoryPagerAdapter adapter;
  private List<MenuCategory> listMenus;

  private String memberName = "";

  private Object cacheCallback = new Object() {
    @Subscribe
    public void onMenuUpdated(final MenuUpdateEvent event) {
      loadMenusList();
      adapter.notifyDataSetChanged();
    }
  };

  public static OrderingItemPickingFragment newInstance(Long menuId) {
    Bundle args = new Bundle();
    args.putLong(ARGS_MENU_ID, menuId);
    OrderingItemPickingFragment fragment = new OrderingItemPickingFragment_();
    fragment.setArguments(args);
    return fragment;
  }

  public static OrderingItemPickingFragment newInstance() {
    Bundle args = new Bundle(); //Passing empty bundle prevents getArguments() method return null
    OrderingItemPickingFragment fragment = new OrderingItemPickingFragment_();
    fragment.setArguments(args);
    return fragment;
  }

  @Click(R.id.view_mode_button)
  void viewModeChanged() {
    boolean mode = adapter.changeMode();
    viewModeButton.setImageResource(mode ? R.drawable.vec_list_view : R.drawable.vec_thumb_view);

    Intent intent = new Intent();
    intent.setAction(CategoryFragment.ACTION_CHANGE_VIEW_MODE);
    intent.putExtra(CategoryFragment.EXTRA_MODE, mode);
    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.ordering_food_categories_fragment;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getOrder().diningParty == null) {
      clearFragmentsStack();
    }
    for (DinerSession session : getOrder().diningParty.getDinerSessions()) {
      if (session.getSeatNumber().equals(getSeatNumber())) {
        if (session.getDiner() != null) {
          memberName = session.getDiner().getUserName();
          break;
        }
      }
    }
    setTopBarTitle(getFragmentTitle());
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadMenusList();
  }

  private void loadMenusList() {
    if (getArguments().containsKey(ARGS_MENU_ID)) {
      for (Menu menu : RockServices.getDataService().getMenus()) {
        if (Objects.equals(menu.getId(), getArguments().getLong(ARGS_MENU_ID))) {
          listMenus = menu.getCategories();
        }
      }
    } else {
      listMenus = RockServices.getDataService().getMenus().get(0).getCategories();
    }
  }

  @AfterViews
  void initViews() {
    adapter = new CategoryPagerAdapter(getChildFragmentManager(), listMenus);
    foodViewPager.setAdapter(adapter);
    categoriesTabs.setupWithViewPager(foodViewPager);
    RockServices.getDataService().getCacheBus().register(cacheCallback);
  }

  @Override
  public void onDestroyView() {
    RockServices.getDataService().getCacheBus().unregister(cacheCallback);
    super.onDestroyView();
  }

  @Override
  public String getFragmentTitle() {
    if (isValidSeatNumber()) {
      if (memberName.isEmpty()) {
        return getString(R.string.fragment_title_ordering_seat, getSeatNumber());
      } else {
        return memberName;
      }
    } else {
      return getString(R.string.fragment_title_ordering_table_without_price, getTableName());
    }
  }

  @Override
  public void onTitleClicked() {
    if (isValidSeatNumber() && getOrder().diningParty != null) {
      //will be handled a bit later
//      ExtendedMemberDialog memberDialog = ExtendedMemberDialog.newInstance(getSeatNumber());
//      memberDialog.show(getFragmentManager(), ExtendedMemberDialog.TAG);
    }
  }

  public class CategoryPagerAdapter extends FragmentPagerAdapter {

    private boolean isInGridMode = false;
    private List<MenuCategory> categoryList;

    public CategoryPagerAdapter(FragmentManager fm, List<MenuCategory> list) {
      super(fm);
      this.categoryList = list;
    }

    public boolean changeMode() {
      return isInGridMode = !isInGridMode;
    }

    @Override
    public Fragment getItem(int position) {
      Fragment fragment;
      if (position == getCount() - 1) {
        fragment = ManualEntryFragment.newInstance();
      } else {
        fragment = CategoryFragment.newInstance(isInGridMode, position);
      }
      return fragment;
    }

    @Override
    public int getCount() {
      return 1 + (categoryList != null ? categoryList.size() : 0);
    }

    public void setData(List<MenuCategory> list) {
      this.categoryList.clear();
      this.categoryList.addAll(list);
      this.notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (position == getCount() - 1) {
        return "Manual Entry";
      } else {
        MenuCategory category = categoryList.get(position);
        return getString(com.rockspoon.rockandui.R.string.button_food_category, category.getTitle(), category.getItems().size());
      }
    }
  }

}
