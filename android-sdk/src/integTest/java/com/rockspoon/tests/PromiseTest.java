package com.rockspoon.tests;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.junit.Assert;

import com.rockspoon.error.Error;

public class PromiseTest {

  private static final int PROMISE_TIMEOUT = 300; // Use no more than 15 seconds in production environment

  private CountDownLatch countDown = new CountDownLatch(1);
  private String message = "";
  private boolean failed = false;

  protected RockspoonSuiteTest suite;

  public PromiseTest(final RockspoonSuiteTest suite) {
    this.suite = suite;
  }

  public final DoneCallback<Void> lastSentence = new DoneCallback<Void>() {

    @Override
    public void onDone(final Void result) {
      unlockThread();
    }
  };

  public final FailCallback<Error> genericError = new FailCallback<Error>() {

    @Override
    public void onFail(final Error error) {
      error.printStackTrace();
      fail(error.getLocalizedMessage());
    }
  };

  public void lockThread() {
    try {
      this.countDown.await(PROMISE_TIMEOUT, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      Assert.assertFalse(e.getMessage(), true);
    }
  }

  public void unlockThread() {
    countDown.countDown();
  }

  public void fail(final String message) {
    this.failed = true;
    this.message = message;
    unlockThread();
  }

  public void finishTest() {
    if (failed) {
      Assert.fail(message);
    }
  }

  public boolean isFailed() {
    return failed;
  }

  public void assertEquals(final String message, final Object expected, final Object actual) {
    if (expected != null) {
      if (!expected.equals(actual))
        fail(message);
    } else if (actual != null) {
      if (!actual.equals(expected))
        fail(message);
    }
  }

  public void assertEquals(final String message, final double expected, final double actual) {
    if (expected != actual)
      fail(message);
  }

  public void assertEquals(final String message, final float expected, final float actual) {
    if (expected != actual)
      fail(message);
  }

  public void assertEquals(final String message, final long expected, final long actual) {
    if (expected != actual)
      fail(message);
  }

  public void assertEquals(final String message, final int expected, final int actual) {
    if (expected != actual)
      fail(message);
  }

  public void assertNull(final String message, final Object object) {
    if (object != null)
      fail(message);
  }

  public void assertEmpty(final String message, final String data) {
    if (data != null && data.length() > 0)
      fail(message);
  }

  public void assertNotEquals(final String message, final Object unexpected, final Object actual) {
    if (unexpected != null) {
      if (unexpected.equals(actual))
        fail(message);
    } else if (actual != null) {
      if (actual.equals(unexpected))
        fail(message);
    }
    fail(message);
  }

  public void assertNotEquals(final String message, final double unexpected, final double actual) {
    if (unexpected == actual)
      fail(message);
  }

  public void assertNotEquals(final String message, final float unexpected, final float actual) {
    if (unexpected == actual)
      fail(message);
  }

  public void assertNotEquals(final String message, final long unexpected, final long actual) {
    if (unexpected == actual)
      fail(message);
  }

  public void assertNotEquals(final String message, final int unexpected, final int actual) {
    if (unexpected == actual)
      fail(message);
  }

  public void assertNotNull(final String message, final Object object) {
    if (object == null)
      fail(message);
  }

  public void assertNotEmpty(final String message, final String data) {
    if (data == null || data.length() == 0)
      fail(message);
  }

}
