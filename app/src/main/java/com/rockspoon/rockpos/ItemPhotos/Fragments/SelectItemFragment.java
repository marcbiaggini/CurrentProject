package com.rockspoon.rockpos.ItemPhotos.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.rockspoon.error.Error;
import com.rockspoon.error.ErrorCode;
import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.venue.item.Item;
import com.rockspoon.models.venue.menu.MenuCategory;
import com.rockspoon.rockandui.Adapters.CategoryAdapter;
import com.rockspoon.rockandui.Adapters.FoodItemsAdapter;
import com.rockspoon.rockandui.Components.LinearListView;
import com.rockspoon.rockandui.Components.RSBaseFragment;
import com.rockspoon.rockandui.Objects.CategoryData;
import com.rockspoon.rockpos.Camera.CameraActivity;
import com.rockspoon.rockpos.Camera.CameraActivity_;
import com.rockspoon.rockpos.Camera.CameraStorage;
import com.rockspoon.rockpos.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lucas on 17/12/15.
 */
@EFragment
public class SelectItemFragment extends RSBaseFragment {

  private final int CAMERA_RESULT_CODE = 100;
  protected boolean withPhotos = false;

  @ViewById(R.id.itemphotos_withphotos_btn)
  protected Button withPhotosButton;

  @ViewById(R.id.itemphotos_withoutphotos_btn)
  protected Button withoutPhotosButton;

  @ViewById(R.id.itemphotos_category_bar)
  protected LinearListView categoryBar;

  @ViewById(R.id.itemphotos_items)
  protected RecyclerView itemList;

  private int selectedCategory = 0;
  private CategoryAdapter categoryAdapter;

  public static SelectItemFragment newInstance() {
    return new SelectItemFragment_();
  }
  private List<MenuCategory> menuCategoriesWithPhotos = new ArrayList<>();
  private List<MenuCategory> menuCategoriesWithoutPhotos = new ArrayList<>();

  @Click(R.id.itemphotos_withphotos_btn)
  protected void withPhotosButtonClick() {
    withPhotos = true;

    withPhotosButton.setEnabled(false);
    withPhotosButton.setBackgroundResource(R.drawable.rsbutton_shape_selected);
    withPhotosButton.setTextColor(getResources().getColor(R.color.white));

    withoutPhotosButton.setEnabled(true);
    withoutPhotosButton.setBackgroundResource(R.drawable.rsbutton_shape_normal);
    withoutPhotosButton.setTextColor(getResources().getColor(R.color.textcolor_lightblue));
    updateWithPhotosData();
  }

  @Click(R.id.itemphotos_withoutphotos_btn)
  protected void withoutPhotosButtonClick() {
    withPhotos = false;

    withoutPhotosButton.setEnabled(false);
    withoutPhotosButton.setBackgroundResource(R.drawable.rsbutton_shape_selected);
    withoutPhotosButton.setTextColor(getResources().getColor(R.color.white));

    withPhotosButton.setEnabled(true);
    withPhotosButton.setBackgroundResource(R.drawable.rsbutton_shape_normal);
    withPhotosButton.setTextColor(getResources().getColor(R.color.textcolor_lightblue));
    updateWithPhotosData();
  }

  @AfterViews
  void initAfterViews() {
    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    itemList.setLayoutManager(llm);

    setTopBarTitle(getFragmentTitle());

    loadItemsUi();
  }

  @SuppressWarnings({"unchecked"})
  protected void updateWithPhotosData() {
    updateCategories(getCurrentMenuCategory());
  }

  @UiThread
  protected void loadItemsUi() {
    closeWaitingDialog();
    showWaitingDialog(null, getString(R.string.message_loading_items), "loadingItemsDialog");
    loadItems();
  }

