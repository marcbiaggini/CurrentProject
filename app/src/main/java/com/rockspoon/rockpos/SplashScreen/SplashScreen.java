package com.rockspoon.rockpos.SplashScreen;

import android.app.Activity;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.TextView;

import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.models.venue.menu.Menu;
import com.rockspoon.pushnotification.RegistrationIntentService;
import com.rockspoon.rockandui.InitialSetup.InitialSetupActivity_;
import com.rockspoon.rockpos.BuildConfig;
import com.rockspoon.rockpos.Login.LoginActivity_;
import com.rockspoon.rockpos.R;
import com.rockspoon.services.CacheManagerService_;
import com.rockspoon.services.ManagerService_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by lucas on 03/12/15.
 */
@EActivity(R.layout.splash_screen)
public class SplashScreen extends Activity {


  @ViewById(R.id.splashscreen_message)
  TextView splashMessage;

  @ViewById(R.id.splashscreen_version)
  TextView splashVersion;

  CoordinatorLayout coordinatorLayout;

  @AfterViews
  protected void afterViews() {
    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

    String v = String.format(getString(R.string.format_build_version), BuildConfig.VERSION_NAME, BuildConfig.BUILD_VERSION, BuildConfig.VERSION_CODE);
    splashVersion.setText(v);

    ManagerService_.intent(this).start();
    CacheManagerService_.intent(this).start();

    if (!RockServices.getDataService().getRegisteredState() || RockServices.getDataService().getDeviceVenueId() == null) {
      RockServices.getDataService().resetData();

      InitialSetupActivity_.intent(this).extra("caller", SplashScreen_.class).start();
      finish();
    } else {
      fetchData();
    }
  }

  @UiThread
  protected void updateSplashMessage(String message) {
    splashMessage.setText(message);
  }

  @UiThread
  protected void startLoginActivity() {
    LoginActivity_.intent(this).start();
    finish();
  }

  @UiThread
  protected void retryFetchData() {
    new Handler().postDelayed(() -> fetchData(), 2000);
  }

  @UiThread
  protected void showSnackBar(final String message) {
    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
  }

  @Background
  protected void fetchData() {
    try {
      updateSplashMessage(getString(R.string.message_fetching_device_data));
      final Device device = RockServices.getDeviceService().whoAmI(RockServices.getDataService().getDeviceVenueId());
      RockServices.getDataService().setThisDevice(device);

      updateSplashMessage(getString(R.string.message_initializing_google_services));

      List<String> cacheTopics = RockServices.getDeviceService().getCacheTopics(RockServices.getDataService().getDeviceVenueId());
      RockServices.getDataService().setCacheTopics(cacheTopics);

      if (!RegistrationIntentService.initializeGCM(this)) {
        showSnackBar(getString(R.string.message_failed_to_initialize_google_services));
        throw new com.rockspoon.error.Error(ErrorCode.InternalError, getString(R.string.message_failed_to_initialize_google_services));
      }

      updateSplashMessage(getString(R.string.message_fetching_employees));
      final List<Employee> employeeList = RockServices.getEmployeeService().getVenueEmployees(RockServices.getDataService().getDeviceVenueId());
      Log.d("SplashScreen", "Loaded " + employeeList.size() + " employees.");
      RockServices.getDataService().setVenueEmployees(SplashScreen.this, employeeList);

      updateSplashMessage(getString(R.string.message_fetching_menus));
      List<Menu> menus = RockServices.getVenueService().fetchMenuList(RockServices.getDataService().getDeviceVenueId(), true, true, true);
      RockServices.getDataService().updateMenuList(menus);

      updateSplashMessage(getString(R.string.message_fetching_floor_plan));
      final VenueFloorPlan floorPlan = RockServices.getVenueService().fetchFloorPlan();
      Log.d("SplashScreen", "FloorPlan Loaded.");
      RockServices.getDataService().setCurrentFloorPlan(floorPlan);

      startLoginActivity();
    } catch (com.rockspoon.error.Error e) {
      Log.e("SplashScreen", "Exception " + e.getLocalizedMessage());
      e.printStackTrace();

      updateSplashMessage(String.format(getString(R.string.message_splash_error), e.getLocalizedMessage()));
      retryFetchData();
    }
  }
}
