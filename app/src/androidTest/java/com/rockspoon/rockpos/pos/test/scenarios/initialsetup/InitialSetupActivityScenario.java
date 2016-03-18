package com.rockspoon.rockpos.pos.test.scenarios.initialsetup;

/**
 * Created by juancamilovilladuarte on 3/16/16.
 */

import android.os.Handler;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.test.ActivityInstrumentationTestCase2;
import android.text.format.DateUtils;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.rockandui.InitialSetup.InitialSetupActivity_;
import com.rockspoon.rockpos.R;
import com.rockspoon.rockpos.pos.test.scenarios.Resources.HandlerEvents.Countdown;
import com.rockspoon.rockpos.pos.test.scenarios.Resources.HandlerEvents.EventCountDownHandler;
import com.rockspoon.rockpos.pos.test.scenarios.Resources.TimeIdlingResource;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;

public class InitialSetupActivityScenario extends ActivityInstrumentationTestCase2<InitialSetupActivity_> {

  public InitialSetupActivityScenario() {
    super(InitialSetupActivity_.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    InitialSetupActivity_ activity = getActivity();
    if (!RockServices.getDataService().getRegisteredState() || RockServices.getDataService().getDeviceVenueId() == null) {
      RockServices.getDataService().resetData();

      final EventCountDownHandler eventCountDownHandler = new EventCountDownHandler();

      onView(withId(R.id.initialsetup_startsetupbtn)).perform(click());

      onView(withId(R.id.initialsetup_login_email)).perform(typeText("test@test.com"), closeSoftKeyboard());
      onView(withId(R.id.initialsetup_login_password)).perform(typeText("Vt4$gM"), closeSoftKeyboard());//
      onView(withId(R.id.initialsetup_login_loginbtn)).perform(click());

      waitForDeviceList(DateUtils.SECOND_IN_MILLIS * 10);

      final CountingIdlingResource idlingResource = new CountingIdlingResource("Countdown");
      eventCountDownHandler.setCountdown(new CountdownWrapper(eventCountDownHandler.makeHandler(), idlingResource));
      registerIdlingResources(idlingResource);
    }// Comment this line to see the test failing
  }

  public void testIsSuccessfullyDestroyed() {
    try {
      onView(withId(R.id.dialog_generic_message_single_okbtn)).perform(click());
    } catch (NoMatchingViewException e) {
      //View Message not displayed
    }

  }

  private void waitForDeviceList(long waitingTime) {

    // Make sure Espresso does not time out
    IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);
    IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, TimeUnit.MILLISECONDS);

    // Now we wait
    IdlingResource idlingResource = new TimeIdlingResource(waitingTime);
    Espresso.registerIdlingResources(idlingResource);

    //Validate if Dialog is showed
    try {
      onView(withId(R.id.dialog_generic_select_message)).check(matches(isDisplayed()));
      onData(instanceOf(String.class)).atPosition(4).perform(click());
      onView(withId(R.id.dialog_generic_message_dual_okbtn)).perform(click());
    } catch (NoMatchingViewException e) {
      //View Message not displayed
      onView(withId(R.id.dialog_generic_message_single_okbtn)).perform(click());
    }

    // Clean up
    Espresso.unregisterIdlingResources(idlingResource);
  }

  private class CountdownWrapper extends Countdown {

    private final CountingIdlingResource idlingResource;

    public CountdownWrapper(Handler handler, CountingIdlingResource idlingResource) {
      super(handler);
      this.idlingResource = idlingResource;
    }

    @Override
    protected void onCountdownStarted() {
      idlingResource.increment();
    }

    @Override
    public void onCountdownFinished() {
      idlingResource.decrement();
    }

    @Override
    public void stop() {
      super.stop();
      idlingResource.decrement();
    }
  }
}

