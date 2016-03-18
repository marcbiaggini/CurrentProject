package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockspoon.models.venue.network.Network;
import com.rockspoon.rockandui.Tools;

import java.util.ArrayList;
import java.util.List;
import com.rockspoon.rockandui.R;

/**
 * Created by Lucas Teske on 31/08/15.
 */
public class WifiNetworkListAdapter extends BaseAdapter {

  private List<Network> networks = new ArrayList<>();
  private Context ctx;

  public WifiNetworkListAdapter(final Context ctx, final List<Network> networks) {
    this.networks.addAll(networks);
    this.ctx = ctx;
  }

  @Override
  public int getCount() {
    return networks.size();
  }

  @Override
  public Network getItem(int i) {
    return networks.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(final int position, View convertView, final ViewGroup rootView) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.select_wifi_item, null) : convertView;
    final ViewHolder vH = (convertView == null) ? new ViewHolder(v) : (ViewHolder) v.getTag();
    final Network wifi = networks.get(position);

    vH.wifiName.setText(wifi.getSsid());
    vH.wifiLock.setVisibility("NONE".equals(wifi.getSettings().get("keytype")) ? View.INVISIBLE : View.VISIBLE);
    vH.wifiSignal.setImageResource(Tools.getResourceByName(ctx, "drawable", "ic_signal_wifi_" + wifi.getSettings().get("signal4") + "_bar_black_24dp"));

    return v;
  }

  static class ViewHolder {
    TextView wifiName;
    ImageView wifiLock;
    ImageView wifiSignal;

    public ViewHolder(View v) {
      wifiName = (TextView) v.findViewById(R.id.select_wifi_item_name);
      wifiLock = (ImageView) v.findViewById(R.id.select_wifi_lock);
      wifiSignal = (ImageView) v.findViewById(R.id.select_wifi_signal);
      v.setTag(this);
    }
  }
}
