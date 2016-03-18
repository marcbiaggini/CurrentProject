package com.rockspoon.tests;

import java.util.Date;

import javax.inject.Inject;

import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.rockspoon.error.Error;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.credentials.TokenType;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.user.User;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

public class UserLoginTest extends PromiseTest {

  @Inject
  UserService us;

  public UserLoginTest(final RockspoonSuiteTest suite) {
    super(suite);
  }

  public void run() {
    ObjectGraph.create(new DummyConnection()).inject(this);

    System.out.println("Running UserLoginTest");

    createUser().then(testUserLogin).then(readUserAndDisconnect).then(lastSentence).fail(genericError);

    lockThread();
    finishTest();
  }

  /**
   * Creates an the test user.
   */
  private Promise<User, Error, Void> createUser() {
    final User user = new User().withFirstName("Test").withLastName("User").withBirthday(new Date()).withEmail(RockspoonSuiteTest.userEmail)
        .withPassword(RockspoonSuiteTest.userPassword).withAvatar(new Image(suite.readImage()));
    System.out.println("Testing user creation...");
    return us.create(user);
  }

  /**
   * Tests the created user Login in Login API
   */
  private DonePipe<User, User, Error, Void> testUserLogin = new DonePipe<User, User, Error, Void>() {

    @Override
    public Promise<User, Error, Void> pipeDone(final User user) {
      System.out.println("Testing user login...");
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
   * Stores the logged user in Test Suite and disconnects.
   */
  private DonePipe<User, Void, Error, Void> readUserAndDisconnect = new DonePipe<User, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final User user) {
      System.out.println("Logged in with " + user.getFirstName());
      suite.setCreatedUser(user);
      return us.disconnect();
    }
  };

  @Module(library = true, complete = false, includes = ServiceManager.class, injects = UserLoginTest.class)
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
