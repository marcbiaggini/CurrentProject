package com.rockspoon.rockpos.Planing;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.rockspoon.rockpos.Planing.Adapters.PlaningPageAdapter;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.planing_activity)
public class PlaningActivity extends Activity {

  @ViewById(R.id.tab_layout)
  TabLayout tabLayout;

  @ViewById(R.id.pager)
  ViewPager pager;

  @AfterViews
  void initAfterViews() {
    ;

    final PlaningPageAdapter adapter = new PlaningPageAdapter
        (getFragmentManager(), PlaningActivity.this);
    pager.setAdapter(adapter);
    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
    tabLayout.setupWithViewPager(pager);
    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        pager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    pager.setOffscreenPageLimit(tabLayout.getTabCount());
  }
}
