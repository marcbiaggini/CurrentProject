package com.rockspoon.tests;

import javax.inject.Inject;

import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.credentials.TokenType;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.device.DeviceType;
import com.rockspoon.error.Error;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

public class CreateDeviceTest extends PromiseTest {

  @Inject
  IDeviceService ds;

  @Inject
  UserService us;

  Device iDevice;

  public CreateDeviceTest(final RockspoonSuiteTest suite) {
    super(suite);
  }

  public void run() {
    ObjectGraph.create(new DummyConnection()).inject(this);
    System.out.println("Running CreateDeviceTest");

    lockThread();
    finishTest();
  }

  /**
   * Fills the device with test data
   */
  public Promise<Void, Error, Void> fillDevice() {
    final Deferred<Void, Error, Void> defer = new DeferredObject<>();
    final DeviceType type = new DeviceType().withId("4").withLabel("Generic POS").withNeedsStation(false).withNeedsLocation(false);

    System.out.println("Filling a new device with test data");
    final Device device = new Device().withName("Test POS").withType(type).withVersion("TestV0.1")
        .withMetadata(new ImmutableMap.Builder<String, String>().put("cellularImei", "382.018.354.628.982").put("cellularSignal", "3").put("wirelessSignal", "3")
            .put("cellularNetwork", "LTE").put("cellularOperator", "Verizon").build());

    iDevice = device;

    return defer.resolve(null);
  }

  /**
   * Disconnects the logged user
   */
  private DonePipe<Void, Void, Error, Void> disconnectUser = new DonePipe<Void, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Void unused) {
      return us.disconnect();
    }
  };

  /**
   * Logins as Test User
   */
  private DonePipe<Void, User, Error, Void> loginTestUser = new DonePipe<Void, User, Error, Void>() {

    @Override
    public Promise<User, Error, Void> pipeDone(final Void result) {
      return us.connect(new UserCredentialsDelegate() {

        @Override
        public Promise<ImmutableCredentials, Error, Void> requestUserCredentials() {
          return new DeferredObject<ImmutableCredentials, Error, Void>()
              .resolve(new ImmutableCredentials(RockspoonSuiteTest.userEmail, RockspoonSuiteTest.userPassword, null, TokenType.LongTime));
        }
      }, null);
    }
  };

  /**
   * Receives the device and sets at RockspoonSuite
   */
  private DonePipe<Device, Void, Error, Void> checkDevice = new DonePipe<Device, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Device device) {
      System.out.println("Received the device");

      assertNotNull("Device came null from backend!", device);
      assertNotNull("Device ID came null from backend!", device.getId());
      assertNotNull("Device UID came null from backend!", device.getUid());
      assertNotEmpty("Device ID is empty!", device.getId());
      assertNotEmpty("Device UID is empty!", device.getUid());

      System.out.println("DeviceID: " + device.getId().toString());
      System.out.println("Device UID: " + device.getUid());
      System.out.println("Device name: " + device.getName());

      suite.setDevice(device);

      return new DeferredObject<Void, Error, Void>().resolve(null);
    }
  };

  /**
   * List venue devices
   */
  private DonePipe<Void, Void, Error, Void> getVenueDevices = new DonePipe<Void, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Void unused) {
      System.out.println("Getting venue Devices");
      return ds.list(suite.getVenue()).then(new DonePipe<ImmutableList<Device>, Void, Error, Void>() {

        @Override
        public Promise<Void, Error, Void> pipeDone(final ImmutableList<Device> devices) {
          System.out.println("Venue Devices(" + devices.size() + "): ");
          for (Device device : devices) {
            System.out.println("  Device ID: " + device.getId());
            System.out.println("  Device UID: " + device.getUid());
            System.out.println("  Device Name: " + device.getName());
          }
          return new DeferredObject<Void, Error, Void>().resolve(null);
        }
      });
    }
  };

  @Module(library = true, complete = false, includes = ServiceManager.class, injects = CreateDeviceTest.class)
  public class DummyConnection {

    @Provides
    public IConnectionSourceFactory provideSourceFactory() {
      return new IConnectionSourceFactory() {

        @Override
        public ConnectionSource getConnectionSource() {
          try {
            return new JdbcConnectionSource("jdbc:sqlite:./myDb.db");
          } catch (java.sql.SQLException e) {
            fail(e.getLocalizedMessage());
          }

          return null;
        }
      };
    }
  }
}
