RockSpoon Java SDK
==================

Java abstraction layer to connect/use the RockSpoon backend services. By using
the rockspoon-java-sdk you will be able to easily connect to the printer
notification service and use all our REST sdk without the need to right HTTP/S
calls. It also implements the OAuth2 client authentication.

Push Notification service (PNS)
-------------------------------

The push notification service is our aim to create a one way channel connecting
our Backend Servers to the java clients, allowing us to notify events happening
on the server to the client.

For perfomance reasons, we will use this channel only to notify users about events
and not to transport the data itself.

Whenever the user wants to send/retrieve the data from our backend servers, it
will have to do a REST call.

Following you can take a look on how to use the PNS:

```
import com.rockspoon.printer.pushnotification.NotificationContext;
import com.rockspoon.printer.pushnotification.NotificationListener;
import com.rockspoon.printer.pushnotification.NotificationManager;
import com.rockspoon.printer.services.ServicesManager;
import com.rockspoon.ssl.JKSKeyFactory;

public class PushNotificationSample {
  private static final String TOKEN = "00000164b5ed3b602445628ee7211261052ba98650d32b49fcc403de6df8c7918a831a";
  void start() {
    ServicesManager.getInstance().setSSLKeyFactory(new JKSKeyFactory());
    final NotificationManager manager = new NotificationManager();

    manager.addListener("printer.event1", new NotificationListener() {
      @Override
      public void notificationReceived(final String payload) {
      }
    });

    manager.addListener("printer.event2", new NotificationListener() {
      @Override
      public void notificationReceived(final String payload) {
      }
    });

    ServicesManager.getInstance().getPushNotificationService().register(TOKEN, manager);
  }

  public static void main(String[] args) {
    new PushNotificationSample().start();
  }
}
```

Whenever you want to listen for an server event you must create a new notification
listener.

Registering a new Device
------------------------

To make use of the PNS, the device must be registered in the backend.
Whenever you register a new device, the Backend will return a new token that can
be used to start listen for push notification events.

Following you can take a look on how to register a new device:

```
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.rockspoon.printer.models.ImmutablePrinterInfo;
import com.rockspoon.printer.models.ImmutablePrinterRegistation;
import com.rockspoon.printer.services.ConnectionSourceFactory;
import com.rockspoon.printer.services.PrinterService;
import com.rockspoon.printer.services.ServiceHandler;
import com.rockspoon.printer.services.ServicesManager;
import com.rockspoon.rest.models.RestError;

public class RegisterSample {
  public static final String UID = "IMEI";
  public static final String ONE_TIME_REGISTRATION_NUMBER = "564894";

  public void start() {
    ServicesManager.getInstance().setConnectionSourceFactory(new ConnectionSourceFactory() {
      @Override
      public ConnectionSource getConnectionSource() {
        try {
          return new JdbcConnectionSource("jdbc:sqlite:./myDb.db");
        } catch (java.sql.SQLException e) {}

        return null;
      }
    });

    final PrinterService ps = ServicesManager.getInstance().getPrinterService();
    if (!ps.isRegistered()) {
      ps.register(new ImmutablePrinterRegistation(UID, ONE_TIME_REGISTRATION_NUMBER), new ServiceHandler<ImmutablePrinterInfo>() {

        @Override
        public void completed(final RestError error, final ImmutablePrinterInfo result) {
          if (error == null) {
            //README: Now you have the printer information.
          } else
            error.printStackTrace();
        }
      });
    }
  }

  public static void main(String[] args) {
    new RegisterSample().start();
  }
}
```

The `ONE_TIME_REGISTRATION_NUMBER` constant is a one time token generated when
you register a new printer to the backoffice and it will be used to link the device
to the backoffice. If the call has been successfuly executed you will receive in the
result all informations about the printer and the error variable will be null, otherwise,
the error will have information about it.

Accessing the Printer Service
-----------------------------

When you use the RockSpoon SDK, you don't need to care anymore about HTTP calls.
The SDK abstract the OAuth2 and the REST calls.

Following you can find an example on how to use the PrinterService:

```
import java.nio.channels.AlreadyConnectedException;

import com.rockspoon.printer.exception.NotRegisteredException;
import com.rockspoon.printer.models.ImmutablePrinterJob;
import com.rockspoon.printer.services.PrinterService;
import com.rockspoon.printer.services.ServiceHandler;
import com.rockspoon.printer.services.ServicesManager;
import com.rockspoon.printer.services.SessionService;
import com.rockspoon.rest.models.RestError;

public class PrinterServiceSample {
  public void start() {
    final PrinterService ps = ServicesManager.getInstance().getPrinterService();

    if (!ps.isRegistered()) {
      //README: You should call here the registration process
    } else
      connect();
  }

  public void connect() {
    final PrinterServiceSample self = this;
    final SessionService ss = ServicesManager.getInstance().getSessionService();
    try {
      ss.connect(new ServiceHandler<String>() {
        @Override
        public void completed(RestError error, String token) {
         //README: You should connect to the PNS here using the token.
         self.fetchJob();
        }
      });
    } catch (AlreadyConnectedException e) {
      e.printStackTrace();
    } catch (NotRegisteredException e) {
      e.printStackTrace();
    }
  }

  public void fetchJob() {
    ServicesManager.getInstance().getPrinterService().fetchJob("123", new ServiceHandler<ImmutablePrinterJob>() {
      @Override
      public void completed(RestError error, ImmutablePrinterJob job) {
        //README: Now you can process the job and call the printer to print it.
      }
    });
  }

  public static void main(String[] args) {
    new PrinterServiceSample().start();
  }
}
```

Basically we are using 2 services. The first one is the Session Service. It will
be responsible to authenticate and to keep the printer logged into the backend.

The second one is the PrinterService. It will be used to send/retrieve information
from the backend.
