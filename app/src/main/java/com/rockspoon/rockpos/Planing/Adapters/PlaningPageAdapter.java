package com.rockspoon.rockpos.Planing.Adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.rockspoon.rockpos.Planing.Fragments.DeliveryFragment;
import com.rockspoon.rockpos.Planing.Fragments.DeniInFragment;
import com.rockspoon.rockpos.Planing.Fragments.FloorPlanFragment_;
import com.rockspoon.rockpos.Planing.Fragments.ReservationsFragment;
import com.rockspoon.rockpos.Planing.Fragments.TakeoutFragment;
import com.rockspoon.rockpos.R;

public class PlaningPageAdapter extends FragmentStatePagerAdapter {
  private Context context;

  public PlaningPageAdapter(FragmentManager fm, Context context) {
    super(fm);

    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {

    switch (position) {
      case 0:
        return new DeliveryFragment();
      case 1:
        return new DeniInFragment();
      case 2:
        return new TakeoutFragment();
      case 3:
        return new ReservationsFragment();
      case 4:
        return new FloorPlanFragment_();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return 5;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return context.getString(R.string.dine_in);
      case 1:
        return context.getString(R.string.delivery);
      case 2:
        return context.getString(R.string.takeout);
      case 3:
        return context.getString(R.string.reservations);
      case 4:
        return context.getString(R.string.floor_plan);
    }
    return null;
  }
}
