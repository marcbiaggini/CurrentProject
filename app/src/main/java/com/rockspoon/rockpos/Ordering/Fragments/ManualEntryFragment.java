package com.rockspoon.rockpos.Ordering.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;
import com.rockspoon.rockpos.Ordering.BaseOrderingFragment;
import com.rockspoon.rockpos.Ordering.OrderingActions;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.util.List;

/**
 * Created by greenfrvr
 */
@EFragment
public class ManualEntryFragment extends BaseOrderingFragment implements TextWatcher {

  @ViewById(R.id.manual_entry_desc_input)
  EditText descriptionEditText;

  @ViewById(R.id.manual_entry_price_input)
  EditText priceEditText;

  @ViewsById({R.id.manual_entry_order, R.id.manual_entry_order_and_continue})
  List<Button> buttons;

  private OrderingActions orderingActions;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (getActivity() instanceof OrderingActions) {
      orderingActions = (OrderingActions) getActivity();
    }
  }

  public static ManualEntryFragment newInstance() {
    return new ManualEntryFragment_();
  }

  @AfterViews
  void initViews() {
    descriptionEditText.addTextChangedListener(this);
  }

  @Override
  public void onDestroyView() {
    descriptionEditText.removeTextChangedListener(this);
    super.onDestroyView();
  }

  @Click(R.id.manual_entry_order_and_continue)
  void orderAndContinue() {
    if (orderingActions != null) {
      orderingActions.orderAndContinue(createItemInstanceRequest());
    }
  }

  @Click(R.id.manual_entry_order)
  void order() {
    if (orderingActions != null) {
      orderingActions.order(createItemInstanceRequest());
    }
  }

  private ItemInstanceRequest createItemInstanceRequest() {
    //TODO specify how to create manual entry items
    return null;
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.manual_entry_fragment;
  }

  @Override
  public String getFragmentTitle() {
    return null;
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    boolean enabled = s.length() != 0;
    for (Button b : buttons) {
      b.setEnabled(enabled);
    }
  }

  @Override
  public void afterTextChanged(Editable s) {

  }

}
