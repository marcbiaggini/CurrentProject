package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rockspoon.rockandui.Adapters.PhoneCountryDataAdapter;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.R;

/**
 * Created by Eugen K. on 2/5/16.
 */
public class ChooseCountryDialog extends RSDialog {

  private ListView countriesListView;
  private AdapterView.OnItemClickListener listener;

  public static ChooseCountryDialog newInstance() {
    return new ChooseCountryDialog();
  }

  public void setItemClickListener(AdapterView.OnItemClickListener listener) {
    this.listener = listener;
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.dialog_choose_country, container, false);
    countriesListView = (ListView) view.findViewById(R.id.choose_country_list);
    countriesListView.setAdapter(new PhoneCountryDataAdapter(getActivity()));
    countriesListView.setOnItemClickListener(listener);
    return view;
  }

}
