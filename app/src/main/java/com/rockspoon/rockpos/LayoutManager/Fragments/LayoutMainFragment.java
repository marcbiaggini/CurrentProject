package com.rockspoon.rockpos.LayoutManager.Fragments;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.models.venue.layout.DiningSpotList;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.rockandui.Adapters.TablesListAdapter;
import com.rockspoon.rockandui.Interfaces.LayoutManagerInterface;
import com.rockspoon.rockandui.Interfaces.OnTableClick;
import com.rockspoon.rockandui.Interfaces.OnTableItemClick;
import com.rockspoon.rockandui.Interfaces.SectionModelInterface;
import com.rockspoon.rockandui.Managers.LayoutModelManager;
import com.rockspoon.rockandui.Objects.SectionModel;
import com.rockspoon.rockpos.Ordering.BaseOrderingFragment;
import com.rockspoon.rockpos.Ordering.Fragments.OrderingItemPickingFragment;
import com.rockspoon.rockpos.Ordering.Fragments.OrderingTableFragment;
import com.rockspoon.rockpos.R;
import com.rockspoon.services.DiningPartyService;
import com.rockspoon.services.DiningPartyService_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Lucas Teske on 27/07/15.
 */

@EFragment
public class LayoutMainFragment extends BaseOrderingFragment {

  @FragmentArg
  protected Long modelId;
  private RecyclerView recyclerView;
  private LayoutManagerInterface listener;
  private SectionModelInterface layoutModelManager;
  private DiningPartyService diningPartyService;
  private TablesListAdapter adapter;
  private List<DiningParty> diningPartyList;
  private ProgressBar progressBar;

  public static LayoutMainFragment newInstance(Long id) {
    return LayoutMainFragment_.builder().modelId(id).build();
  }

  @AfterInject
  void calledAfterInjection() {
    if (getParentFragment() instanceof ViewPagerFragment) {
      layoutModelManager = ((ViewPagerFragment) getParentFragment()).getLayoutModelManager();
      listener = (LayoutManagerInterface) getParentFragment();
    } else {
      layoutModelManager = new LayoutModelManager(getActivity());
    }
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    recyclerView = (RecyclerView) view.findViewById(R.id.rv_test);
    progressBar = (ProgressBar) view.findViewById(R.id.loading_bar);
  }

  @AfterViews
  void initViews() {
    diningPartyService = DiningPartyService_.getInstance_(getActivity());
    requestForDiningSessions();
  }

  @Background
  void requestForDiningSessions() {
    DiningSpotList diningSpotList = diningPartyService.requestDiningSpotList(modelId);
    List<DiningParty> diningParties = diningPartyService.requestDiningPartyList(diningSpotList);
    updateDiningSessionUi(diningParties, diningSpotList);
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  void updateDiningSessionUi(List<DiningParty> diningPartyList, DiningSpotList diningSpotList) {
    if (diningPartyList == null || diningPartyList.size() == 0) {
      diningPartyList = new ArrayList<>();
    }
    this.diningPartyList = diningPartyList;
    layoutModelManager.getDiningSectionById(modelId).setDiningParties(diningPartyList, diningSpotList);
    adapter = new TablesListAdapter(getActivity(), layoutModelManager.getDiningSectionById(modelId));
    adapter.setOnTableClick(tableClickListener);
    adapter.setOnTableItemClick(tableItemClickListener);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    setScrollableView(recyclerView);
    recyclerView.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.GONE);
  }

  @Override
  public void onDestroyView() {
    removeScrollableView(recyclerView);
    super.onDestroyView();
  }

  @Override
  public void onResume() {
    super.onResume();
    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(tableStateReceiver, getLayoutIntentFilter());
  }

  @Override
  public void onPause() {
    LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(tableStateReceiver);
    super.onPause();
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.layout_main_fragment;
  }

  public String getFragmentTitle() {
    return getString(R.string.fragment_title_layout);
  }

  @Override
  public LoaderManager getLoaderManager() {
    return super.getLoaderManager();
  }

  private void unMergeTables() {
    SectionModel section = layoutModelManager.getDiningSectionById(modelId);
    List<DiningParty> parties = section.getCheckedDiningParty();
    section.unCheckParties();
    if (parties.size() > 0) {
      diningPartyService.unMergeParty(unMergeCallback, parties.get(0));
    }
  }

  private DiningPartyService.MergeDiningPartyCallBack unMergeCallback = (mergedDiningParty, diningParty) -> {
    requestForDiningSessions();
    if (listener != null) {
      listener.setTopBarState(modelId);
    }
  };

