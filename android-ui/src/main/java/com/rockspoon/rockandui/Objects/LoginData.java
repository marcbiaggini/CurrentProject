package com.rockspoon.rockandui.Objects;

/**
 * Created by lucas on 17/07/15.
 */
public class LoginData {

  private String inputPassword;
  private String userEmail;

  // TODO: Remove this when integrate with SDK
  public LoginData() {
    this("test@test.com");
  }

  public LoginData(String userEmail) {
    this.userEmail = userEmail;
    inputPassword = "";
  }

  public String getInputPassword() {
    return this.inputPassword;
  }

  public void setInputPassword(String inputPassword) {
    this.inputPassword = inputPassword;
  }

  public String getUserEmail() {
    return this.userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }
}
