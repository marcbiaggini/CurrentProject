package com.rockspoon.services;

//public abstract class PromiseTestHandler<T> {
//
//  @Module(includes = ServiceManager.class, injects = { TransactionRequestTest.class,
//      RefundTransactionRequestTest.class }, overrides = true, complete = false)
//  public class TestModule {
//    public TestModule() {
//    }
//  }
//
//  @Before
//  public void prepareService() {
//    ObjectGraph.create(new TestModule()).inject(this);
//  }
//
//  private CountDownLatch countDown;
//
//  protected void prepareCountDown() {
//    this.countDown = new CountDownLatch(1);
//  }
//
//  protected final void checkCountDown() {
//    try {
//      this.countDown.await(15, TimeUnit.SECONDS);
//    } catch (InterruptedException e) {
//      Assert.assertFalse(e.getMessage(), true);
//    }
//    Assert.assertEquals(0, this.countDown.getCount());
//  }
//
//  public void failCheck(final Promise<?, Errors, ?> promise, final Errors expectedError) {
//    prepareCountDown();
//
//    promise.fail(new FailCallback<Errors>() {
//      @Override
//      public void onFail(final Errors result) {
//        assertFail(expectedError, result);
//        countDown.countDown();
//      }
//    });
//
//    checkCountDown();
//    Assert.assertTrue(promise.isRejected());
//  }
//
//  protected void assertFail(final Errors expectedError, final Errors result) {
//    Assert.assertEquals("Amount of errors don't match expectation", expectedError.getErrorCodes().size(),
//        result.getErrorCodes().size());
//    Assert.assertThat(result, new BaseMatcher<Errors>() {
//
//      @Override
//      public boolean matches(final Object item) {
//        return result.getErrorCodes().containsAll(expectedError.getErrorCodes())
//            && result.getErrorCodes().size() == expectedError.getErrorCodes().size();
//      }
//
//      @Override
//      public void describeTo(final Description description) {
//        description.appendValue(result.getErrorCodes());
//      }
//    });
//  }
//
//  public void doneCheck(final Promise<T, Errors, ?> promise) {
//    prepareCountDown();
//
//    promise.done(new DoneCallback<T>() {
//      @Override
//      public void onDone(final T result) {
//        assertSucess(result);
//        countDown.countDown();
//      }
//    });
//
//    checkCountDown();
//    Assert.assertTrue(promise.isResolved());
//  }
//
//  protected abstract void assertSucess(T result);
//}
