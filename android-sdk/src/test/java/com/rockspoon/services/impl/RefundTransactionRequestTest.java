package com.rockspoon.services.impl;

//public final class RefundTransactionRequestTest extends PromiseTestHandler<ImmutableTransactionResponse> {
//
//  @Inject
//  IPaymentService paymentService;
//
//  //@Test
//  public void successfulRefundAmountTest() throws InterruptedException {
//    final ImmutableCreditCard cc = new ImmutableCreditCard().withNumber("370497478433758").withExpDate("01/2017");
//    Money refundAmount = Money.of(new BigDecimal(1), Currency.getInstance(Locale.getDefault()).getCurrencyCode());
//
//    Promise<ImmutableTransactionResponse, Errors, Void> refundResponse = paymentService.refundCreditCard(cc, refundAmount, "ABCDEF");
//    doneCheck(refundResponse);
//  }
//
//  //@Test
//  public void invalidCreditCardNumberTest() throws InterruptedException {
//    final ImmutableCreditCard cc = new ImmutableCreditCard().withNumber("37049747843375A").withExpDate("01/2017");
//    Money refundAmount = Money.of(new BigDecimal(1), Currency.getInstance(Locale.getDefault()).getCurrencyCode());
//
//    Promise<ImmutableTransactionResponse, Errors, Void> refundResponse = paymentService.refundCreditCard(cc, refundAmount, null);
//    failCheck(refundResponse, new Errors("", ErrorCode.InvalidCreditCardNumber));
//  }
//
//  @Override
//  protected void assertSucess(final ImmutableTransactionResponse result) {
//    Assert.assertEquals("AmericanExpress", result.getAccountType());
//    Assert.assertEquals("000000", result.getAuthCode());
//  }
//
//}
