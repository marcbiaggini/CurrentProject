package com.rockspoon.rockpos.OpenTabs;

import android.app.Activity;
import android.widget.ListView;

import com.rockspoon.rockpos.OpenTabs.Adapters.OpenTabsAdapter;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;

/**
 * Created by Yury Minich on 2/8/16.
 */
@EActivity(R.layout.open_tabs_activity)
public class OpenTabsActivity extends Activity {

  private final DecimalFormat df = new DecimalFormat("0.00");
  private final int rangeMin = 10;
  private final int rangeMax = 120;

  @ViewById(R.id.open_tabs_list)
  ListView openTabsListView;

  private OpenTabsAdapter openTabsAdapter;
  private List<OpenTabItem> openTabItems;

  @Click(R.id.back_button)
  void backButtonClick() {
    onBackPressed();
  }

  @AfterViews
  void initAfterViews() {
    generateData();
  }

  @UiThread
  void fillData(List<OpenTabItem> openTabs) {
    openTabItems = openTabs;
    openTabsAdapter = new OpenTabsAdapter(this, openTabItems);
    openTabsListView.setAdapter(openTabsAdapter);
  }

  @Background
  void generateData() {
    List<OpenTabItem> openTabs = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
      OpenTabItem tab = new OpenTabItem(String.valueOf(i + 1), new BigDecimal(df.format(randomValue)));
      openTabs.add(tab);
    }

    fillData(openTabs);
  }

  @AllArgsConstructor
  public class OpenTabItem {
    public String tableNumber;
    public BigDecimal orderAmount;
  }

}
