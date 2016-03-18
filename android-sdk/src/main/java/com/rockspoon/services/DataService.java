package com.rockspoon.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.helpers.events.CacheBusEvent;
import com.rockspoon.helpers.events.PrintEvent;
import com.rockspoon.libraries.printer.PrinterJob;
import com.rockspoon.models.session.ImmutableSession;
import com.rockspoon.models.user.LoginUserData;
import com.rockspoon.models.user.User;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.models.venue.menu.Menu;
import com.squareup.otto.Bus;

import org.acra.ACRA;
import org.androidannotations.api.UiThreadExecutor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lucas on 03/12/15.
 */
public class DataService {

  private final static String DATA_DEVICE_VENUE_ID = "venueId";
  private final static String DATA_DEVICE_ID = "deviceId";
  private final static String GCM_TOKEN_SENT_TO_SERVER = "gcmTokenSentToServer";

  // Locks
  private final Object floorPlanLock = new Object();

  // Busses
  private final Bus cacheBus = new Bus();
  private final List<String> cacheTopics = new ArrayList<>();
  private final List<LoginUserData> venueEmployees = new ArrayList<>();
  private final List<Menu> menus = new ArrayList<>();
  // Caches
  private SharedPreferences preferences;
  private boolean registered = false;
  private User loggedUser;
  private Employee loggedEmployee;
  private List<UserClockEvent> currentClock = new ArrayList<>();
  private VenueFloorPlan currentFloorPlan = new VenueFloorPlan();
  private Device thisDevice;
  private Venue deviceVenue;

  public DataService(Context ctx) {
    this.preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    reload();
  }

  /**
   * Sets the current logged user and employee.<BR/>
   * Also sets the Acra Custom Data loggedUser and loggedEmployee with its IDs
   *
   * @param user     the Logged User
   * @param employee the Logged Employee
   *
   * @see User
   * @see Employee
   */
  public void setLogged(final User user, final Employee employee) {
    this.loggedUser = user;
    this.loggedEmployee = employee;

    if (user != null) {
      ACRA.getErrorReporter().putCustomData("loggedUser", user.getId());
    }

    if (employee != null) {
      ACRA.getErrorReporter().putCustomData("loggedEmployee", employee.getId());
    }
  }

  /**
   * Converts a list of Employee to LoginUserData containing the name and images and sets <BR/>
   * the cache.
   *
   * @param ctx            the context to instatiate image object
   * @param venueEmployees the list of Employee from the venue
   *
   * @see LoginUserData
   * @see Employee
   */
  public void setVenueEmployees(final Context ctx, final List<Employee> venueEmployees) {
    final List<LoginUserData> loginUsers = new LinkedList<>();

    for (final Employee employee : venueEmployees) {
      loginUsers.add(new LoginUserData(ctx, employee));
    }

    synchronized (this.venueEmployees) {
      this.venueEmployees.clear();
      this.venueEmployees.addAll(loginUsers);
    }
  }

  /**
   * Replaces the current list of clock events for the logged user in cache.
   *
   * @param events List of UserClockEvent
   *
   * @see UserClockEvent
   */
  public void updateCurrentClock(final List<UserClockEvent> events) {
    if (currentClock == null) {
      currentClock = new ArrayList<>();
    }

    if (events != null) {
      currentClock.clear();
      currentClock.addAll(events);
    }
  }

  /**
   * Adds a Clock Event to the current logged user cache
   *
   * @param event a UserClockEvent Object
   *
   * @see UserClockEvent
   */
  public void updateCurrentClock(final UserClockEvent event) {
    currentClock.add(event);
  }

  /**
   * Updates the cache of Menus (Thread-safe with Menu List Lock)
   *
   * @param menuList a list of Menu
   *
   * @see Menu
   */
  public void updateMenuList(final List<Menu> menuList) {
    synchronized (menus) {
      menus.clear();
      menus.addAll(menuList);
    }
  }

  /**
   * Return the logged user current clock events
   *
   * @return List of UserClockEvent
   *
   * @see UserClockEvent
   */
  public List<UserClockEvent> getCurrentClock() {
    return this.currentClock;
  }

  /**
   * Returns the device ID of this device
   *
   * @return the device ID
   */
  public String getDeviceId() {
    return this.preferences.getString(DATA_DEVICE_ID, null);
  }

  /**
   * Sets the current device ID into SharedPreferences
   *
   * @param deviceId the current device ID
   *
   * @see Device
   * @see SharedPreferences
   */
  public void setDeviceId(String deviceId) {
    this.preferences.edit().putString(DATA_DEVICE_ID, deviceId).commit();
  }

  /**
   * Returns the cached list of the Venue Employees (Thread-safe with Employee List Lock)
   *
   * @return List of Venue Employees
   *
   * @see Employee
   */
  public List<LoginUserData> getVenueEmployees() {
    synchronized (venueEmployees) {
      return venueEmployees;
    }
  }

  /**
   * Returns the device Venue ID
   *
   * @return the Device Venue ID
   *
   * @see Venue
   */
  public String getDeviceVenueId() {
    return this.preferences.getString(DATA_DEVICE_VENUE_ID, null);
  }

  /**
   * Saves the Device VenueID to the SharedPreferences.
   *
   * @param venueId the Venue ID of the current device
   *
   * @see SharedPreferences
   * @see Venue
   */
  public void setDeviceVenueId(String venueId) {
    this.preferences.edit().putString(DATA_DEVICE_VENUE_ID, venueId).commit();
  }

  /**
   * Returns the current logged employee or null if none is logged
   *
   * @return User object
   *
   * @see User
   */
  public User getLoggedUser() {
    return loggedUser;
  }

