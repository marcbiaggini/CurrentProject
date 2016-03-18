package com.rockspoon.rockandui.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.rockspoon.models.image.ImageData;
import com.rockspoon.rockandui.R;
import com.rockspoon.rockandui.Tools;

/**
 * Created by Lucas Teske on 27/07/15.
 */
@Deprecated
public class MemberData extends ImageData implements Parcelable {
  @SuppressWarnings("unused")
  public static final Parcelable.Creator<MemberData> CREATOR = new Parcelable.Creator<MemberData>() {
    @Override
    public MemberData createFromParcel(Parcel in) {
      return new MemberData(in);
    }

    @Override
    public MemberData[] newArray(int size) {
      return new MemberData[size];
    }
  };
  private String name;
  private String memberSince;
  private int averageTime;
  private int lifetimeRevenue;
  private int averageTicket;
  private float rating;
  private boolean selected;
  private boolean free;

  public MemberData() {
    this.name = "Free";
    this.memberSince = "";
    this.averageTime = 0;
    this.lifetimeRevenue = 0;
    this.averageTicket = 0;
    this.imageResource = R.drawable.thumbnail;
    this.imageIsResource = true;
    this.imageUrl = "";
    this.rating = 0.f;
    this.free = true;
  }

  public MemberData(String name, String memberSince, int averageTime, int lifetimeRevenue, int averageTicket, float rating) {
    this.name = name;
    this.memberSince = memberSince;
    this.averageTime = averageTime;
    this.lifetimeRevenue = lifetimeRevenue;
    this.averageTicket = averageTicket;
    this.imageResource = R.drawable.thumbnail;
    this.imageIsResource = true;
    this.imageUrl = "";
    this.rating = rating;
    this.free = false;
  }

  public MemberData(String name, String memberSince, int averageTime, int lifetimeRevenue, int averageTicket, float rating, int imageResource) {
    this.name = name;
    this.memberSince = memberSince;
    this.averageTime = averageTime;
    this.lifetimeRevenue = lifetimeRevenue;
    this.averageTicket = averageTicket;
    this.imageResource = imageResource;
    this.imageIsResource = true;
    this.imageUrl = "";
    this.rating = rating;
    this.free = false;
  }

  public MemberData(String name, String memberSince, int averageTime, int lifetimeRevenue, int averageTicket, float rating, String imageFile) {
    this.name = name;
    this.memberSince = memberSince;
    this.averageTime = averageTime;
    this.lifetimeRevenue = lifetimeRevenue;
    this.averageTicket = averageTicket;
    this.imageUrl = imageFile;
    this.imageIsResource = true;
    this.rating = rating;
    this.free = false;
  }

  protected MemberData(Parcel in) {
    from(in);
    name = in.readString();
    memberSince = in.readString();
    averageTime = in.readInt();
    lifetimeRevenue = in.readInt();
    averageTicket = in.readInt();
    rating = in.readFloat();
    selected = in.readByte() != 0x00;
    free = in.readByte() != 0x00;
  }

  public boolean isSelected() {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getName() {
    return name;
  }

  public boolean isFree() {
    return free;
  }

  public String getMemberSince() {
    return memberSince;
  }

  public int getAverageTime() {
    return averageTime;
  }

  public String getAverageTimeString() {
    return Tools.timeToString(averageTime * 60);
  }

  public int getLifetimeRevenue() {
    return lifetimeRevenue;
  }

  public int getAverageTicket() {
    return averageTicket;
  }

  public float getRating() {
    return rating;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeString(name);
    dest.writeString(memberSince);
    dest.writeInt(averageTime);
    dest.writeInt(lifetimeRevenue);
    dest.writeInt(averageTicket);
    dest.writeFloat(rating);
    dest.writeByte((byte) (selected ? 0x01 : 0x00));
    dest.writeByte((byte) (free ? 0x01 : 0x00));
  }
}
