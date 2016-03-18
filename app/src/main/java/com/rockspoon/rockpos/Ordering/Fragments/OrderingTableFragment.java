package com.rockspoon.rockpos.Ordering.Fragments;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockpos.Ordering.Adapters.TableMembersAdapter;
import com.rockspoon.rockpos.Ordering.BaseOrderingFragment;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Lucas Teske on 27/07/15.
 */
@EFragment
public class OrderingTableFragment extends BaseOrderingFragment implements TableMembersAdapter.MemberTableListener {

  public static final String ARGS_TABLE_VALUE = "tableValue";

  private TableMembersAdapter adapter;

  @ViewById(R.id.ordering_table_members)
  RecyclerView tableMembers;

  @ViewById(R.id.fire_fab)
  FloatingActionButton fireButton;

  @ViewById(R.id.container)
  FrameLayout frameLayout;

  Boolean isPressed = false;
  ViewDragHelper dragHelper;

  public static OrderingTableFragment newInstance() {
    Bundle args = new Bundle();

    OrderingTableFragment_ fragment = new OrderingTableFragment_();
    fragment.setArguments(args);
    return fragment;
  }

  @AfterViews
  void initViews() {
    setTopBarTitle(getFragmentTitle());

    List<Menu> menus = RockServices.getDataService().getMenus();
    adapter = new TableMembersAdapter(getActivity(), Collections.emptyList(), menus);
    adapter.setMemberTableListener(this);
    tableMembers.setAdapter(adapter);
    tableMembers.setLayoutManager(new LinearLayoutManager(getActivity()));
    setScrollableView(tableMembers);
    dragHelper = ViewDragHelper.create(frameLayout, 1.0f, dragHelperCallback);

    frameLayout.setOnTouchListener(onTouchListener);

    onItemChecked(false, 0l);
    loadCartDetails();
  }

  @Override
  public void onResume() {
    super.onResume();
    frameLayout.setOnTouchListener(onTouchListener);
  }

  @Override
  public void onPause() {
    frameLayout.setOnTouchListener(null);
    super.onPause();
  }

  @Background
  void loadCartDetails() {
    if (getOrder().diningParty != null && getOrder().diningParty.getId() != null) {
      List<ListCartDetails> detailsList = RockServices.getOrderService().listCartDetails(getOrder().diningParty.getId());
      Timber.i("CART DETAILS: " + detailsList.toString());
      updateRecyclerView(detailsList);
    }
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateRecyclerView(List<ListCartDetails> detailsList) {
    adapter.setData(detailsList);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onDestroyView() {
    removeScrollableView(tableMembers);
    super.onDestroyView();
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.ordering_table_fragment;
  }

  public String getFragmentTitle() {
    return getString(R.string.fragment_title_ordering_table_without_price, getTableName());
  }

  @Override
  public void onMemberClicked(DinerSession session, int seatNumber) {
    getOrder().setSeatNumber(session.getSeatNumber(), false);
    Fragment fragment = OrderingItemPickingFragment.newInstance();
    replaceFragment(fragment);
  }

  @Override
  public void onMenuClicked(Long menuId, int position) {
    getOrder().seatNumber = -1;
    Fragment fragment = OrderingItemPickingFragment.newInstance(menuId);
    replaceFragment(fragment);
  }

  @Override
  public void onSelectAll(boolean isSelected) {
  }

  @Override
  public void onItemChecked(boolean isChecked, Long instanceId) {
    Boolean hasSelectedItems = adapter.getSelectedInstanceItemsIds().size() > 0;

    fireButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), hasSelectedItems ? R.color.textcolor_lightblue_enabled : R.color.button_disabled)));
    fireButton.setEnabled(hasSelectedItems);
  }

  GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      Timber.i("onSingleTapConfirmed " + isPressed);
      if (fireButton != null && fireButton.isEnabled()) {
        //Toast.makeText(getActivity(), "Pressed once", Toast.LENGTH_SHORT).show();
      }
      return super.onSingleTapConfirmed(e);
    }

    public void onLongPress(MotionEvent motionEvent) {
      Timber.i("onLongPress " + isPressed);
      if (fireButton != null && motionEvent.getX() >= fireButton.getLeft() && motionEvent.getX() <= fireButton.getRight() && motionEvent.getY() >= fireButton.getTop() && motionEvent.getY() <= fireButton.getBottom()) {
        isPressed = true;
        fireButton.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        dragHelper.processTouchEvent(motionEvent);
      }
    }
  });

  View.OnTouchListener onTouchListener = (view, motionEvent) -> {
    Boolean isEventCatched = false;
    switch (motionEvent.getAction()) {
      case MotionEvent.ACTION_UP:
        fireButton.getBackground().clearColorFilter();
        Timber.i("ACTION_UP " + isPressed);
        isPressed = false;
        break;
      case MotionEvent.ACTION_DOWN:
        Timber.i("ACTION_DOWN " + isPressed);
        if (fireButton != null && motionEvent.getX() >= fireButton.getLeft() && motionEvent.getX() <= fireButton.getRight() && motionEvent.getY() >= fireButton.getTop() && motionEvent.getY() <= fireButton.getBottom()) {
          isEventCatched = true;
          if (fireButton.isEnabled()) {
            fireButton.getBackground().setColorFilter(0x8800A9CE, PorterDuff.Mode.SRC_ATOP);
          }
        }
        break;
      case MotionEvent.ACTION_MOVE:
        Timber.i("ACTION_MOVE " + isPressed);
        if (isPressed) {
          dragHelper.processTouchEvent(motionEvent);
          isEventCatched = true;
        }
        break;
    }

    gestureDetector.onTouchEvent(motionEvent);
    return isEventCatched;
  };

  ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {
    @Override
    public boolean tryCaptureView(View child, int pointerId) {
      return child.getId() == R.id.fire_fab;
    }

    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      super.onViewPositionChanged(changedView, left, top, dx, dy);
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {
      return left;
    }

    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      return top;
    }

    @Override
    public void onViewCaptured(View capturedChild, int activePointerId) {
      super.onViewCaptured(capturedChild, activePointerId);
    }

    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      super.onViewReleased(releasedChild, xvel, yvel);
    }
  };
}
