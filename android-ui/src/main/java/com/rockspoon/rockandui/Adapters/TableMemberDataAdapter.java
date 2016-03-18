package com.rockspoon.rockandui.Adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rockspoon.rockandui.Components.RSGridView;
import com.rockspoon.rockandui.Components.RoundImageView;
import com.rockspoon.rockandui.Interfaces.OnMemberClick;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lucas on 27/06/15.
 */
@Deprecated
public class TableMemberDataAdapter extends LinearGridViewAdapter implements RSGridView.OnChangeListener {

  private final List<MemberData> members = new ArrayList<>();
  private boolean selectable = true;
  private OnMemberClick onMemberClick;
  private int fistItemPosition = -1;
  private int secondItemPosition = -1;

  public TableMemberDataAdapter(final Context ctx) {
    super(ctx, 3);
  }

  public TableMemberDataAdapter(final Context ctx, final List<MemberData> members) {
    this(ctx);
    addAllStableId(members);
    this.members.addAll(members);
  }

  public void setData(ArrayList<MemberData> data) {
    clear();
    clearStableIdMap();
    addAllStableId(data);
    this.members.clear();
    this.members.addAll(data);
  }

  public List<MemberData> getMembers() {
    return members;
  }

  public void addMember(MemberData member) {
    this.addStableId(member);
    this.members.add(member);
  }

  public void setNumSeats(int seats) {
    if (seats > getCount()) {
      while (seats > getCount()) {
        addMember(new MemberData());
      }
    } else if (seats < getCount()) {
      int diff = getCount() - seats;
      for (int i = 0; i < diff; i++) {
        final MemberData member = getMembers().get(getCount() - 1);
        if (member.isFree())
          getMembers().remove(member);
        else
          break;
      }
    }
    clearStableIdMap();
    addAllStableId(members);
    notifyDataSetChanged();
  }

  @Override
  public boolean itemIsSelected(int position) {
    return getItem(position).isSelected();
  }

  public int getCount() {
    return members.size();
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    final LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final View v = (convertView == null) ? inflater.inflate(R.layout.table_member, null) : convertView;
    final ViewHolder vH = (convertView == null) ? new ViewHolder(v) : (ViewHolder) v.getTag();
    final MemberData member = members.get(position);

    String name = member.getName();
    //TODO: Try to use text metrics
    if (name.length() > 10) {
      name = name.substring(0, 7);
      name += "...";
    }

    vH.memberName.setText(name);

    if (selectable) {
      vH.memberCheckBox.setVisibility(View.VISIBLE);
      vH.memberSeatText.setVisibility(View.GONE);
      vH.memberCheckBox.setText(String.format(ctx.getResources().getString(R.string.format_seat_n), (position + 1)));
      vH.memberCheckBox.setOnClickListener((v2) -> member.setSelected(vH.memberCheckBox.isChecked()));
    } else {
      vH.memberCheckBox.setVisibility(View.GONE);
      vH.memberSeatText.setVisibility(View.VISIBLE);
      vH.memberSeatText.setText(String.format(ctx.getResources().getString(R.string.format_seat_n), (position + 1)));
    }
    vH.memberImage.setOnClickListener((view) -> {
      if (onMemberClick != null)
        onMemberClick.onMemberClick(position);
    });

    vH.memberImage.setOnLongClickListener(view -> {
      ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
      String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

      ClipData dragData = new ClipData(member.getName(), mimeTypes, item);
      View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);

      view.startDrag(dragData, myShadow, null, 0);

      fistItemPosition = position;
      return true;
    });
    vH.memberImage.setImageDrawable(member.getImage(ctx, vH.memberImage));

    v.setOnDragListener(new MemberDragListener(position));

    return v;
  }

  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
    notifyDataSetChanged();
    if (!selectable)
      for (MemberData data : members)
        data.setSelected(false);
  }

  public void setOnMemberClick(OnMemberClick listener) {
    this.onMemberClick = listener;
  }

  @Override
  public MemberData getItem(int position) {
    return members.get(position);
  }

  @Override
  public void reorderItems(int orgPos, int newPos) {
    if (newPos < getCount()) {
      MemberData obj = members.remove(orgPos);
      members.add(newPos, obj);
      notifyDataSetChanged();
    }
  }

  @Override
  public boolean canReorder(int pos) {
    return true;
  }

  @Override
  public void onChange(int from, int to) {
    Collections.swap(members, from, to);
    notifyDataSetChanged();
  }

  private void swap(int first, int second) {
    if (first != -1 && second != -1) {
      Collections.swap(members, first, second);
      notifyDataSetChanged();
    }
  }

  static class ViewHolder {
    RoundImageView memberImage;
    TextView memberName;
    TextView memberSeatText;
    CheckBox memberCheckBox;

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
