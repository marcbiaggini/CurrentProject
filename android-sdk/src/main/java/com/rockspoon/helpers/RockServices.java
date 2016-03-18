package com.rockspoon.helpers;

import android.content.Context;
import android.util.Log;

import com.rockspoon.error.RSRestErrorHandler;
import com.rockspoon.exceptions.DataServiceNotInitializedException;
import com.rockspoon.exceptions.DeviceServiceNotInitializedException;
import com.rockspoon.exceptions.EmployeeServiceNotInitializedException;
import com.rockspoon.exceptions.ItemServiceNotInitializedException;
import com.rockspoon.exceptions.OrderServiceNonInitializedException;
import com.rockspoon.exceptions.SessionServiceNotInitializedException;
import com.rockspoon.exceptions.UserServiceNotInitializedException;
import com.rockspoon.exceptions.VenueServiceNotInitializedException;
import com.rockspoon.services.DataService;
import com.rockspoon.services.DeviceService;
import com.rockspoon.services.DeviceService_;
import com.rockspoon.services.EmployeeService;
import com.rockspoon.services.EmployeeService_;
import com.rockspoon.services.ForgotPasswordService;
import com.rockspoon.services.ForgotPasswordService_;
import com.rockspoon.services.ItemService;
import com.rockspoon.services.ItemService_;
import com.rockspoon.services.ManagerService;
import com.rockspoon.services.OrderService;
import com.rockspoon.services.OrderService_;
import com.rockspoon.services.SessionService;
import com.rockspoon.services.TableService;
import com.rockspoon.services.TableService_;
import com.rockspoon.services.UserService;
import com.rockspoon.services.UserService_;
import com.rockspoon.services.VenueService;
import com.rockspoon.services.VenueService_;

/**
 * Created by Lucas Teske on 10/11/15.
 */
public class RockServices {

  /**
   * Build Version. Filled by Application using BuildConfig
   */
  public static String BUILD_VERSION = "SNAPSHOT (unk)";

  /**
   * Version Code. Filled by Application using BuildConfig
   */
  public static int VERSION_CODE = -1;

  /**
   * Version Name. Filled by Application using BuildConfig
   */
  public static String VERSION_NAME = "---";

  protected static SessionService sessionService = null;
  protected static DeviceService deviceService = null;
  protected static UserService userService = null;
  protected static VenueService venueService = null;
  protected static EmployeeService employeeService = null;
  protected static DataService dataService = null;
  protected static ItemService itemService = null;
  protected static OrderService orderService = null;
  protected static ForgotPasswordService forgotPasswordService = null;
  protected static TableService tableService = null;
  protected static ManagerService managerService = null;

  public static void initializeServices(Context context, RSRestErrorHandler errorHandler) {
    deviceService = new DeviceService_(context);
    deviceService.setRestErrorHandler(errorHandler);

    userService = new UserService_(context);
    userService.setRestErrorHandler(errorHandler);

    venueService = new VenueService_(context);
    venueService.setRestErrorHandler(errorHandler);

    employeeService = new EmployeeService_(context);
    employeeService.setRestErrorHandler(errorHandler);

    dataService = new DataService(context);

    itemService = new ItemService_(context);
    itemService.setRestErrorHandler(errorHandler);

    orderService = new OrderService_(context);
    orderService.setRestErrorHandler(errorHandler);

    forgotPasswordService = new ForgotPasswordService_(context);
    forgotPasswordService.setRestErrorHandler(errorHandler);

    tableService = new TableService_(context);
    tableService.setRestErrorHandler(errorHandler);
  }

  public static void setSessionService(SessionService sessionService) {
    Log.d("RockServices", "Initializing SessionService");
    RockServices.sessionService = sessionService;
  }

  public static DeviceService getDeviceService() {
    if (deviceService == null) {
      throw new DeviceServiceNotInitializedException();
    }
    return deviceService;
  }

  public static UserService getUserService() {
    if (userService == null) {
      throw new UserServiceNotInitializedException();
    }
    return userService;
  }

  public static VenueService getVenueService() {
    if (venueService == null) {
      throw new VenueServiceNotInitializedException();
    }
    return venueService;
  }

  public static SessionService getSessionService() {
    if (sessionService == null) {
      throw new SessionServiceNotInitializedException();
    }
    return sessionService;
  }

  public static EmployeeService getEmployeeService() {
    if (employeeService == null) {
      throw new EmployeeServiceNotInitializedException();
    }
    return employeeService;
  }

  public static DataService getDataService() {
    if (dataService == null) {
      throw new DataServiceNotInitializedException();
    }
    return dataService;
  }

  public static ItemService getItemService() {
    if (itemService == null) {
      throw new ItemServiceNotInitializedException();
    }
    return itemService;
  }

  public static OrderService getOrderService() {
    if (orderService == null) {
      throw new OrderServiceNonInitializedException();
    }
    return orderService;
  }

  public static ManagerService getManagerService() {
    return managerService;
  }

  public static boolean isRegistered() {
    return getSessionService().restoreSession("device") != null;
  }

  public static void setManagerService(ManagerService managerService) {
    RockServices.managerService = managerService;
  }
}
