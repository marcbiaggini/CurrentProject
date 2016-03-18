package com.rockspoon.rockpos.pos.test.scenarios.login;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.user.LoginUserData;
import com.rockspoon.rockandui.InitialSetup.InitialSetupActivity_;
import com.rockspoon.rockpos.R;
import com.rockspoon.rockpos.SplashScreen.SplashScreen_;
import com.rockspoon.rockpos.pos.test.scenarios.Helpers.SetupHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by juancamilovilladuarte on 3/17/16.
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {


  @Rule
  ActivityTestRule<SplashScreen_> mActivityRule = new ActivityTestRule<>(SplashScreen_.class);

  public static void setSetupHelper() {
    SetupHelper setupHelper = new SetupHelper();
    if (!RockServices.getDataService().getRegisteredState() || RockServices.getDataService().getDeviceVenueId() == null) {
      RockServices.getDataService().resetData();
      setupHelper.doLogin("test@test.com", "Vt4$gM");

      onData(instanceOf(LoginUserData.class)).atPosition(0).perform(click());
      //get the text which the fragment shows
      ViewInteraction fragmentText = onView(withId(R.id.textViewPassword));

      //check the fragments text is now visible in the activity
      fragmentText.check(ViewAssertions.matches(isDisplayed()));
    }
  }

  @Before
  public static void resetTimeout() {
    IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
    IdlingPolicies.setIdlingResourceTimeout(26, TimeUnit.SECONDS);
  }

  @Test
  public static void loginUser() {

      onData(instanceOf(LoginUserData.class)).atPosition(0).perform(click());
      //get the text which the fragment shows
      ViewInteraction fragmentText = onView(withId(R.id.textViewPassword));

      //check the fragments text is now visible in the activity
      fragmentText.check(ViewAssertions.matches(isDisplayed()));

      //click the button to login
      onView((withId(R.id.input_key2))).perform(click());
      onView((withId(R.id.input_key5))).perform(click());
      onView((withId(R.id.input_key1))).perform(click());
      onView((withId(R.id.input_key0))).perform(click());
  }

}
