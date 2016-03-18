package com.rockspoon.services.impl;

//public final class TransactionRequestTest extends PromiseTestHandler<ImmutableTransactionResponse> {
//
//  @Inject
//  IPaymentService paymentService;
//
//  //@Test
//  public void invalidAmountTest() throws InterruptedException {
//
//    final ImmutableCreditCard cc = new ImmutableCreditCard().withNumber("370497478433758").withExpDate("01/2017");
//    Money amount = Money.of(new BigDecimal(0.1), Currency.getInstance(Locale.getDefault()).getCurrencyCode());
//
//    Promise<ImmutableTransactionResponse, Errors, Void> response = paymentService.chargeCreditCard(cc, amount);
//    failCheck(response, new Errors("", ErrorCode.InvalidAmountForTransaction));
//  }
//
//  //@Test
//  public void invalidImmutableCreditCardTest() throws InterruptedException {
//
//    final ImmutableCreditCard cc = new ImmutableCreditCard().withNumber("3704974784337A").withExpDate("01/2017");
//    Money amount = Money.of(new BigDecimal(1), Currency.getInstance(Locale.getDefault()).getCurrencyCode());
//
//    Promise<ImmutableTransactionResponse, Errors, Void> response = paymentService.chargeCreditCard(cc, amount);
//    failCheck(response, new Errors("", ErrorCode.InvalidCreditCardNumber));
//  }
//
//  //@Test
//  public void successfulTransactionTest() throws InterruptedException {
//
//    final ImmutableCreditCard cc = new ImmutableCreditCard().withNumber("370497478433758").withExpDate("01/2017");
//    Money amount = Money.of(new BigDecimal(1), Currency.getInstance(Locale.getDefault()).getCurrencyCode());
//
//    Promise<ImmutableTransactionResponse, Errors, Void> response = paymentService.chargeCreditCard(cc, amount);
//    doneCheck(response);
//  }
//
//  @Override
//  protected void assertSucess(final ImmutableTransactionResponse result) {
//    Assert.assertEquals("AmericanExpress", result.getAccountType());
//    Assert.assertEquals("000000", result.getAuthCode());
//  }
//
//}
