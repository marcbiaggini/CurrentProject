package com.rockspoon.rockandui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Interfaces.OnCameraCallback;
import com.rockspoon.rockandui.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 23/07/15.
 * TODO: Dynamic Preview Orientation / Size
 */
public class RSCamera extends ViewGroup implements SurfaceHolder.Callback {

  private final Context ctx;
  private final SurfaceView surfaceView;
  private final SurfaceHolder holder;
  private final List<Size> supportedPreviewSizes = new ArrayList<>();
  private Camera camera;
  private Size previewSize;
  private OnCameraCallback onCameraCallback;
  private Type cameraType;
  private int cameraId;

  private final Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
      if (onCameraCallback != null) {
        int angleToRotate = Tools.getCameraRotationAngle((Activity) ctx, cameraId);
        if (cameraType == Type.FRONT_CAMERA) {
          // compensate the mirror
          angleToRotate = 360 - angleToRotate;
        }

        final Bitmap originalImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        final Bitmap rotatedImage = BitmapTools.rotate(originalImage, angleToRotate);
        originalImage.recycle();
        onCameraCallback.onPictureTaken(rotatedImage);
      }
    }
  };

  public RSCamera(final Context ctx) {
    this(ctx, null);
  }

  public RSCamera(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public RSCamera(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, defStyle);

    this.ctx = ctx;
    this.surfaceView = new SurfaceView(ctx);
    addView(this.surfaceView);
    this.holder = this.surfaceView.getHolder();
    this.holder.addCallback(this);
    this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void openCamera(Type cameraType) {
    Camera cam = null;
    int numCams = Camera.getNumberOfCameras();

    for (int i = 0; i < numCams; i++) {
      final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
      Camera.getCameraInfo(i, cameraInfo);
      int cameraFacing = (cameraType == Type.FRONT_CAMERA) ? Camera.CameraInfo.CAMERA_FACING_FRONT :
          Camera.CameraInfo.CAMERA_FACING_BACK;

      if (cameraInfo.facing == cameraFacing) {
        cam = Camera.open(i);
        this.cameraType = cameraType;
        cameraId = i;
        break;
      }
    }

    if (cam == null && onCameraCallback != null) {
      onCameraCallback.onError(ERROR.CAMERA_NOT_AVAILABLE);
    } else {
      setCamera(cam);
    }
  }

  private void setCamera(Camera camera) {
    this.camera = camera;

    if (camera != null) {
      supportedPreviewSizes.addAll(camera.getParameters().getSupportedPreviewSizes());
      requestLayout();

      final Camera.Parameters params = camera.getParameters();
      final List<String> focusModes = params.getSupportedFocusModes();

      if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(params);
      }

      if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
      }

      surfaceCreated(holder);
      camera.startPreview();
    }
  }

  public void setOnCameraCallback(OnCameraCallback listener) {
    this.onCameraCallback = listener;
  }

  public void takePicture() {
    if (onCameraCallback != null) {
      camera.takePicture(null, null, jpegCallback);
    }
  }

  public void closeCamera() {
    if (camera != null) {
      camera.stopPreview();
      camera.release();
      camera = null;
    }
  }

  public void onPause() {
    if (camera != null) {
      camera.stopPreview();
    }
  }

  public void onResume() {
    if (camera != null) {
      camera.startPreview();
    }
  }

  public void surfaceCreated(SurfaceHolder holder) {
    try {
      if (camera != null) {
        camera.setPreviewDisplay(holder);
        int angleToRotate = Tools.getCameraRotationAngle((Activity) ctx, cameraId);
        camera.setDisplayOrientation(angleToRotate);
      }
    } catch (IOException exception) {
      if (onCameraCallback != null) {
        onCameraCallback.onError(ERROR.SURFACE_ERROR);
      }
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    if (camera != null) {
      camera.stopPreview();
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    if (camera != null) {
      int angleToRotate = Tools.getCameraRotationAngle((Activity) ctx, cameraId);
      camera.setDisplayOrientation(angleToRotate);

      Camera.Parameters parameters = camera.getParameters();
      parameters.setPreviewSize(previewSize.width, previewSize.height);
      requestLayout();

      camera.stopPreview();
      camera.setParameters(parameters);
      camera.startPreview();
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    setMeasuredDimension(width, height);

    if (supportedPreviewSizes != null) {
      previewSize = Tools.getOptimalPreviewSize(supportedPreviewSizes, width, height);
    }

    float ratio;
    if (previewSize.height >= previewSize.width) {
      ratio = (float) previewSize.height / (float) previewSize.width;
    } else {
      ratio = (float) previewSize.width / (float) previewSize.height;
    }

    setMeasuredDimension(width, (int) (width * ratio));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (changed && getChildCount() > 0) {
      getChildAt(0).layout(0, 0, r - l, b - t);
    }
  }

  public enum ERROR {
    SURFACE_ERROR,
    CAMERA_NOT_AVAILABLE
  }

  public enum Type {
    FRONT_CAMERA,
    BACK_CAMERA
  }


}
