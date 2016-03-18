package com.rockspoon.models.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.rockspoon.helpers.CachedHTTP;
import com.rockspoon.sdk.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.lang.ref.WeakReference;

/**
 * Created by Lucas Teske on 27/07/15.
 */
@EBean
public class ImageData implements Parcelable {

  private static final String TAG = "ImageData";

  protected int imageResource;
  protected String imageUrl;
  protected Boolean imageIsResource;
  private boolean downloadRunning = false;

  protected ImageData() {
  }

  public ImageData from(Parcel in) {
    imageResource = in.readInt();
    imageUrl = in.readString();
    byte imageIsResourceVal = in.readByte();
    imageIsResource = imageIsResourceVal == 0x02 ? null : imageIsResourceVal != 0x00;
    return this;
  }

  public ImageData from(int imageResource) {
    this.imageResource = imageResource;
    this.imageIsResource = true;
    this.imageUrl = "";
    return this;
  }

  public ImageData from(String imageUrl) {
    this.imageUrl = imageUrl;
    this.imageResource = R.drawable.thumbnail;
    this.imageIsResource = false;
    return this;
  }

  public Drawable getImage(final Context ctx, final ImageView imageView) {
    if (imageIsResource) {
      return ctx.getResources().getDrawable(imageResource);
    } else {
      if (!downloadRunning) {
        downloadImage(imageView != null ? new WeakReference<>(imageView) : null); //  Start download if it wasn't started
      }
      return ctx.getResources().getDrawable(R.drawable.thumbnail);
    }
  }

  public void downloadImage(final WeakReference<ImageView> imageViewReference) {
    if (!downloadRunning) {
      downloadRunning = true;
      downloadImageData(imageUrl, imageViewReference);
    }
  }

  public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
    public ImageData createFromParcel(Parcel in) {
      return new ImageData().from(in);
    }

    public ImageData[] newArray(int size) {
      return new ImageData[size];
    }
  };

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(imageResource);
    dest.writeString(imageUrl);
    if (imageIsResource == null) {
      dest.writeByte((byte) (0x02));
    } else {
      dest.writeByte((byte) (imageIsResource ? 0x01 : 0x00));
    }
  }

  @Background
  void downloadImageData(final String imageUrl, final WeakReference<ImageView> imageViewReference) {
    final Bitmap bmp = CachedHTTP.getCachedImageFromURL(imageUrl);
    onImageDownloaded(bmp, imageViewReference);
  }

  @UiThread
  void onImageDownloaded(final Bitmap result, final WeakReference<ImageView> imageViewReference) {
    if (result == null)
      Log.e(TAG, "Cannot download bitmap.");
    else {
      if (imageViewReference != null && imageViewReference.get() != null) {
        imageViewReference.get().setImageBitmap(result);
      }
    }
    downloadRunning = false;
  }

  @Override
  public int describeContents() {
    return 0;
  }

}
