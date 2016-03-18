package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rockspoon.rockandui.Adapters.TableMemberDataAdapter;
import com.rockspoon.rockandui.Interfaces.OnPayBillClick;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by lucas on 27/06/15.
 */
public class TableSeated extends Table {

  private final RSGridView members;
  private final TextView tablePayBill;
  private OnPayBillClick onPayBillClick;

  public TableSeated(final Context ctx) {
    this(ctx, null);
  }

  public TableSeated(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, 0);
  }

  public TableSeated(final Context ctx, final AttributeSet attrs, final int defStyle) {
    super(ctx, attrs, R.layout.table_seated);
    members = (RSGridView) findViewById(R.id.table_members);
    tablePayBill = (TextView) findViewById(R.id.table_pay_bill_btn);
    tablePayBill.setOnClickListener((v) -> {
      if (onPayBillClick != null)
        onPayBillClick.onPayBillClick(TableSeated.this);
    });
  }

  public void setOnDragListener(OnDragListener listener) {
    members.setOnDragListener(listener);
  }

  public boolean isItemSelected(int position) {
    return ((MemberData) members.getAdapter().getItem(position)).isSelected();
  }

  public void updateOccupiedSeats() {
    TableMemberDataAdapter adapter = (TableMemberDataAdapter) members.getAdapter();

    final List<MemberData> membersD = adapter.getMembers();
    occupiedNumSeats = 0;
    for (final MemberData member : membersD) {
      if (!member.isFree())
        occupiedNumSeats++;
    }
  }

  public void setAdapter(TableMemberDataAdapter adapter) {
    super.setAdapter(adapter);
    adapter.setSelectable(true);
    adapter.setOnMemberClick(onMemberClick);
    members.setAdapter(adapter);
    updateOccupiedSeats();
  }

  @Override
  public TableType getType() {
    return TableType.SEATED;
  }

}
