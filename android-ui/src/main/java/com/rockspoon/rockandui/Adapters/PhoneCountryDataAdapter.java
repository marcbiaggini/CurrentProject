package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.rockandui.CountryTools;
import com.rockspoon.rockandui.Objects.PhoneCountryData;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 09/07/15.
 */
public class PhoneCountryDataAdapter extends BaseAdapter {

  private final int PHONECOUNTRY_TEXT_SIZE;

  private final Context ctx;

  public PhoneCountryDataAdapter(final Context ctx) {
    this.ctx = ctx;
    PHONECOUNTRY_TEXT_SIZE = ctx.getResources().getInteger(R.integer.phonecountry_text_maxsize);
  }

  public int getCount() {
    return CountryTools.getData().size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.flag_view_select_hold, null) : convertView;
    final FlagViewHolder vH = (convertView == null) ? new FlagViewHolder(v) : (FlagViewHolder) v.getTag();
    vH.flagImage.setImageResource(getItem(position).imageResource);
    return v;
  }

  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.flag_view_select, null) : convertView;
    final DropDownViewHolder vH = (convertView == null) ? new DropDownViewHolder(v) : (DropDownViewHolder) v.getTag();

    String name = getItem(position).countryName;

    if (name.length() > PHONECOUNTRY_TEXT_SIZE) {
      name = name.substring(0, PHONECOUNTRY_TEXT_SIZE - 3);
      name += "...";
    }

    vH.countryName.setText(name);
    vH.countryPrefix.setText("(+" + getItem(position).countryPrefix + ")");
    vH.flagImage.setImageResource(getItem(position).imageResource);

    return v;
  }

  public PhoneCountryData getItem(int position) {
    return CountryTools.getData().get(position);
  }

  static class FlagViewHolder {
    ImageView flagImage;

    public FlagViewHolder(View v) {
      flagImage = (ImageView) v.findViewById(R.id.flag_image_hold);
      v.setTag(this);
    }
  }

  static class DropDownViewHolder {
    ImageView flagImage;
    TextView countryName;
    TextView countryPrefix;

    public DropDownViewHolder(View v) {
      flagImage = (ImageView) v.findViewById(R.id.flag_image);
      countryName = (TextView) v.findViewById(R.id.flag_country_name);
      countryPrefix = (TextView) v.findViewById(R.id.flag_country_prefix);
      v.setTag(this);
    }
  }
}
