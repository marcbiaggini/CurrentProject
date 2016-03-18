package com.rockspoon.models.user;

import android.content.Context;

import com.rockspoon.models.image.Image;
import com.rockspoon.models.image.ImageData;
import com.rockspoon.models.image.ImageData_;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.sdk.R;

/**
 * Created by lucas on 06/07/15.
 */
public class LoginUserData {

  private final Employee employeeData;

  private final ImageData_ loAvatar;

  private final ImageData_ noAvatar;

  private final ImageData_ hiAvatar;

  public LoginUserData(final Context ctx, final Employee employeeData) {
    this.employeeData = employeeData;
    Image avatar = employeeData.getUser().getAvatar();
    loAvatar = ImageData_.getInstance_(ctx);
    noAvatar = ImageData_.getInstance_(ctx);
    hiAvatar = ImageData_.getInstance_(ctx);
    if (avatar != null) {
      loAvatar.from(avatar.getLoResolution().getUrl());
      noAvatar.from(avatar.getNoResolution().getUrl());
      hiAvatar.from(avatar.getHiResolution().getUrl());
    } else {
      loAvatar.from(R.drawable.thumbnail);
      noAvatar.from(R.drawable.thumbnail);
      hiAvatar.from(R.drawable.thumbnail);
    }
  }

  public String getFullName() {
    return employeeData.getUser().getFirstName() + " " + employeeData.getUser().getLastName();
  }

  public Employee getEmployeeData() {
    return employeeData;
  }

  public User getUserData() {
    return employeeData.getUser();
  }

  public ImageData getLoAvatar() {
    return loAvatar;
  }

  public ImageData getNoAvatar() {
    return noAvatar;
  }

  public ImageData getHiAvatar() {
    return hiAvatar;
  }
}
