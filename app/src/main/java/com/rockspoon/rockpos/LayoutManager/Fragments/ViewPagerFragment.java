package com.rockspoon.rockpos.LayoutManager.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Interfaces.LayoutManagerInterface;
import com.rockspoon.rockandui.Managers.LayoutModelManager;
import com.rockspoon.rockpos.LayoutManager.FloorAdapter;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Eugen K. on 2/11/16.
 */
@EFragment
public class ViewPagerFragment extends RSBaseFragment implements LayoutManagerInterface, ViewPager.OnPageChangeListener {

  private TextView leftLabel;
  private TextView centerLabel;
  private TextView rightLabel;

  private LayoutModelManager layoutModelManager;
  private FloorAdapter adapter;
  private Long currentSectionId;
  private TabLayout tabLayout;

  @ViewById(R.id.section_view_pager)
  ViewPager pager;

  public static ViewPagerFragment newInstance() {
    return new ViewPagerFragment_();
  }

  public LayoutModelManager getLayoutModelManager() {
    return layoutModelManager;
  }

  @AfterViews
  void initPager() {
    pager.setAdapter(adapter);
    pager.addOnPageChangeListener(this);

    setTopBarDiningSections();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    layoutModelManager = new LayoutModelManager(getActivity());
    adapter = new FloorAdapter(getChildFragmentManager(), layoutModelManager.getDiningSections());
    currentSectionId = layoutModelManager.getVenueFloorPlan().getDiningSections().get(0).getId();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    layoutModelManager = null;
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
    return R.layout.section_view_pager_fragment;
  }

  @Override
  public String getFragmentTitle() {
    return null;
  }

  @Override
  public void setTopBarState(Long sectionId) {
    LayoutModelManager.CheckedTablesState state = layoutModelManager.getDiningSectionById(sectionId).getCheckedTableState();
    if (getTopBar() != null && getTopBar().getChildAt(1) != null) {
      getTopBar().removeViewAt(1);
    }
    tabLayout.setVisibility(View.GONE);
    switch (state) {
      case NO_CHECKED:
        if (getTopBar() != null && getTopBar().getChildAt(1) != null) {
          getTopBar().removeViewAt(1);
        }
        tabLayout.setVisibility(View.VISIBLE);
        break;
      case ONE_CHECKED:
        setTopBarSingleTableChecked();
        break;
      case MORE_THEN_ONE_CHECKED:
        setMergeBtnTopBar(sectionId);
        break;
      case MERGED_PARTY_CHECKED:
        setUnMergeBtnTopBar(sectionId);
        break;
      default:
        break;
    }
  }

  public void setMergeBtnTopBar(Long sectionId) {
    View v = LayoutInflater.from(getActivity()).inflate(R.layout.topbar_buttons, null);
    leftLabel = (TextView) v.findViewById(R.id.topbar_left_label);
    leftLabel.setText(R.string.merge_tables);
    leftLabel.setOnClickListener(v1 -> {
      Intent intent = new Intent(LayoutModelManager.RECEIVER_MERGE_TABLE);
      intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, sectionId);
      LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    });
    if (getTopBar() != null) {
      getTopBar().addView(v);
    }
  }

  public void setUnMergeBtnTopBar(Long sectionId) {
    View v = LayoutInflater.from(getActivity()).inflate(R.layout.topbar_buttons, null);
    leftLabel = (TextView) v.findViewById(R.id.topbar_left_label);
    leftLabel.setText(R.string.un_merge_tables);
    leftLabel.setOnClickListener(v1 -> {
      Intent intent = new Intent(LayoutModelManager.RECEIVER_UN_MERGE_TABLE);
      intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, sectionId);
      LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    });
    if (getTopBar() != null) {
      getTopBar().addView(v);
    }
  }

  public void setTopBarSingleTableChecked() {
    View v = LayoutInflater.from(getActivity()).inflate(R.layout.topbar_buttons, null);
    leftLabel = (TextView) v.findViewById(R.id.topbar_left_label);
    leftLabel.setText(R.string.transfer_bill);
    if (getTopBar() != null) {
      getTopBar().addView(v);
    }
  }

  public void setTopBarDiningSections() {
    getTopBar().removeAllViews();
    tabLayout = (TabLayout) LayoutInflater.from(getActivity()).inflate(R.layout.topbar_table_sections_tabs, null);
    getTopBar().addView(tabLayout);
    tabLayout.setupWithViewPager(pager);
    setTabsLabels(tabLayout);
  }

  private void setTabsLabels(TabLayout tabLayout) {
    for (int i = 0; i < layoutModelManager.getVenueFloorPlan().getDiningSections().size(); i++) {
      TextView textRates = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.topbar_tabs_header, null);
      textRates.setText(layoutModelManager.getDiningSections().get(i).getName());
      textRates.setGravity(View.TEXT_ALIGNMENT_CENTER);
      tabLayout.getTabAt(i).setCustomView(textRates);
    }
  }

  private IntentFilter getLayoutIntentFilter() {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(LayoutModelManager.RECEIVER_TABLE_STATE);
    return intentFilter;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  @Override
  public void onPageSelected(int position) {
    currentSectionId = adapter.getSectionId(position);
  }

  @Override
  public void onPageScrollStateChanged(int state) {
  }


  private BroadcastReceiver tableStateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Long sectionId = intent.getLongExtra(LayoutModelManager.EXTRA_SECTION_ID, -1);
      switch (intent.getAction()) {
        case LayoutModelManager.RECEIVER_TABLE_STATE:
          setTopBarState(sectionId);
          break;
      }
    }

  };
}