  /**
   * Returns the current logged employee or null if none is logged
   *
   * @return Employee object that can be null
   *
   * @see Employee
   */
  public Employee getLoggedEmployee() {
    return loggedEmployee;
  }

  /**
   * Returns the current device venue
   *
   * @return a Venue Object from the current Device
   *
   * @see Venue
   */
  public Venue getDeviceVenue() {
    return deviceVenue;
  }

  /**
   * Sets the current device venue
   *
   * @param venue the current device Venue
   *
   * @see Venue
   */
  public void setDeviceVenue(final Venue venue) {
    this.deviceVenue = venue;
    if (venue != null) {
      ACRA.getErrorReporter().putCustomData("venueId", venue.getId());
    }
  }

  /**
   * Gets this device data.
   *
   * @return a Device Object containing the data of the current device.
   *
   * @see Device
   */
  public Device getThisDevice() {
    return thisDevice;
  }

  /**
   * Sets the current device data
   *
   * @param device the current device data
   *
   * @see Device
   */
  public void setThisDevice(final Device device) {
    this.thisDevice = device;
    if (device != null) {
      ACRA.getErrorReporter().putCustomData("deviceId", device.getId());
    }
  }

  /**
   * Get the current cached floorplan in Data Service
   *
   * @return the Current VenueFloorPlan
   *
   * @see CacheManagerService
   * @see VenueFloorPlan
   */
  public VenueFloorPlan getCurrentFloorPlan() {
    synchronized (floorPlanLock) {
      return currentFloorPlan;
    }
  }

  /**
   * Updates the current floor plan (Thread-safe with Floor Plan Lock)
   *
   * @param floorPlan a VenueFloorPlanObject
   *
   * @see VenueFloorPlan
   */
  public void setCurrentFloorPlan(final VenueFloorPlan floorPlan) {
    synchronized (floorPlanLock) {
      currentFloorPlan = floorPlan;
    }
  }

  /**
   * Get the list of cached menus in Data Service.
   *
   * @return List of Menus
   *
   * @see CacheManagerService
   * @see Menu
   */
  public List<Menu> getMenus() {
    synchronized (menus) {
      return menus;
    }
  }

  /**
   * Gets the status of the GCM Token in our Cloud
   *
   * @return true if the GCM token was already send to our servers
   *
   * @see com.rockspoon.pushnotification.RegistrationIntentService
   */
  public boolean getGCMTokenSent() {
    return this.preferences.getBoolean(GCM_TOKEN_SENT_TO_SERVER, false);
  }

  /**
   * Sets if the GCM Token was already sent to our cloud services into SharedPreferences.
   *
   * @param tokenSent true if it already have been sent
   *
   * @see com.rockspoon.pushnotification.RegistrationIntentService
   * @see SharedPreferences
   */
  public void setGCMTokenSent(boolean tokenSent) {
    this.preferences.edit().putBoolean(GCM_TOKEN_SENT_TO_SERVER, tokenSent).commit();
    ACRA.getErrorReporter().putCustomData(GCM_TOKEN_SENT_TO_SERVER, String.valueOf(tokenSent));
  }

  /**
   * Return the list of Cache Topic Names for GCM Topic Registration
   *
   * @return List of the GCM Topic Registration name
   *
   * @see com.rockspoon.pushnotification.RegistrationIntentService
   */
  public List<String> getCacheTopics() {
    return this.cacheTopics;
  }

  /**
   * Sets the Topics to GCM Register (Thread-safe with Cache Topic List Lock)
   *
   * @param cacheTopics a list of String
   *
   * @see com.rockspoon.pushnotification.RegistrationIntentService
   */
  public void setCacheTopics(final List<String> cacheTopics) {
    synchronized (cacheTopics) {
      this.cacheTopics.clear();
      this.cacheTopics.addAll(cacheTopics);
    }
  }

  /**
   * Returns the Cache Bus
   *
   * @return a Bus Object
   *
   * @see Bus
   * @see CacheManagerService
   * @see ManagerService
   */
  public Bus getCacheBus() {
    return cacheBus;
  }

  /**
   * Sends a Event through a Cache Bus
   *
   * @param event a CacheBusEvent
   *
   * @see CacheManagerService
   * @see ManagerService
   */
  public void sendCacheBusMessage(final CacheBusEvent event) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      cacheBus.post(event);
    } else {
      UiThreadExecutor.runTask("", () -> cacheBus.post(event), 0L);
    }
  }

  /**
   * Dispatches the print job to be printed in this device.
   *
   * @param job the Printer Job
   *
   * @see ManagerService
   */
  public void dispatchPrintJob(final PrinterJob job) {
    RockServices.getDataService().sendCacheBusMessage(new PrintEvent(job));
  }

  /**
   * Reload DataService basic data
   */
  public void reload() {
    final ImmutableSession session = RockServices.getSessionService().restoreSession("device");
    String token = session != null ? session.getToken() : "";

    setDeviceVenue(new Venue().withId(getDeviceVenueId()));
    setThisDevice(new Device().withId(getDeviceId()).withUid(token));
    setGCMTokenSent(getGCMTokenSent());
    registered = RockServices.isRegistered();
  }

  /**
   * Returns true if this device is registered (a.k.a. configured with the cloud)
   *
   * @return true if is registered with cloud
   */
  public boolean getRegisteredState() {
    return registered;
  }

  /**
   * Clears a logged user from DataService
   */
  public void logout() {
    loggedEmployee = null;
    loggedUser = null;
  }

  /**
   * Clears the DataService data
   */
  public void resetData() {
    this.preferences.edit().clear().commit();
    currentClock = new ArrayList<>();
    loggedEmployee = null;
    loggedUser = null;
  }
}
