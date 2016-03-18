package com.rockspoon.rockpos.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.rockspoon.rockandui.BitmapTools;
import com.rockspoon.rockandui.Components.RSCamera;
import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.Dialogs.GenericWaitingDialog;
import com.rockspoon.rockandui.Interfaces.OnCameraCallback;
import com.rockspoon.rockandui.Objects.PhotoFilters.Amaro;
import com.rockspoon.rockandui.Objects.PhotoFilters.EarlyBird;
import com.rockspoon.rockandui.Objects.PhotoFilters.LomoFi;
import com.rockspoon.rockandui.RecyclerTools.RecyclerItemClickListener;
import com.rockspoon.rockpos.Camera.Adapters.PhotoFiltersAdapter;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 23/12/15.
 */
@EActivity(R.layout.camera_activity)
public class CameraActivity extends FragmentActivity {

  private RSCamera.Type currentCamera = RSCamera.Type.BACK_CAMERA;
  private RSDialog dialog;
  private PhotoFiltersAdapter photoFiltersAdapter;
  private List<PhotoFilter> filters;
  private Bitmap originalPhoto;
  private Bitmap filteredPhoto;

  @ViewById(R.id.camera_cameraholder)
  RSCamera rsCamera;

  @ViewById(R.id.camera_takepicturebtn)
  Button takePictureBtn;

  @ViewById(R.id.retake_photobtn)
  Button retakePhotobtn;

  @ViewById(R.id.use_photobtn)
  Button usePhotoBtn;

  @ViewById(R.id.camera_reversecamera)
  ImageView reverseCamera;

  @ViewById(R.id.camera_backbtn)
  ImageView backButton;

  @ViewById(R.id.filtersView)
  RecyclerView filtersView;

  @ViewById(R.id.filtersContainer)
  ViewGroup filtersContainer;

  @ViewById(R.id.resultView)
  ImageView resultView;

  @DimensionPixelSizeRes(R.dimen.filter_preview_size)
  int filterPreviewSize;

  private OnCameraCallback onCameraCallback = new OnCameraCallback() {
    @Override
    public void onPictureTaken(Bitmap image) {
      Log.d("Camera", "Picture taken!");
      rsCamera.closeCamera();
      encodeBitmap(image, encodedImage -> {
        filteredPhoto = originalPhoto = encodedImage;
        resultView.setImageBitmap(filteredPhoto);
        resultView.setVisibility(View.VISIBLE);
        rsCamera.setVisibility(View.GONE);
        filtersContainer.setVisibility(View.VISIBLE);

        if (dialog != null) {
          dialog.dismiss();
        }
      });
    }

    @Override
    public void onError(RSCamera.ERROR errorCode) {
      Log.e("Camera", "Error: " + errorCode.toString());
    }
  };

  private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
    @Override
    public void onItemClick(View childView, int position) {
      PhotoFilter selectedFilter = filters.get(position);
      applyFilter(selectedFilter);
    }

