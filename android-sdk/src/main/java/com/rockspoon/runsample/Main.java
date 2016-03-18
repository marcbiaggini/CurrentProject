package com.rockspoon.runsample;

public class Main {
/*
  @Inject
  UserService us;

  @Inject
  DeviceService ds;

  @Inject
  VenueService vs;

  @Inject
  PushNotificationService pns;

  @Inject
  ForgotPasswordService fs;

  public String readImage() {
    String content = null;
    try (final Scanner scanner = new Scanner(new File("./file.txt"))) {
      content = scanner.useDelimiter("\\Z").next();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return content;
  }

  public Promise<User, Error, Void> createUser(final String email, final String avatar) {
    User user = new User();
    user = user.withFirstName("Leo").withLastName("Bispo").withBirthday(new Date()).withEmail(email).withPassword("rct4+GeRk=");

    if (avatar != null) {
      user = user.withAvatar(new Image(avatar));
    }

    return us.create(user);
  }

  public Promise<Void, Error, Void> createVenue(final String owner) {
    Brand brand = new Brand();
    brand = brand.withName("My Name");
    // brand.setLogo(new Image(null, null, null, null, null, null,
    // readImage()));

    final Address addr = new Address(null, "Avenida Brigadeiro Faria Lima 244", null, "Sao Paulo", "Brazil", null, null, null, null, false, "Sao Paulo",
        "05426-200");

    final Venue venue = new Venue(null, addr, brand, null, null);

    return us.connect(() -> new DeferredObject<ImmutableCredentials, Error, Void>().resolve(new ImmutableCredentials(owner, "rct4+GeRk=", null, TokenType.LongTime))
        , null).then((DonePipe<User, Venue, Error, Void>) (user) -> vs.createGeneralInfo(venue)
    ).then((DonePipe<Venue, Void, Error, Void>) rVenue -> us.disconnect());
  }

  public void start() {

    final Device d = new Device().withQueue("aaef1ee3-c855-4693-9531-a026c1f03509");
    final Device d1 = new Device().withQueue("328f61e8-4b2a-4a5e-980a-4a30153f3ec9");
    /*
     * us.connect(() -> { return new DeferredObject<ImmutableCredentials, Error, Void>().resolve( new ImmutableCredentials("leonardob@rockspoon.com", "5485", null,
     * TokenType.LongTime)); }, new Device().withUid(
     * "eyJraWQiOiJrMSIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiJST0NLU1BPT04iLCJhdWQiOiJST0NLU1BPT05fVVNFUlMiLCJqdGkiOiJZc2o5WHhCZnZabEFNUWZ5eFhOOEFBIiwiaWF0IjoxNDUwNDUzMDQzLCJuYmYiOjE0NTA0NTI5MjMsInN1YiI6IjgiLCJsb2dpblR5cGUiOiJkZXZpY2UifQ.jpYLqZdf5vUJnJjamoPvtHRJ8-OF4SvRQvoyNaAlZG_Cp1NZ1OlSSbSjZK63DlFLE0j2FODMagrAZcDB37pDWx1HjUp6BkB50AQWbT3ToVklxFRRzmi84dMMUmeCbJx8WS0vThHn0sNntNAe6g_QYfTvx-NaKXXejgNkKZ4zpymKXpPQzRc3Yfmx-kaW7bgrqoDGT4gy1vSzcwO96DRNV4Sf42LFb5c14oC00E6SQbsq6sqtQJqxJprMNPvahATEGH4ZJ10CcJM8YXLmavG9ssuJiR1vRQyxkVXe3-Vc3XL2stjDXeJFUMSr43G82Deh4mbbC8A8TYYxH8HheLfowQ"
     * )).then(user -> { System.out.println(user); });
     */
/*
    ds.listSyncDb(new Venue().withId("1")).then(sync -> {
      NotificationManager manager = new NotificationManager();
      manager.on("Employee", ctx -> {
        System.out.println("THIS WAS CHANGED **********************" + ctx.getPayload());
      });

      NotificationManager manager1 = new NotificationManager();
      manager.on("Employee", ctx -> {
        System.out.println("THIS WAS CHANGED **********************" + ctx.getPayload());
      });

      pns.register(d, sync.get(0), manager);
      pns.register(d1, manager);
    }).fail(err -> {
      err.getCause().printStackTrace();
    });

    ds.listSyncDb(new Venue().withId("1")).then(sync -> {
      NotificationManager manager = new NotificationManager();
      manager.on("Employee", ctx -> {
        System.out.println("THIS WAS CHANGED IN THE SECOND **********************" + ctx.getPayload());
      });

      pns.register(d1, sync.get(0), manager);
    }).fail(err -> {
      err.getCause().printStackTrace();
    });

/*
    us.connect(() -> {
      return new DeferredObject<ImmutableCredentials, Error, Void>().resolve(new ImmutableCredentials("leonardob@rockspoon.com", "", null, TokenType.LongTime));
    } , null).then((DonePipe<User, ImmutableList<Device>, Error, Void>) (user) -> {
      return ds.list(new Venue().withId("1"));
    }).then((DonePipe<ImmutableList<Device>, Device, Error, Void>) devices -> {
      return ds.register(devices.get(0), new Venue().withId("1"));
    }).fail(error -> {
      System.out.println(error.getErrorCode().toString());
      if (error.getCause() != null)
        error.getCause().printStackTrace();
    });
  } */

  public static void main(final String[] args) {
    //final Main m = new Main();
    //AbstractRestClient.setApiServer("http://api.rockspoon.com:8080/rockspoon%s%s");
    //ObjectGraph.create(new MainModule()).inject(m);
    //m.start();
  }

}
