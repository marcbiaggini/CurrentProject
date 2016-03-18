package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rockspoon.rockandui.Objects.RadioModifierData;
import com.rockspoon.rockandui.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 03/08/15.
 */
public class ModifierRadioAdapter extends AbstractModifierItemAdapter {

  private final List<RadioModifierData> radioModifiers = new ArrayList<>();
  private final List<RadioButton> radioButtonCache = new ArrayList<>();

  private final Context ctx;

  public ModifierRadioAdapter(Context ctx) {
    this.ctx = ctx;
  }

  public ModifierRadioAdapter(Context ctx, List<RadioModifierData> data) {
    this.ctx = ctx;
    this.radioModifiers.addAll(data);
  }

  public List<RadioModifierData> getData() {
    return radioModifiers;
  }

  @Override
  public int getCount() {
    return radioModifiers.size();
  }

  @Override
  public Object getItem(int i) {
    return radioModifiers.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup root) {
    ViewHolder holder;
    View view = convertView;

    if (convertView == null) {
      view = LayoutInflater.from(ctx).inflate(R.layout.modifier_item_radio, root, false);
      holder = new ViewHolder(view);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    final RadioModifierData data = radioModifiers.get(position);
    radioButtonCache.add(position, holder.name);

    holder.name.setText(data.getName());
    holder.name.setChecked(data.getSelected());
    holder.name.setOnClickListener((v) -> {
      for (final RadioButton btn : radioButtonCache) {
        btn.setChecked(false);
      }

      for (final RadioModifierData rdata : radioModifiers) {
        rdata.setSelected(false);
      }

      holder.name.setChecked(true);
      data.setSelected(true);
      if (onModifierItemSelected != null) {
        onModifierItemSelected.onSelected(position);
      }
    });

    holder.price.setVisibility(data.getPrice().compareTo(BigDecimal.ZERO) == 0 ? View.INVISIBLE : View.VISIBLE);
    holder.price.setText(ctx.getResources().getString(R.string.format_price, data.getPrice()));

    return view;
  }

  static class ViewHolder {
    RadioButton name;
    TextView price;

    public ViewHolder(View v) {
      name = (RadioButton) v.findViewById(R.id.modifier_item_radio_text);
      price = (TextView) v.findViewById(R.id.modifier_item_radio_price);
      v.setTag(this);
    }
  }
}