  private OnTableClick tableClickListener = (tableNumber, diningParty) -> {
    getOrder().setTableName(tableName(diningParty));
    getOrder().setTableNumber(tableNumber, true);
    getOrder().setDiningParty(diningParty);
    replaceFragment(OrderingTableFragment.newInstance());
  };

  private final OnTableItemClick tableItemClickListener = (session, diningParty) -> {
    getOrder().setTableName(tableName(diningParty));
    getOrder().setSeatNumber(session.getSeatNumber(), false);
    getOrder().setDiningParty(diningParty);
    replaceFragment(OrderingItemPickingFragment.newInstance());
  };

  private String tableName(DiningParty diningParty) {
    StringBuilder builder = new StringBuilder();
    List<DiningSectionSpot> spots = diningParty.getLayoutSpots();
    builder.append(spots.get(0).getNumber());
    for (int i = 1; i < spots.size(); i++) {
      builder.append(", ");
      builder.append(spots.get(i).getNumber());
    }
    return builder.toString();
  }

  private void changeTableState(Intent intent) {
    boolean state = intent.getBooleanExtra(LayoutModelManager.EXTRA_TABLE_STATE, false);
    long[] ids = intent.getLongArrayExtra(LayoutModelManager.EXTRA_TABLE_ID);
    if (listener != null) {
      listener.setTopBarState(modelId);
    }
  }

  private void spotsMerged() {
    layoutModelManager.getDiningSectionById(modelId).setDiningParties(diningPartyList);
    TablesListAdapter adapter = new TablesListAdapter(getActivity(), layoutModelManager.getDiningSectionById(modelId));
    adapter.setOnTableClick(tableClickListener);
    adapter.setOnTableItemClick(tableItemClickListener);

    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(adapter);

    if (listener != null) {
      listener.setTopBarState(modelId);
    }
  }

  private IntentFilter getLayoutIntentFilter() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(LayoutModelManager.RECEIVER_SPOTS_MERGED);
    intentFilter.addAction(LayoutModelManager.RECEIVER_DRAG_AND_DROP_MERGE);
    intentFilter.addAction(LayoutModelManager.RECEIVER_UN_MERGE_TABLE);
    intentFilter.addAction(LayoutModelManager.RECEIVER_MERGE_TABLE);
    intentFilter.addAction(LayoutModelManager.RECEIVER_CLOSE_TABLE);
    intentFilter.addAction(LayoutModelManager.RECEIVER_REFRESH_TABLES);
    return intentFilter;
  }

  private void mergeTables() {
    SectionModel section = layoutModelManager.getDiningSectionById(modelId);
    List<DiningParty> parties = section.getCheckedDiningParty();
    section.unCheckParties();
    if (parties.size() > 0) {
      diningPartyService.mergeDiningParty(unMergeCallback, parties);
    }
  }

  private void closeTables(Long partyId) {
    SectionModel section = layoutModelManager.getDiningSectionById(modelId);
    DiningParty diningParty = null;
    for (DiningParty party : section.getDiningParties()) {
      if (party.getId() != null && party.getId().equals(partyId)) {
        diningParty = party;
      }
    }
    section.unCheckParties();
    if (diningParty != null) {
      diningPartyService.closeTable((mergedDiningParty, diningParties) -> {
        requestForDiningSessions();
      }, diningParty);
    }
  }

  private BroadcastReceiver tableStateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Long sectionId = intent.getLongExtra(LayoutModelManager.EXTRA_SECTION_ID, -1);

      if (!Objects.equals(sectionId, modelId)) {
        return;
      }

      switch (intent.getAction()) {
        case LayoutModelManager.RECEIVER_SPOTS_MERGED: {
          spotsMerged();
        }
        break;
        case LayoutModelManager.RECEIVER_DRAG_AND_DROP_MERGE: {
          changeTableState(intent);
          layoutModelManager.merge(modelId);
          spotsMerged();
        }
        break;
        case LayoutModelManager.RECEIVER_UN_MERGE_TABLE: {
          unMergeTables();
        }
        break;
        case LayoutModelManager.RECEIVER_MERGE_TABLE: {
          mergeTables();
        }
        break;
        case LayoutModelManager.RECEIVER_CLOSE_TABLE: {
          Long partyId = intent.getLongExtra(LayoutModelManager.EXTRA_PARTY_ID, -1);
          closeTables(partyId);
        }
        break;
        case LayoutModelManager.RECEIVER_REFRESH_TABLES: {
          requestForDiningSessions();
        }
        break;
      }
    }
  };
}
