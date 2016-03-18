package com.rockspoon.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.device.Device;

public class RockspoonSuiteTest {

  @Test
  public void run() {
    final UserLoginTest userLoginTest = new UserLoginTest(this);
    final CreateVenueTest createVenueTest = new CreateVenueTest(this);
    final CreateDeviceTest createDeviceTest = new CreateDeviceTest(this);
    final DeviceLoginTest deviceLoginTest = new DeviceLoginTest(this);

    userLoginTest.run();
    createVenueTest.run();
    createDeviceTest.run();
    deviceLoginTest.run();
  }

  // Test Variables
  private User createdUser;
  private Venue venue;
  private Device device;
  private Device loggedDevice;

  // Getters / Setters

  public synchronized Venue getVenue() {
    return venue;
  }

  public synchronized void setVenue(final Venue venue) {
    this.venue = venue;
  }

  public synchronized void setDevice(final Device device) {
    this.device = device;
  }

  public synchronized Device getDevice() {
    return this.device;
  }

  public synchronized User getCreatedUser() {
    return this.createdUser;
  }

  public synchronized void setCreatedUser(final User createdUser) {
    this.createdUser = createdUser;
  }

  public synchronized Device getLoggedDevice() {
    return loggedDevice;
  }

  public synchronized void setLoggedDevice(final Device loggedDevice) {
    this.loggedDevice = loggedDevice;
  }

  // Static methods for Tests

  public String readImage() {
    String content = null;
    try (final Scanner scanner = new Scanner(new File("./mini.txt"))) {
      content = scanner.useDelimiter("\\Z").next();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return content;
  }

  // Static Constants for Tests
  public static final String adminUser = "admin@rockspoon.com";
  public static final String adminPass = "rct4+GeRk=";
  public static final String userEmail = "test@test.com";
  public static final String userPassword = "rct4+GeRk=";

}
