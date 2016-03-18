package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.rockspoon.rockandui.Adapters.TableMemberDataAdapter;
import com.rockspoon.rockandui.Interfaces.OnReOpenBillClick;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.R;

import java.util.List;

/**
 * Created by lucas on 15/07/15.
 */
public class TablePaid extends Table {

  private static final String TAG = "TablePaid";

  private final LinearGridView members;
  private final TextView tableReopenBill;
  private OnReOpenBillClick onReOpenBillClick;

  public TablePaid(final Context ctx) {
    this(ctx, null);
  }

  public TablePaid(final Context ctx, final AttributeSet attrs) {
    super(ctx, attrs, R.layout.table_paid);

    members = (LinearGridView) findViewById(R.id.table_members);
    tableReopenBill = (TextView) findViewById(R.id.table_reopen_bill_btn);
    members.setEditModeEnabled(false);
    members.setStopAfterDrag(true);
    members.setEnabled(false);
    members.setClickable(false);

    tableReopenBill.setOnClickListener((v) -> {
      if (onReOpenBillClick != null)
        onReOpenBillClick.onReOpenBillClick(TablePaid.this);
    });
  }

  public void setOnReOpenBillClick(OnReOpenBillClick listener) {
    this.onReOpenBillClick = listener;
  }

  public void setAdapter(TableMemberDataAdapter adapter) {
    super.setAdapter(adapter);
    adapter.setSelectable(false);
    members.setAdapter(adapter);
    updateOccupiedSeats();
  }

  public void updateOccupiedSeats() {
    TableMemberDataAdapter adapter = (TableMemberDataAdapter) members.getAdapter();

    final List<MemberData> membersD = adapter.getMembers();
    Log.d(TAG, "Updating Occupied Seats. Seats: " + membersD.size());
    occupiedNumSeats = 0;
    for (final MemberData member : membersD) {
      if (!member.isFree())
        occupiedNumSeats++;
    }
  }

  @Override
  public TableType getType() {
    return TableType.PAID;
  }
}
