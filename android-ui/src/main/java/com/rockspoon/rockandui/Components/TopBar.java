package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.rockandui.R;

/**
 * Created by greenfrvr
 */
public class TopBar extends FrameLayout implements View.OnClickListener {

  private BackButtonListener backButtonListener;
  private ImageButtonListener imageButtonListener;
  private TitleClickListener titleClickListener;
  private ExtraButtonListener extraButtonListener;

  private TextView titleView;
  private ImageView imageButton;
  private ImageView backButton;
  private Button extraButton;

  public TopBar(Context context) {
    this(context, null, 0);
  }

  public TopBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    inflate(context, R.layout.pos_base_top_bar, this);

    setBackgroundResource(R.drawable.actionbar_background);

    initViews();
    extractAttributes(attrs);
  }

  private void initViews() {
    titleView = (TextView) findViewById(R.id.pos_base_title);
    imageButton = (ImageView) findViewById(R.id.pos_base_image_button);
    backButton = (ImageView) findViewById(R.id.pos_base_back_button);
    extraButton = (Button) findViewById(R.id.pos_base_extra_button);

    imageButton.setOnClickListener(this);
    titleView.setOnClickListener(this);
    backButton.setOnClickListener(this);
    extraButton.setOnClickListener(this);
  }

  private void extractAttributes(AttributeSet attrs) {
    if (attrs != null) {
      final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TopBarView, 0, 0);
      try {
        enableTitleView(a.getBoolean(R.styleable.TopBarView_topbarTitleEnabled, true));
        enableBackButton(a.getBoolean(R.styleable.TopBarView_topbarBackEnabled, true));
        enableImageButton(a.getBoolean(R.styleable.TopBarView_topbarActionEnabled, false));
        enableExtraButton(a.getBoolean(R.styleable.TopBarView_topbarDoneEnabled, false));
      } finally {
        a.recycle();
      }
    }
  }

  public void enableBackButton(boolean enable) {
    backButton.setVisibility(enable ? VISIBLE : GONE);
  }

  public void setTitle(CharSequence sequence) {
    titleView.setText(sequence);
  }

  public void setTitle(@StringRes int stringRes) {
    titleView.setText(stringRes);
  }

  public String getTitle() {
    return titleView.getText().toString();
  }

  public void enableTitleView(boolean enable) {
    titleView.setVisibility(enable ? VISIBLE : GONE);
  }

  public TextView getTitleView() {
    return titleView;
  }

  public void enableImageButton(boolean enable) {
    imageButton.setVisibility(enable ? VISIBLE : GONE);
  }

  public void setImageButtonRes(@DrawableRes int imageRes) {
    imageButton.setImageResource(imageRes);
  }

  public void setExtraButtonText(@StringRes int textRes) {
    extraButton.setText(textRes);
  }

  public void enableExtraButton(boolean enable) {
    extraButton.setVisibility(enable ? VISIBLE : GONE);
  }

  public void extraButtonClickable(boolean enabled) {
    extraButton.setEnabled(enabled);
  }

  public void setTitleClickListener(TitleClickListener titleClickListener) {
    this.titleClickListener = titleClickListener;
  }

  public void setImageButtonListener(ImageButtonListener imageButtonListener) {
    this.imageButtonListener = imageButtonListener;
  }

  public void setBackButtonListener(BackButtonListener backButtonListener) {
    this.backButtonListener = backButtonListener;
  }

  public void setExtraButtonListener(ExtraButtonListener extraButtonListener) {
    this.extraButtonListener = extraButtonListener;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int defaultHeight = getResources().getDimensionPixelOffset(R.dimen.topbar_height);

    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    int height = defaultHeight;

    if (heightMode == MeasureSpec.EXACTLY) {
      height = heightSize;
    } else if (heightMode == MeasureSpec.AT_MOST) {
      height = Math.min(defaultHeight, heightSize);
    }

    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    measureChildren(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    if (id == R.id.pos_base_back_button && backButtonListener != null) {
      backButtonListener.onBackClicked();
    }

    if (id == R.id.pos_base_image_button && imageButtonListener != null) {
      imageButtonListener.onImageButtonClicked();
    }

    if (id == R.id.pos_base_title && titleClickListener != null) {
      titleClickListener.onTitleClicked();
    }

    if (id == R.id.pos_base_extra_button && extraButtonListener != null) {
      extraButtonListener.onExtraButtonClicked();
    }
  }

  public interface BackButtonListener {
    void onBackClicked();
  }

  public interface ImageButtonListener {
    void onImageButtonClicked();
  }

  public interface TitleClickListener {
    void onTitleClicked();
  }

  public interface ExtraButtonListener {
    void onExtraButtonClicked();
  }
}
