package com.rockspoon.rockpos.PaymentCompleted.Fragments;


import android.app.Fragment;

import com.rockspoon.rockpos.PaymentCompleted.PaymentCompletedActivity;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

/**
 * Created by Eugen K. on 2/4/16.
 */
@EFragment(R.layout.payment_completed_fragment)
public class PaymentCompletedFragment extends Fragment {

  private PaymentCompletedFragmentListener listener;

  public interface PaymentCompletedFragmentListener {
    void onPaymentCompletedFragmentReady();

    void onPaymentDone();
  }

  public void setListener(PaymentCompletedActivity listener) {
    this.listener = listener;
  }

  @Click(R.id.send_link_button)
  void onSendLinkButtonClick() {
    listener.onPaymentCompletedFragmentReady();
  }

  @Click(R.id.no_send_link_button)
  void onNoThanksClick() {
    if (listener != null) {
      listener.onPaymentDone();
    }
  }

}
