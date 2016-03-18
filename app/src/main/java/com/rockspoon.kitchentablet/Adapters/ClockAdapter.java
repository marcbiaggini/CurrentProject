package com.rockspoon.kitchentablet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rockspoon.kitchentablet.Animations.AnimatorSetup;
import com.rockspoon.models.user.clockin.UserClockEvent;
import com.rockspoon.rockpos.R;

import java.text.SimpleDateFormat;
import java.util.List;

import at.markushi.ui.CircleButton;

/**
 * Created by juancamilovilladuarte on 2/3/16.
 */
public class ClockAdapter extends BaseAdapter {
  private List<UserClockEvent> data;
  private Context context;
  private String codeJob;
  private Activity clockActivity;
  private AnimatorSetup animatorSetup = new AnimatorSetup();

  public ClockAdapter(Context context, List<UserClockEvent> data, Activity clockActivity) {
    this.data = data;
    this.context = context;
    this.clockActivity = clockActivity;
  }

  public void setData(List<UserClockEvent> data) {
    this.data = data;
  }

  @Override
  public boolean isEnabled(int position) {
    return true;
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public Object getItem(int position) {
    return data.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder viewHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.kitchen_clock_item, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    final UserClockEvent userLogin = (UserClockEvent) getItem(position);
    viewHolder.userstatus.setEnabled(false);
    switch (userLogin.getEventType()) {
      case clockin:
        viewHolder.userstatus.setColor(clockActivity.getResources().getColor(R.color.timeline_running));
        viewHolder.clockStatus.setText("You Clocked-In At");
        break;
      case pause:
        viewHolder.userstatus.setColor(clockActivity.getResources().getColor(R.color.timeline_paused2));
        viewHolder.clockStatus.setText("You Took a Break At");
        break;
      case resume:
        viewHolder.userstatus.setColor(clockActivity.getResources().getColor(R.color.timeline_running));
        viewHolder.clockStatus.setText("You Go Back From Break At");
        break;
      case clockout:
        viewHolder.userstatus.setColor(clockActivity.getResources().getColor(R.color.timeline_paused));
        viewHolder.clockStatus.setText("You Clocked Out At");
        break;
      default:
        viewHolder.userstatus.setColor(clockActivity.getResources().getColor(R.color.timeline_running));
        viewHolder.clockStatus.setText("You Clocked-In At");
        break;
    }
    viewHolder.clockHour.setText(new SimpleDateFormat("HH:mm").format(userLogin.getWhen().getTime()));
    viewHolder.clockHour.startAnimation(AnimationUtils.loadAnimation(clockActivity.getApplicationContext(), R.anim.fade_in_dialog));
    viewHolder.clockStatus.startAnimation(AnimationUtils.loadAnimation(clockActivity.getApplicationContext(), R.anim.fade_in_dialog));
    viewHolder.userstatus.startAnimation(AnimationUtils.loadAnimation(clockActivity.getApplicationContext(), R.anim.fade_in_dialog));
    return convertView;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView clockStatus, clockHour;
    public CircleButton userstatus;

    public ViewHolder(View v) {
      super(v);
      userstatus = (CircleButton) v.findViewById(R.id.userStatus);
      clockHour = (TextView) v.findViewById(R.id.clockHour);
      clockStatus = (TextView) v.findViewById(R.id.clockStatusUser);
    }
  }
}
