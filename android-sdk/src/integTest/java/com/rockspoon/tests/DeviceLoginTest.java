package com.rockspoon.tests;

import javax.inject.Inject;

import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.rockspoon.error.Error;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.credentials.TokenType;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.device.Device;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

public class DeviceLoginTest extends PromiseTest {

  @Inject
  IDeviceService ds;

  @Inject
  UserService us;

  public DeviceLoginTest(final RockspoonSuiteTest suite) {
    super(suite);
  }

  public void run() {
    ObjectGraph.create(new DummyConnection()).inject(this);
    System.out.println("Running DeviceLoginTest");

    lockThread();
    finishTest();
  }

  /**
   * Logins as Test User
   */
  private Promise<User, Error, Void> loginTestUser() {
    return us.connect(new UserCredentialsDelegate() {

      @Override
      public Promise<ImmutableCredentials, Error, Void> requestUserCredentials() {
        System.out.println("Logging in with test user");
        return new DeferredObject<ImmutableCredentials, Error, Void>()
            .resolve(new ImmutableCredentials(RockspoonSuiteTest.userEmail, RockspoonSuiteTest.userPassword, null, TokenType.LongTime));
      }
    }, null);
  }

  /**
   * Check device login
   */
  private DonePipe<Device, Void, Error, Void> checkDeviceLogin = new DonePipe<Device, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Device loggedDevice) {
      System.out.println("Logged in");
      assertNotNull("Device is null from backend!", loggedDevice);
      assertNotNull("Device name is null from backend!", loggedDevice.getName());
      assertNotNull("Device Subtype is null!", loggedDevice.getType());

      System.out.println("Logged Device name: " + loggedDevice.getName());
      System.out.println("Logged Device ID: " + loggedDevice.getId());
      System.out.println("Logged Device Type ID: " + loggedDevice.getType().getId());
      System.out.println("Logged Device Type Type: " + loggedDevice.getType().getLabel());
      System.out.println("Logged Device Label: " + loggedDevice.getType().getLabel());

      suite.setLoggedDevice(loggedDevice);
      return new DeferredObject<Void, Error, Void>().resolve(null);
    }
  };

  /**
   * Disconnects the logged user
   */
  private DonePipe<Void, Void, Error, Void> disconnectUser = new DonePipe<Void, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Void unused) {
      return us.disconnect();
    }
  };

  @Module(library = true, complete = false, includes = ServiceManager.class, injects = DeviceLoginTest.class)
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
