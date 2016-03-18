package com.rockspoon.kitchentablet.UI.SetupScreen.Fragments;

import android.app.Fragment;
import android.widget.RadioGroup;

import com.rockspoon.kitchentablet.UI.SetupScreen.KitchenSetupActivity;
import com.rockspoon.kitchentablet.UI.SetupScreen.KitchenSetupData;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * SelectOrderModeFragment.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/1/16.
 */
@EFragment(R.layout.kitchen_select_order_mode_fragment)
public class SelectOrderModeFragment extends Fragment implements KitchenSetupActivity.OnSetupDataFragment {

  private KitchenSetupData setupData;

  @ViewById(R.id.modes_group)
  RadioGroup modesGroup;

  @AfterViews
  void initViews() {
    modesGroup.setOnCheckedChangeListener(onOrderModeChangeListener);
    modesGroup.check(setupData.getOrderViewMode() == KitchenSetupData.OrderViewMode.INBOX ? R.id.inbox_button : R.id.in_progress_button);
  }

  private RadioGroup.OnCheckedChangeListener onOrderModeChangeListener = (group, checkedId) -> {
    switch (checkedId) {
      case R.id.inbox_button:
        setupData.setOrderViewMode(KitchenSetupData.OrderViewMode.INBOX);
        break;
      case R.id.in_progress_button:
        setupData.setOrderViewMode(KitchenSetupData.OrderViewMode.IN_PROGRESS);
        break;
    }
  };

  @Override
  public void setSetupData(KitchenSetupData data) {
    this.setupData = data;
  }

  @Override
  public KitchenSetupData getSetupData() {
    return setupData;
  }
}
