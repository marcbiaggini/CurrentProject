package com.rockspoon.rockpos.Planing.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RadioGroup;

import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.rockandui.Components.DrawGridView;
import com.rockspoon.rockandui.Components.HScroll;
import com.rockspoon.rockandui.Components.VScroll;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockandui.Objects.FloorPlanItemData;
import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanHeaderAdapter;
import com.rockspoon.rockpos.Planing.Adapters.FloorPlanListRecyclerAdapter;
import com.rockspoon.rockpos.Planing.Adapters.Wrappers.HeaderHolderWrapper;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

@EFragment(R.layout.floor_plan_fragment)
public class FloorPlanFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
  private FloorPlanHeaderAdapter adapter;
  private GenericMessageDialog deleteConfirmDialog;

  private ArrayList<VenueFloorPlan> venueFloorPlans;
  @ViewById(R.id.floor_plans_headers)
  TwoWayView floorPlansHeaders;
  @ViewById(R.id.floor_plan_list)
  RecyclerView floorPlanRecyclerView;
  @ViewById(R.id.floor_plan_segmented)
  SegmentedGroup floorPlanSegmented;
  @ViewById(R.id.zoomIn)
  Button zoomIn;
  @ViewById(R.id.zoomOut)
  Button zoomOut;
  @ViewById(R.id.circlesDrawingView)
  DrawGridView drawGridView;
  @ViewById(R.id.hScroll)
  HScroll hScroll;
  @ViewById(R.id.vScroll)
  VScroll vScroll;

  @AfterViews
  void initViews() {
    //TODO: Remove this implementation with interanl store/service methods
    venueFloorPlans = new ArrayList<>();
    venueFloorPlans.add(VenueFloorPlan.newInstance(String.format(getString(R.string.section_placeholder), 1)));

    adapter = new FloorPlanHeaderAdapter(getActivity(), venueFloorPlans, floorPlansHeaders);
    adapter.setAddHeaderItemVisibility(false);
    adapter.setListener(mFloorPlansTabsListener);

    floorPlansHeaders.setHasFixedSize(true);
    floorPlansHeaders.setLongClickable(true);
    floorPlansHeaders.setAdapter(adapter);

    deleteConfirmDialog = GenericMessageDialog.newInstance();
    deleteConfirmDialog.setOnCancelButtonClick((v) -> deleteConfirmDialog.dismiss());

    floorPlanRecyclerView.setAdapter(new FloorPlanListRecyclerAdapter(getActivity()));
    floorPlanSegmented.setOnCheckedChangeListener(this);
    zoomIn.setOnClickListener(view -> drawGridView.increaseZoom());

    zoomOut.setOnClickListener(view -> drawGridView.decreaseZoom());

    drawGridView.setHorizontalScroll(hScroll);
    drawGridView.setVerticalScroll(vScroll);

    drawGridView.setOnDragListener((v, dragEvent) -> {
      switch (dragEvent.getAction()) {
        case DragEvent.ACTION_DRAG_STARTED:
          // Do nothing
          break;
        case DragEvent.ACTION_DRAG_ENTERED:
          break;
        case DragEvent.ACTION_DRAG_EXITED:
          break;
        case DragEvent.ACTION_DROP:
          // view dropped, reassign the view to the new ViewGroup
          View view = (View) dragEvent.getLocalState();
          FloorPlanItemData data = (FloorPlanItemData) view.getTag();
          drawGridView.addNewObject(dragEvent.getX(), dragEvent.getY(), data);
          break;
        case DragEvent.ACTION_DRAG_ENDED:

        default:
          break;
      }
      return true;
    });

    ViewTreeObserver vto = drawGridView.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            drawGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            drawGridView.setZoom(DrawGridView.ZOOM_MAX_CONSTANT);
          }
        }

    );
  }


  FloorPlanHeaderAdapter.FloorPlansTabsListener mFloorPlansTabsListener = new FloorPlanHeaderAdapter.FloorPlansTabsListener() {
    @Override
    public void onItemPressed(HolderWrapper item) {
      adapter.selectItem(item);
    }

    @Override
    public void onItemLongPressed(HolderWrapper item) {
    }

    @Override
    public void onAddItemPressed(HolderWrapper item) {
      adapter.addItemAtPosition(item);
    }

    @Override
    public void onCloseItemPressed(HolderWrapper item) {
      final Bundle dialogArgs = new Bundle();
      dialogArgs.putBoolean(GenericMessageDialog.PARAM_DUAL_BUTTON_MODE, true);
      dialogArgs.putString(GenericMessageDialog.PARAM_TITLE, getString(R.string.deleting_combinations));
      dialogArgs.putString(GenericMessageDialog.PARAM_MESSAGE, String.format(getString(R.string.do_you_want_delete_tab), ((HeaderHolderWrapper) item).getVenuePlan().getName()));
      deleteConfirmDialog.setArguments(dialogArgs);
      deleteConfirmDialog.setOnOKButtonClick((v) -> {
        adapter.removeItem(item);
        deleteConfirmDialog.dismiss();
      });
      deleteConfirmDialog.show(getFragmentManager(), "confirmDialog");
    }
  };

  @Override
  public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
    floorPlanRecyclerView.setVisibility(checkedId == R.id.button_floor_plan ? View.VISIBLE : View.GONE);
  }
}