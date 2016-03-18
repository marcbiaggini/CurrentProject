package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockspoon.rockandui.Interfaces.OnModifierQuantityChanged;
import com.rockspoon.rockandui.Objects.QuantityModifierData;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 03/08/15.
 */
public class ModifierQuantityAdapter extends AbstractModifierItemAdapter {

  private final List<QuantityModifierData> qtyModifiers = new ArrayList<>();
  private final Context ctx;

  private OnModifierQuantityChanged onModifierQuantityChanged;

  public ModifierQuantityAdapter(Context ctx) {
    this.ctx = ctx;
  }

  public ModifierQuantityAdapter(Context ctx, List<QuantityModifierData> data) {
    this.ctx = ctx;
    this.qtyModifiers.addAll(data);
  }

  public void setOnModifierQuantityChanged(OnModifierQuantityChanged listener) {
    this.onModifierQuantityChanged = listener;
  }

  public List<QuantityModifierData> getData() {
    return qtyModifiers;
  }

  @Override
  public int getCount() {
    return qtyModifiers.size();
  }

  @Override
  public Object getItem(int i) {
    return qtyModifiers.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup root) {
    View view = convertView;
    ViewHolder holder;

    if (view == null) {
      view = LayoutInflater.from(ctx).inflate(R.layout.modifier_item_quantity, root, false);
      holder = new ViewHolder(view);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    final QuantityModifierData data = qtyModifiers.get(position);

    holder.plus.setOnClickListener(v -> {
      holder.quantity.setText(String.valueOf(data.getQuantity() + 1));
      data.setQuantity(data.getQuantity() + 1);
      if (onModifierQuantityChanged != null) {
        onModifierQuantityChanged.onModifierQuantityChanged(data.getQuantity());
      }
    });

    holder.minus.setOnClickListener(v -> {
      if (data.getQuantity() > 0) {
        holder.quantity.setText(String.valueOf(data.getQuantity() - 1));
        data.setQuantity(data.getQuantity() - 1);
        if (onModifierQuantityChanged != null) {
          onModifierQuantityChanged.onModifierQuantityChanged(data.getQuantity());
        }
      }
    });

    holder.name.setText(data.getName());
    holder.quantity.setText(String.valueOf(data.getQuantity()));

    return view;
  }

  static class ViewHolder {
    TextView name;
    TextView quantity;
    ImageButton plus;
    ImageButton minus;

    public ViewHolder(View v) {
      name = (TextView) v.findViewById(R.id.modifier_item_quantity_name);
      quantity = (TextView) v.findViewById(R.id.modifier_item_quantity_quantity);
      plus = (ImageButton) v.findViewById(R.id.modifier_item_quantity_plus);
      minus = (ImageButton) v.findViewById(R.id.modifier_item_quantity_minus);
      v.setTag(this);
    }
  }
}