    @Override
    public void onItemLongPress(View childView, int position) {
      // do nothing
    }
  };

  @AfterViews
  protected void initViews() {
    rsCamera.setOnCameraCallback(onCameraCallback);
    rsCamera.openCamera(RSCamera.Type.BACK_CAMERA);

    filters = populateTestFilters();
    photoFiltersAdapter = new PhotoFiltersAdapter(this, filters);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

    filtersView.setAdapter(photoFiltersAdapter);
    filtersView.setLayoutManager(layoutManager);
    filtersView.addOnItemTouchListener(new RecyclerItemClickListener(this, onItemClickListener));
  }

  @Click(R.id.camera_backbtn)
  protected void goBack() {
    Intent returnIntent = getIntent();
    setResult(RESULT_CANCELED, returnIntent);

    if (dialog != null) {
      dialog.dismiss();
    }

    finish();
  }

  @Click(R.id.camera_takepicturebtn)
  protected void takePictureClick() {
    dialog = GenericWaitingDialog.newInstance();
    dialog.show(getFragmentManager(), "rsCameraWaiting");
    rsCamera.takePicture();
    takePictureBtn.setEnabled(false);
    retakePhotobtn.setVisibility(View.VISIBLE);
    usePhotoBtn.setVisibility(View.VISIBLE);
  }

  @Click(R.id.retake_photobtn)
  protected void retakePictureClick() {
    rsCamera.openCamera(currentCamera);
    takePictureBtn.setEnabled(true);
    retakePhotobtn.setVisibility(View.GONE);
    usePhotoBtn.setVisibility(View.GONE);
    resultView.setVisibility(View.GONE);
    rsCamera.setVisibility(View.VISIBLE);
    filtersContainer.setVisibility(View.GONE);
  }

  @Click(R.id.use_photobtn)
  protected void usePhotoClick() {
    CameraStorage.setImage(filteredPhoto);
    returnImage();
  }

  @Click(R.id.camera_reversecamera)
  protected void swapCamera() {
    if (currentCamera == RSCamera.Type.BACK_CAMERA) {
      rsCamera.closeCamera();
      rsCamera.openCamera(RSCamera.Type.FRONT_CAMERA);
      currentCamera = RSCamera.Type.FRONT_CAMERA;
      reverseCamera.setImageResource(R.drawable.ic_camera_rear_white_24dp);
    } else {
      rsCamera.closeCamera();
      rsCamera.openCamera(RSCamera.Type.BACK_CAMERA);
      currentCamera = RSCamera.Type.BACK_CAMERA;
      reverseCamera.setImageResource(R.drawable.ic_camera_front_white_24dp);
    }
  }

  @Background
  protected void encodeBitmap(Bitmap bitmap, EncodeListener callback) {
    Bitmap scaledBitmap = BitmapTools.resize(bitmap, rsCamera.getWidth(), rsCamera.getHeight());
    bitmap.recycle();

    CameraStorage.setImage(scaledBitmap);
    prepareFilterPreviews(scaledBitmap);

    onEncodeFinished(scaledBitmap, callback);
  }

  private void prepareFilterPreviews(Bitmap originalImage) {
    Bitmap scaledImage = BitmapTools.resize(originalImage, filterPreviewSize, filterPreviewSize);

    for (PhotoFilter filter : filters) {
      Bitmap preview = filter.applyFilter(scaledImage);
      filter.setPreviewImage(preview);
    }
  }

  @UiThread
  protected void onEncodeFinished(Bitmap result, EncodeListener callback) {
    photoFiltersAdapter.setFilters(filters);
    callback.onComplete(result);
  }

  @Override
  public void onDestroy() {
    try {
      rsCamera.closeCamera();
    } catch (RuntimeException e) {
      // Ignore, already closed.
    }
    super.onDestroy();
  }

  @UiThread
  protected void returnImage() {
    Intent returnIntent = getIntent();
    setResult(RESULT_OK, returnIntent);
    dialog.dismiss();
    finish();
  }

  private List<PhotoFilter> populateTestFilters() {
    List<PhotoFilter> testFilters = new ArrayList<>();

    testFilters.add(new PhotoFilter("No Filter", null));
    testFilters.add(new PhotoFilter("Amaro", new Amaro()));
    testFilters.add(new PhotoFilter("Early Bird", new EarlyBird()));
    testFilters.add(new PhotoFilter("Lomo Fi", new LomoFi()));

    return testFilters;
  }

  @Background
  protected void applyFilter(PhotoFilter filter) {
    Bitmap bitmap = filter.applyFilter(originalPhoto);
    finishFiltering(bitmap);
  }

  @UiThread
  protected void finishFiltering(Bitmap filteredPhoto) {
    this.filteredPhoto = filteredPhoto;
    resultView.setImageBitmap(filteredPhoto);
  }

  protected interface EncodeListener {
    void onComplete(Bitmap encodedImage);
  }
}
