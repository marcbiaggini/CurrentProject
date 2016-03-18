package com.rockspoon.rockpos.PaymentCompleted;

import android.app.Activity;
import android.app.Fragment;

import com.rockspoon.rockpos.PaymentCompleted.Fragments.PaymentCompletedFragment;
import com.rockspoon.rockpos.PaymentCompleted.Fragments.PaymentCompletedFragment_;
import com.rockspoon.rockpos.PaymentCompleted.Fragments.SendLinkFragment;
import com.rockspoon.rockpos.PaymentCompleted.Fragments.SendLinkFragment_;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Eugen K. on 2/4/16.
 */
@EActivity(R.layout.payment_completed_activity)
public class PaymentCompletedActivity extends Activity implements PaymentCompletedFragment.PaymentCompletedFragmentListener, SendLinkFragment.SendLinkFragmentListener {

  @AfterViews
  void setUpUi() {
    final PaymentCompletedFragment fragment = PaymentCompletedFragment_.builder().build();
    fragment.setListener(this);
    replaceFragment(fragment);
  }

  @Override
  public void onPaymentCompletedFragmentReady() {
    final SendLinkFragment fragment = SendLinkFragment_.builder().build();
    fragment.setListener(this);
    replaceFragment(fragment);
  }

  @Override
  public void onPaymentDone() {
    finishWithResult();
  }

  @Override
  public void onSendLinkCancel() {
    getFragmentManager().popBackStack();
  }

  @Override
  public void onSendLinkFragmentReady() {
    finishWithResult();
  }

  private void finishWithResult() {
    setResult(RESULT_OK, null);
    finish();
  }

  private void replaceFragment(Fragment fragment) {
    getFragmentManager()
        .beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .replace(R.id.payment_completed_container, fragment)
        .addToBackStack(null)
        .commit();
  }
}
