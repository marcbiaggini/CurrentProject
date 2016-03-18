package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.models.user.LoginUserData;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 06/07/15.
 */
public class LoginUserAdapter extends BaseAdapter {

  private final List<LoginUserData> users = new ArrayList<>();
  private final Context ctx;

  public LoginUserAdapter(final Context ctx) {
    this.ctx = ctx;
  }

  public LoginUserAdapter(final Context ctx, final List<LoginUserData> users) {
    this.users.addAll(users);
    this.ctx = ctx;
  }

  public void setData(final List<LoginUserData> users) {
    this.users.clear();
    if (users != null)
      this.users.addAll(users);
    this.notifyDataSetChanged();
  }

  public int getCount() {
    return users.size();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.login_user, null) : convertView;
    final ViewHolder vH = (convertView == null) ? new ViewHolder(v) : (ViewHolder) v.getTag();
    final LoginUserData user = users.get(position);

    vH.userName.setText(user.getFullName());
    vH.userRole.setText(user.getEmployeeData().getRole().getName());
    vH.userImage.setImageDrawable(user.getNoAvatar().getImage(ctx, vH.userImage));

    return v;
  }

  public LoginUserData getItem(int position) {
    return users.get(position);
  }

  static class ViewHolder {
    RoundImageView userImage;
    TextView userName;
    TextView userRole;
    int position;

    public ViewHolder(View v) {
      userImage = (RoundImageView) v.findViewById(R.id.login_user_userImage);
      userName = (TextView) v.findViewById(R.id.login_user_userName);
      userRole = (TextView) v.findViewById(R.id.login_user_userRole);
      v.setTag(this);
    }
  }
}
