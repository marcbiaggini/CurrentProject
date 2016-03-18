package com.rockspoon.rockandui.Components;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockspoon.rockandui.Adapters.TableMemberDataAdapter;
import com.rockspoon.rockandui.Interfaces.OnMemberClick;
import com.rockspoon.rockandui.Interfaces.OnNumSeatsClick;
import com.rockspoon.rockandui.Managers.LayoutModelManager;
import com.rockspoon.rockandui.R;

import org.apache.commons.lang.ArrayUtils;

/**
 * Created by lucas on 15/07/15.
 */
public abstract class Table extends RelativeLayout {

  protected final TableNameBarView tableNameBarView;
  protected final TextView numSeatsView;
  protected OnNumSeatsClick onNumSeatsClick;
  protected OnMemberClick onMemberClick;
  protected int numSeats;
  protected int occupiedNumSeats;
  protected TableMemberDataAdapter memberAdapter;
  protected Long[] spotId;
  protected Long sectionId;
  protected LinearLayout gridContainer;

  public Table(final Context ctx) {
    this(ctx, R.layout.table_seated);
  }

  public Table(final Context ctx, final int resource) {
    this(ctx, null, resource);
  }

  public Table(final Context ctx, final AttributeSet attrs) {
    this(ctx, attrs, R.layout.table_seated);
  }

  public Table(final Context ctx, final AttributeSet attrs, int resource) {
    super(ctx, attrs);
    inflate(ctx, resource, this);
    gridContainer = (LinearLayout) findViewById(R.id.table_grid_container);
    tableNameBarView = (TableNameBarView) findViewById(R.id.table_name_bar);
    numSeatsView = (TextView) findViewById(R.id.table_num_seats_btn);
    tableNameBarView.setOnCheckedChangeListener((buttonView, isChecked) -> {   // Table checked
      if (spotId.length > 0) {
        Intent intent = new Intent(LayoutModelManager.RECEIVER_TABLE_STATE);
        intent.putExtra(LayoutModelManager.EXTRA_TABLE_ID, ArrayUtils.toPrimitive(spotId));
        intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, sectionId);
        intent.putExtra(LayoutModelManager.EXTRA_TABLE_STATE, isChecked);
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intent);
      }
    });

    if (attrs != null) {
      final TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.TableSeated, 0, 0);
      try {
        numSeats = a.getInt(R.styleable.TableSeated_numSeats, 0);
        if (numSeatsView != null) {
          numSeatsView.setText(String.format(getResources().getString(R.string.format_num_seats_n), ((numSeats != 0) ? "(" + numSeats + ")" : "")));
        }
      } finally {
        a.recycle();
      }
    }

    if (numSeatsView != null) {
      numSeatsView.setOnClickListener((v) -> {
        if (onNumSeatsClick != null)
          onNumSeatsClick.onNumSeatsClick(Table.this);
      });
    }
    memberAdapter = new TableMemberDataAdapter(ctx);
  }

  public void setOnNumSeatsClick(OnNumSeatsClick listener) {
    this.onNumSeatsClick = listener;
  }

  public void setOnMemberClick(OnMemberClick listener) {
    this.onMemberClick = listener;
    if (memberAdapter != null)
      memberAdapter.setOnMemberClick(listener);
  }

  public int getNumSeats() {
    return numSeats;
  }

  public abstract void updateOccupiedSeats();

  public int getNumOccupiedSeats() {
    updateOccupiedSeats();
    return occupiedNumSeats;
  }

  public void setTableNameClickListener(OnClickListener listener) {
    tableNameBarView.setOnClickListener(listener);
  }

  public TableNameBarView getTableNameBarView() {
    return tableNameBarView;
  }

  public String getTableName() {
    return "";//tableName.getText().toString();
  }

  public void setTableName(String[] name) {
    tableNameBarView.setTableName(name);
  }

  public boolean isSelected() {
    return tableNameBarView.isSelected();
  }

  public void setTableSelectable(boolean selectable) {
  }

  public void setAdapter(TableMemberDataAdapter adapter) {
    numSeats = adapter.getCount();
    numSeatsView.setText(String.format(getResources().getString(R.string.format_num_seats_n), ((numSeats != 0) ? "(" + numSeats + ")" : "")));
    memberAdapter = adapter;
  }

  public void setSpotId(Long[] spotId) {
    this.spotId = spotId;
  }

  public void setSectionId(Long sectionId) {
    this.sectionId = sectionId;
  }

  public abstract TableType getType();

  public void dragEntered(boolean isEntered) {
  }

  public enum TableType {
    EMPTY,
    SEATED,
    PAID
  }

}
