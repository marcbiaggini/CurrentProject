package com.rockspoon.tests;

import javax.inject.Inject;

import org.jdeferred.DeferredManager;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;

import com.google.common.collect.ImmutableList;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.rockspoon.error.Error;
import com.rockspoon.models.address.Address;
import com.rockspoon.models.credentials.ImmutableCredentials;
import com.rockspoon.models.credentials.TokenType;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.brand.Brand;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

public class CreateVenueTest extends PromiseTest {

  @Inject
  UserService us;

  @Inject
  VenueService vs;

  private Brand brand = new Brand();
  private Venue venue = new Venue();
  private final Address addr = new Address(null, "Avenida Brigadeiro Faria Lima 244", null, "Sao Paulo", "Brazil", null, null, null, null, false, "Sao Paulo",
      "05426-200");

  public CreateVenueTest(final RockspoonSuiteTest suite) {
    super(suite);
  }

  public void run() {
    ObjectGraph.create(new DummyConnection()).inject(this);
    System.out.println("Running CreateVenueTest");

    final DeferredManager dm = new DefaultDeferredManager();

    dm.when(new Runnable() {

      @Override
      public void run() {
      }
    }).then(fillData).then(loginTestUser).then(createGeneralInfo).then(getUserVenues).then(disconnectUser).then(lastSentence).fail(genericError);

    lockThread();
    finishTest();
  }

  /**
   * Fills brand, operating entity, holding company and venue data
   */
  private DonePipe<Void, Void, Error, Void> fillData = new DonePipe<Void, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Void unused) {
      System.out.println("Filling brand data");
      brand = brand.withName("My Name");
      brand = brand.withLogo(new Image(suite.readImage()));

      System.out.println("Filling venue data");
      venue = venue.withBrand(brand);
      venue = venue.withAddress(addr);

      return new DeferredObject<Void, Error, Void>().resolve(null);
    }
  };

  /**
   * Get user venues
   */
  private DonePipe<Venue, Void, Error, Void> getUserVenues = new DonePipe<Venue, Void, Error, Void>() {

    @Override
    public Promise<Void, Error, Void> pipeDone(final Venue unused) {
      return us.fetchVenues().then(new DonePipe<ImmutableList<Venue>, Void, Error, Void>() {

        @Override
        public Promise<Void, Error, Void> pipeDone(final ImmutableList<Venue> venues) {
          System.out.println("User Venues(" + venues.size() + "): ");
          for (Venue venue : venues) {
            System.out.println("Venue: " + venue.getId());
          }
          return new DeferredObject<Void, Error, Void>().resolve(null);
        }
      });
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
   * Creates General Information for Venue
   */
  private DonePipe<User, Venue, Error, Void> createGeneralInfo = new DonePipe<User, Venue, Error, Void>() {

    @Override
    public Promise<Venue, Error, Void> pipeDone(final User user) {
      System.out.println("Creating general info");
      return vs.createGeneralInfo(venue);
    }
  };

  @Module(library = true, complete = false, includes = ServiceManager.class, injects = CreateVenueTest.class)
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
