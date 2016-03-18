package com.rockspoon.rockpos.LayoutManager;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.rockspoon.models.venue.layout.DiningSection;
import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.rockpos.LayoutManager.Fragments.LayoutMainFragment;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Eugen K. on 2/11/16.
 */
public class FloorAdapter extends FragmentStatePagerAdapter {

  private List<DiningSection> diningSections;

  public FloorAdapter(FragmentManager fm, List<DiningSection> sections) {
    super(fm);
    diningSections = sections;
    filterSections();
  }

  private void filterSections() {
    Iterator<DiningSection> iterator = diningSections.iterator();
    while (iterator.hasNext()) {
      List<DiningSectionSpot> spots = iterator.next().getSpots();
      if (spots == null || spots.isEmpty()) {
        iterator.remove();
      }
    }
  }

  public void updateData(List<DiningSection> data) {
    diningSections.clear();
    diningSections.addAll(data);
    notifyDataSetChanged();
  }

  @Override
  public Fragment getItem(int position) {
    return LayoutMainFragment.newInstance(diningSections.get(position).getId());
  }

  @Override
  public int getCount() {
    return diningSections != null ? diningSections.size() : 0;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return diningSections.get(position).getName();
  }

  public Long getSectionId(int position) {
    return diningSections.get(position).getId();
  }

}
