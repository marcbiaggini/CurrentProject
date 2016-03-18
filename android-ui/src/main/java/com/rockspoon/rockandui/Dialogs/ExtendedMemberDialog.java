package com.rockspoon.rockandui.Dialogs;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSDialog;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

/**
 * Created by lucas on 31/07/15.
 */
public class ExtendedMemberDialog extends RSDialog {

  public static final String TAG = ExtendedMemberDialog.class.getSimpleName();

  private static final String ARGS_SEAT = "dialog:seat_number";
  private static final String ARGS_MEMBER = "dialog:member";

  private MemberData member;
  private int seatNum;

  public static ExtendedMemberDialog newInstance(int seatNum, MemberData member) {
    Bundle args = new Bundle();
    args.putInt(ARGS_SEAT, seatNum);
    args.putParcelable(ARGS_MEMBER, member);

    ExtendedMemberDialog fragment = new ExtendedMemberDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(STYLE_NO_FRAME, R.style.rsBottomTopDialog);
    setGravity(Gravity.BOTTOM);
    setDialogMarginBottom(0);

    seatNum = getArguments().getInt(ARGS_SEAT);
    member = getArguments().getParcelable(ARGS_MEMBER);
  }

  @Override
  public View onRSCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.extended_member, container, false);

    TextView title = (TextView) view.findViewById(R.id.extended_member_title);
    TextView memberName = (TextView) view.findViewById(R.id.extended_member_name);
    TextView memberSince = (TextView) view.findViewById(R.id.extended_member_since);
    TextView lifetimeRev = (TextView) view.findViewById(R.id.extended_member_lifetimerev);
    TextView averageTicket = (TextView) view.findViewById(R.id.extended_member_average_ticket);
    TextView averageTime = (TextView) view.findViewById(R.id.extended_member_time_spent);
    RoundImageView memberImage = (RoundImageView) view.findViewById(R.id.extended_member_image);

    Button closeButton = (Button) view.findViewById(R.id.extended_member_closebtn);
    closeButton.setOnClickListener((v) -> dismiss());

    title.setText(getString(R.string.format_seat_n, seatNum + 1));
    memberName.setText(member.getName());
    memberSince.setText(member.getMemberSince());
    lifetimeRev.setText(getString(R.string.format_price_no_decimal, member.getLifetimeRevenue()));
    averageTicket.setText(getString(R.string.format_price_no_decimal, member.getAverageTicket()));
    averageTime.setText(member.getAverageTimeString());
    memberImage.setImageDrawable(member.getImage(getActivity(), memberImage));

    return view;
  }

}