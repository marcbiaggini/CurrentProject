package com.rockspoon.rockandui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Interfaces.OnOrderingMemberClick;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Teske on 27/07/15.
 */
public class OrderingMemberAdapter extends BaseAdapter {

  private final int MAX_MEMBER_NAME_SIZE;
  private final List<MemberData> memberData = new ArrayList<>();
  private final Context ctx;

  public OrderingMemberAdapter(final Context ctx) {
    this.ctx = ctx;
    MAX_MEMBER_NAME_SIZE = ctx.getResources().getInteger(R.integer.member_text_maxsize);
  }

  public OrderingMemberAdapter(final Context ctx, final List<MemberData> memberData) {
    this(ctx);
    this.memberData.addAll(memberData);
  }

  public void setData(List<MemberData> members) {
    memberData.clear();
    memberData.addAll(members);
    notifyDataSetInvalidated();
  }

  @Override
  public int getCount() {
    return memberData.size();
  }

  @Override
  public MemberData getItem(int i) {
    return memberData.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(final int position, final View convertView, final ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final MemberData member = memberData.get(position);
    final View v = (convertView == null) ?  inflater.inflate(R.layout.order_member, null) : convertView;
    final ViewHolder vH = (convertView == null) ? new ViewHolder(v) : (ViewHolder) v.getTag();

    vH.memberImage.setImageDrawable(member.getImage(ctx, vH.memberImage));

    String name = member.getName();
    if (name.length() > MAX_MEMBER_NAME_SIZE) {
      name = name.substring(0, MAX_MEMBER_NAME_SIZE - 3);
      name += "...";
    }

    vH.memberName.setText(name);
    vH.memberNumber.setText(String.format(ctx.getResources().getString(R.string.format_number_n), position + 1));

    vH.memberSince.setText(member.getMemberSince());
    vH.memberAvgTime.setText(member.getAverageTimeString());
    vH.memberLifeTimeRev.setText(Integer.toString(member.getLifetimeRevenue()));
    vH.memberAvgTicket.setText(Integer.toString(member.getAverageTicket()));
    vH.memberRating.setRating(member.getRating());

    return v;
  }

  static class ViewHolder {
    RoundImageView memberImage;
    TextView memberName;
    TextView memberNumber;
    TextView memberSince;
    TextView memberAvgTime;
    TextView memberLifeTimeRev;
    TextView memberAvgTicket;
    RatingBar memberRating;
    ImageView memberButton;

    public ViewHolder(View v) {
      memberImage = (RoundImageView) v.findViewById(R.id.order_member_image);
      memberName = (TextView) v.findViewById(R.id.order_member_name);
      memberNumber = (TextView) v.findViewById(R.id.order_member_number);
      memberSince = (TextView) v.findViewById(R.id.order_member_since);
      memberAvgTime = (TextView) v.findViewById(R.id.order_member_avgtime);
      memberLifeTimeRev = (TextView) v.findViewById(R.id.order_member_lifetimerev);
      memberAvgTicket = (TextView) v.findViewById(R.id.order_member_avgticket);
      memberRating = (RatingBar) v.findViewById(R.id.order_member_rating);
      memberButton = (ImageView) v.findViewById(R.id.order_member_button);
      v.setTag(this);
    }
  }
}
