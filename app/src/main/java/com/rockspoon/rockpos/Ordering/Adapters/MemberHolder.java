package com.rockspoon.rockpos.Ordering.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockpos.R;

import java.util.Locale;

class MemberHolder extends OrderViewHolder<ListCartDetails> {

  private RoundImageView memberImage;
  private TextView memberName;
  private TextView memberNumber;
  private TextView memberSince;
  private TextView memberAvgTime;
  private TextView memberLifeTimeRev;
  private TextView memberAvgTicket;
  private RatingBar memberRating;
  private ImageView memberButton;

  public MemberHolder(View v) {
    super(v);
    memberImage = (RoundImageView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_image);
    memberName = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_name);
    memberNumber = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_number);
    memberSince = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_since);
    memberAvgTime = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_avgtime);
    memberLifeTimeRev = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_lifetimerev);
    memberAvgTicket = (TextView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_avgticket);
    memberRating = (RatingBar) v.findViewById(com.rockspoon.rockandui.R.id.order_member_rating);
    memberButton = (ImageView) v.findViewById(com.rockspoon.rockandui.R.id.order_member_button);
  }

  @Override
  public void bind(ListCartDetails data) {
    DinerSession session = data.getDinerSessions().get(0);

    memberImage.setImageResource(R.drawable.thumbnail);
    memberName.setText(String.format("Customer #%s", session.getId()));
    memberNumber.setText(String.format(Locale.getDefault(), "#%d", session.getSeatNumber()));
    //will be implemented later
//    memberSince.setText();
//    memberAvgTime.setText();
//    memberLifeTimeRev.setText();
//    memberAvgTicket.setText();
//    memberRating.setRating();
  }

}