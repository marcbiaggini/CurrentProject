package com.rockspoon.rockpos;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.models.venue.ordering.item.ItemInstanceRequest;
import com.rockspoon.rockandui.Components.RSBaseActivity;
import com.rockspoon.rockandui.Dialogs.GenericMessageDialog;
import com.rockspoon.rockpos.ClockInOut.Fragments.ClockFragment;
import com.rockspoon.rockpos.ItemPhotos.Fragments.SelectItemFragment;
import com.rockspoon.rockpos.LayoutManager.Fragments.ViewPagerFragment;
import com.rockspoon.rockpos.Login.LoginActivity;
import com.rockspoon.rockpos.Ordering.Fragments.OrderingTableFragment;
import com.rockspoon.rockpos.Ordering.OrderBundle;
import com.rockspoon.rockpos.Ordering.OrderingActions;
import com.rockspoon.rockpos.UserProfile.ChangePasswordActivity;
import com.rockspoon.rockpos.UserProfile.UserProfileActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;

import timber.log.Timber;

/**
 * Created by greenfrvr
 */
@EActivity(R.layout.navigation_activity)
public class NavigationActivity extends RSBaseActivity implements FragmentManager.OnBackStackChangedListener, OrderingActions {

  public static final String STATE_ORDER = "order_info";
  public static final String STATE_BOTTOM_MENU = "bottom_menu";
  public static final String STATE_PARTY = "party_instance";

  @ViewById(R.id.pos_base_layout_drawer)
  DrawerLayout drawerLayout;

  @ViewById(R.id.login_user_userImage)
  ImageView userImageView;

  @ViewById(R.id.login_user_userName)
  TextView usernameTextView;

  @ViewById(R.id.login_user_userRole)
  TextView userRoleTextView;

  @ViewById(R.id.pos_base_bottom_menu)
  View bottomMenuView;

  private ProgressDialog dialog;
  private OrderBundle orderBundle;
  private int bottomMenuVisibility;

  public static void startActivity(Context context) {
    NavigationActivity_.intent(context).start();
  }

  public OrderBundle getOrderBundle() {
    return orderBundle;
  }

  public void setBottomMenuVisible(boolean visible) {
    bottomMenuView.setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  @Override
  protected boolean isRootActivity() {
    return true;
  }

  @AfterViews
  void initSlideMenu() {
    Employee employee = RockServices.getDataService().getLoggedEmployee();
    if (employee != null) {
      if (employee.getUser().getAvatar() != null) {
        ImageData_ avatarData = ImageData_.getInstance_(this);
        avatarData.from(employee.getUser().getAvatar().getNoResolution().getUrl());
        userImageView.setImageDrawable(avatarData.getImage(this, userImageView));
      }

      String name = employee.getUser().getFirstName() + " " + employee.getUser().getLastName();
      usernameTextView.setText(name);
      userRoleTextView.setText(employee.getRole().getName());
    }
    bottomMenuView.setVisibility(bottomMenuVisibility);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentManager().addOnBackStackChangedListener(this);

    if (savedInstanceState != null) {
      orderBundle = (OrderBundle) savedInstanceState.getSerializable(STATE_ORDER);
      orderBundle.restorePartyFromJSON(savedInstanceState.getString(STATE_PARTY, ""));
      bottomMenuVisibility = savedInstanceState.getInt(STATE_BOTTOM_MENU, View.VISIBLE);
    } else {
      orderBundle = new OrderBundle();
    }

    if (getFragmentManager().findFragmentById(R.id.fragment_container) == null) {
      addRootFragment(ViewPagerFragment.newInstance());
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putString(STATE_PARTY, orderBundle.convertPartyToJSON());
    outState.putSerializable(STATE_ORDER, orderBundle);
    outState.putInt(STATE_BOTTOM_MENU, bottomMenuView.getVisibility());
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    getFragmentManager().removeOnBackStackChangedListener(this);
    super.onDestroy();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      getWindow().getDecorView().setSystemUiVisibility(
          View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
  }

  @Click(R.id.pos_base_button_open_menu)
  protected void bottomMenuClick() {
    drawerLayout.openDrawer(GravityCompat.START);
  }

  @Click(R.id.sidemenu_button_clockio)
  protected void clockIOClick() {
    replaceFragment(ClockFragment.newInstance(), true);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Click(R.id.sidemenu_button_itemphotos)
  protected void itemPhotosClick() {
    replaceFragment(SelectItemFragment.newInstance(), true);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Click(R.id.sidemenu_button_logout)
  protected void logoutButtonClick() {
    LoginActivity.startActivity(this);
    finish();
  }

  @Click(R.id.user_profile_panel)
  protected void userProfileClick() {
    UserProfileActivity.startActivity(this);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Click(R.id.sidemenu_button_change_password)
  protected void changePasswordClick() {
    ChangePasswordActivity.startActivity(this);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Click(R.id.sidemenu_button_about)
  protected void openAboutDialog() {
    showAboutDialog();
  }

  @Override
  public void onBackStackChanged() {
    boolean isRootFragmentVisible = getFragmentManager().getBackStackEntryCount() == 1;
    setBottomMenuVisible(isRootFragmentVisible);
    Timber.i(orderBundle.toString());
  }

  @Override
  @Background
  public void order(ItemInstanceRequest request) {
    Timber.i("PROCESS ORDERING");
    showProgressDialog("Order processing...");
    RockServices.getOrderService().updateOrderingCart(getOrderBundle().diningParty.getId(), request);
    runOnUiThread(() -> {
      dialog.dismiss();
      replaceFragment(OrderingTableFragment.newInstance(), true);
    });
  }

  @Override
  @Background
  public void orderAndContinue(ItemInstanceRequest request) {
    Timber.i("PROCESS ORDERING AND CONTINUE");
    showProgressDialog("Order processing...");
    RockServices.getOrderService().updateOrderingCart(getOrderBundle().diningParty.getId(), request);
    runOnUiThread(() -> {
      dialog.dismiss();
      getFragmentManager().popBackStack();
    });
  }

  @Override
  @Background
  public void orderAndDuplicate(ItemInstanceRequest request) {
    Timber.i("PROCESS ORDERING AND DUPLICATE");
    showProgressDialog("Order processing...");
    RockServices.getOrderService().updateOrderingCart(getOrderBundle().diningParty.getId(), request);
    runOnUiThread(dialog::dismiss);
  }

  private void showAboutDialog() {
    if (baseDialog != null) {
      baseDialog.dismiss();
    }

    String message = getString(R.string.format_about, RockServices.VERSION_NAME, RockServices.BUILD_VERSION, RockServices.VERSION_CODE);
    baseDialog = GenericMessageDialog.newInstance().setTitle(getString(R.string.button_about)).setMessage(message);
    baseDialog.show(getFragmentManager(), "aboutDialog");
  }

  @UiThread(propagation = UiThread.Propagation.REUSE)
  protected void showProgressDialog(String title) {
    if (dialog != null) {
      dialog.dismiss();
    }
    dialog = new ProgressDialog(this);
    dialog.setIndeterminate(true);
    dialog.setTitle(title);
    dialog.setCanceledOnTouchOutside(false);
    dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.show();
  }
}