  @Background
  void loadItems() {
    try {
      List<Item> items = RockServices.getItemService().fetchVenueItens(RockServices.getDataService().getDeviceVenueId());
      final Map<String, CategoryData> categories = new HashMap<>();
      final List<CategoryData> categoryList = new LinkedList<>();
      menuCategoriesWithPhotos.clear();
      menuCategoriesWithoutPhotos.clear();

      //TODO: We should re-do that, because this is just a workarround since we don't have really defined which categories will appear here.
      for (final Item item : items) {
        final String category = item.getSubcategory() != null ? item.getSubcategory() : item.getCategory().toString();
        if (!categories.containsKey(category)) {
          categories.put(category, new CategoryData(category));
          categoryList.add(categories.get(category));
        }
        categories.get(category).addItem(item);
      }

      for (final CategoryData category : categoryList) {
        ArrayList<Item> itemsWithPhotos = new ArrayList<>();
        ArrayList<Item> itemsWithoutPhotos = new ArrayList<>();

        for(Item item : category.getItems()) {
          if (item.getLogo() != null) {
            itemsWithPhotos.add(item);
          } else {
            itemsWithoutPhotos.add(item);
          }
        }

        menuCategoriesWithPhotos.add(new MenuCategory(null, category.getName(), "", 0, null, itemsWithPhotos));
        menuCategoriesWithoutPhotos.add(new MenuCategory(null, category.getName(), "", 0, null, itemsWithoutPhotos));
      }
      updateCategories(getCurrentMenuCategory());
    } catch (RestClientException e) {
      //TODO: Better Error Handler
      onError(new Error(ErrorCode.InternalError, e.getLocalizedMessage()), "errorLoadingMenuDialog");
    }
  }

  private List<MenuCategory> getCurrentMenuCategory() {
    return withPhotos ? menuCategoriesWithPhotos : menuCategoriesWithoutPhotos;
  }

  @UiThread
  void updateCategories(final List<MenuCategory> categories) {
    closeWaitingDialog();
    if (categoryBar.getAdapter() == null) {
      categoryAdapter = new CategoryAdapter(getActivity(), categories);
      categoryAdapter.setOnCategoryItemClick((pos, data) -> {
        selectedCategory = pos;
        updateItems(categoryAdapter.getItemAdapter(selectedCategory));
        itemList.animate().alpha(1.f).setDuration(200).setListener(null);
      });
      categoryBar.setAdapter(categoryAdapter);
    } else {
      categoryAdapter.setCategoryData(categories, withPhotos);
    }

    if (categoryAdapter.getCount() > selectedCategory) {
      categoryAdapter.clickItem(selectedCategory);
    } else if (categoryAdapter.getCount() > 0) {
      categoryAdapter.clickItem(0);
    } else {
      updateItems(new FoodItemsAdapter(getActivity()));
    }
  }


  @OnActivityResult(CAMERA_RESULT_CODE)
  protected void onResult(int resultCode, @OnActivityResult.Extra Item item) {
    if (resultCode == CameraActivity.RESULT_OK) {
      showWaitingDialog(null, getString(R.string.message_updating_photo), "updatePhotoDialog");
      updatePhoto(item);
    }
  }

  @Background
  protected void updatePhoto(final Item item) {
    try {
      CameraStorage.resizeStoredImage(640, 640);
      final Image image = new Image(CameraStorage.getImageB64());
      RockServices.getItemService().updateItemImage(RockServices.getDataService().getDeviceVenueId(), item.getId(), image);
      loadItemsUi();
    } catch (RestClientException e) {
      onError(new Error(ErrorCode.InternalError, e.getLocalizedMessage()), "updatePhotoError");
    }
  }

  @UiThread
  void updateItems(final FoodItemsAdapter foodAdapter) {
    itemList.animate().alpha(0.f).setDuration(200).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        itemList.setAdapter(foodAdapter);
        foodAdapter.setFoodItemClickListener(item -> CameraActivity_.intent(SelectItemFragment.this).extra("item", item).startForResult(CAMERA_RESULT_CODE));
        itemList.animate().alpha(1.f).setDuration(200).setListener(null);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    CameraStorage.clear();
  }

  @UiThread
  @Override
  protected void onError(final Error error, final String label) {
    super.onError(error, label);
    getActivity().onBackPressed();
  }

  @Override
  public int getFragmentLayoutId() {
    return R.layout.item_photos_fragment;
  }

  @Override
  public String getFragmentTitle() {
    return "Select Item";
  }

}
