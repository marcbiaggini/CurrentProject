package com.rockspoon.rockandui.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Interfaces.OnTableItemClick;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eugen K. on 3/2/16.
 */
public class DinerSessionAdapter extends BaseAdapter {

  private List<DinerSession> dinerSessions = new ArrayList<>();
  private int fistItemPosition = -1;
  private int secondItemPosition = -1;
  private OnTableItemClick onTableItemClick;

  public DinerSessionAdapter(List<DinerSession> dinerSessions) {
    this.dinerSessions = dinerSessions != null ? dinerSessions : new ArrayList<>();
    Collections.sort(this.dinerSessions, (lhs, rhs) -> Integer.compare(lhs.getSeatNumber(), rhs.getSeatNumber()));
  }

  @Override
  public int getCount() {
    return dinerSessions.size();
  }

  @Override
  public Object getItem(int position) {
    return dinerSessions.get(position);
  }

  @Override
  public long getItemId(int position) {
    return dinerSessions.get(position).getId();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final View sessionView = (convertView == null) ? LayoutInflater.from(parent.getContext()).inflate(R.layout.table_member, null) : convertView;
    final ViewHolder viewHolder = (convertView == null) ? new ViewHolder(sessionView) : (ViewHolder) sessionView.getTag();
    DinerSession session = dinerSessions.get(position);
    viewHolder.memberName.setText(String.format("#%1$s", session.getId()));
    //TODO implement case for not null dinerSessions.get(position).getDiner()
    viewHolder.memberImage.setOnLongClickListener(view -> {
      ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
      String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
      ClipData dragData = new ClipData(Long.toString(session.getId()), mimeTypes, item);
      View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
      view.startDrag(dragData, myShadow, null, 0);
      fistItemPosition = position;
      return true;
    });
    viewHolder.memberImage.setOnClickListener(v -> {
      if (onTableItemClick != null) {
        onTableItemClick.onItemClick(session, null);
      }
    });
    String seatText = parent.getContext().getString(R.string.format_seat_n, session.getSeatNumber());
    viewHolder.memberSeatText.setText(seatText);
    sessionView.setOnDragListener(new MemberDragListener(position));
    return sessionView;
  }

  private void swap(int first, int second) {
    if (first != -1 && second != -1) {
      Collections.swap(dinerSessions, first, second);
      notifyDataSetChanged();
    }
  }

  public void setOnMemberClick(OnTableItemClick onTableItemClick) {
    this.onTableItemClick = onTableItemClick;
  }

  static class ViewHolder {
    RoundImageView memberImage;
    TextView memberName;
    TextView memberSeatText;
//    CheckBox memberCheckBox;

    public ViewHolder(View v) {
      memberImage = (RoundImageView) v.findViewById(R.id.member_image);
      memberName = (TextView) v.findViewById(R.id.member_name);
      memberSeatText = (TextView) v.findViewById(R.id.member_seat_text);
//      memberCheckBox = (CheckBox) v.findViewById(R.id.member_checkbox);
      v.setTag(this);
    }
  }

  private class MemberDragListener implements View.OnDragListener {

    private int memberId;

    public MemberDragListener(int memberId) {
      this.memberId = memberId;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
      if (fistItemPosition == -1) {
        return false;
      }
      final int action = event.getAction();
      switch (action) {
        case DragEvent.ACTION_DRAG_ENDED:
          swap(fistItemPosition, secondItemPosition);
          fistItemPosition = -1;
          secondItemPosition = -1;
          return true;
        case DragEvent.ACTION_DRAG_ENTERED:
          secondItemPosition = -1;
          if (memberId != fistItemPosition) {
            secondItemPosition = memberId;
          }
          return true;
        case DragEvent.ACTION_DRAG_EXITED:
          secondItemPosition = -1;
          return true;
      }
      return true;
    }
  }

}
