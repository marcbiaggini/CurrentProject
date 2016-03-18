package com.rockspoon.rockandui.Adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.models.venue.layout.SpotStatus;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.rockandui.Components.RSGridView;
import com.rockspoon.rockandui.Components.TableNameBarView;
import com.rockspoon.rockandui.Dialogs.SeatsNumberDialog;
import com.rockspoon.rockandui.Interfaces.OnTableClick;
import com.rockspoon.rockandui.Interfaces.OnTableItemClick;
import com.rockspoon.rockandui.Managers.LayoutModelManager;
import com.rockspoon.rockandui.Objects.SectionModel;
import com.rockspoon.rockandui.R;
import com.rockspoon.services.DiningPartyService;
import com.rockspoon.services.DiningPartyService_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugen K. on 2/10/16.
 */
public class TablesListAdapter extends RecyclerView.Adapter<TablesListAdapter.ViewHolder> {

  DiningPartyService diningPartyService;

  private final static int availableViewType = 0;
  private final static int reservedViewType = 1;
  private final static int busyViewType = 2;

  private Context context;
  private OnTableClick onTableClick;
  private OnTableItemClick onTableItemClick;

  private SectionModel diningSection = null;
  private int fistItemPosition = -1;
  private int secondItemPosition = -1;

  public TablesListAdapter(Context context, SectionModel diningSection) {
    this.diningSection = diningSection != null ? diningSection : new SectionModel();
    this.context = context;
    diningPartyService = DiningPartyService_.getInstance_(context);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view;
    SpotStatus status;
    switch (viewType) {
      case availableViewType:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_empty, parent, false);
        status = SpotStatus.available;
        break;
      case busyViewType:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_seated, parent, false);
        status = SpotStatus.busy;
        break;
      case reservedViewType:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_reserved, parent, false);
        status = SpotStatus.reserved;
        break;
      default:
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_empty, parent, false);
        status = SpotStatus.available;
    }
    return new ViewHolder(view, status);
  }

  @Override
  public int getItemViewType(int position) {
    SpotStatus status = diningSection.getSpotStatus(position);
    switch (status) {
      case available:
        return availableViewType;
      case busy:
        return busyViewType;
      case reserved:
        return reservedViewType;
      default:
        return availableViewType;
    }
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    String seats = context.getString(R.string.format_num_seats);
    if (diningSection.getDiningParties() != null && diningSection.getDiningParties().size() > 0) {
      List<DinerSession> dinerSessions = diningSection.getDinerSessionList(position);
      DiningParty diningParty = diningSection.getDiningParties().get(position);

      final DinerSessionAdapter memberAdapter = new DinerSessionAdapter(dinerSessions);
      memberAdapter.setOnMemberClick((session, party) -> onTableItemClick.onItemClick(session, diningParty));
      if (holder.rsGridView != null) {
        holder.rsGridView.setAdapter(memberAdapter);
      }
      if (holder.numSeatsView != null) {
        holder.numSeatsView.setOnClickListener(v -> onUpdateNumSeatsClick(position, dinerSessions.size()));
        holder.numSeatsView.setText(seats);
      }
      if (holder.closeTableView != null) {
        boolean isEnable = diningSection.getDiningParties().get(position).isCanClose();
        holder.closeTableView.setEnabled(isEnable);
        if (isEnable) {
          holder.closeTableView.setTextColor(context.getResources().getColor(R.color.textcolor_lightblue));
        }
        holder.closeTableView.setOnClickListener(v -> {
          holder.closeTableView.setClickable(false);
          holder.closeTableView.setVisibility(View.INVISIBLE);
          Intent intent = new Intent(LayoutModelManager.RECEIVER_CLOSE_TABLE);
          intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, diningSection.getId());
          intent.putExtra(LayoutModelManager.EXTRA_PARTY_ID, diningParty.getId());
          LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });
      }
      holder.tableNameBarView.setOnClickListener(v -> tableNameClickListener(position, diningParty));
    }

    if (holder.tblNumSeatsView != null) {
      holder.tblNumSeatsView.setOnClickListener(v -> onNumSeatsClick(position));
    }

    holder.tableNameBarView.setTableName(diningSection.getSpotNames(context, position));
    holder.view.setOnDragListener(new SpotDragListener(position));
    holder.tableNameBarView.setOnLongClickListener(new SpotLongClickListener(holder.view, position));

    if (holder.tblNumSeatsView != null) {
      holder.tblNumSeatsView.setText(seats);
    }
    holder.tableNameBarView.setCheckBoxChecked(diningSection.isPartyChecked(position));
    holder.tableNameBarView.setOnCheckedChangeListener((buttonView, isChecked) -> {
      diningSection.setCheckedSpotsId(position, isChecked);
      Intent intent = new Intent(LayoutModelManager.RECEIVER_TABLE_STATE);
      intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, diningSection.getId());
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    });

  }

  @Override
  public int getItemCount() {
    return diningSection.getDiningParties().size();
  }

  public void setOnTableClick(OnTableClick onTableClick) {
    this.onTableClick = onTableClick;
  }

  public void setOnTableItemClick(OnTableItemClick onTableItemClick) {
    this.onTableItemClick = onTableItemClick;
  }

  private void tableNameClickListener(int position, DiningParty diningParty) {
    if (onTableClick != null) {
      onTableClick.onClick(position, diningParty);
    }
  }

  private void onNumSeatsClick(int position) {
    int maxCapacity = diningSection.getMaxCapacity(position);
    SeatsNumberDialog seatsNumberDialog = SeatsNumberDialog.newInstance(maxCapacity);
    seatsNumberDialog.setOnNumberOfSeatsClick(number -> {
      List<DiningSectionSpot> spots = diningSection.getSpotsListOfParty(position);
      List<Long> diningSectionSpotIds = new ArrayList<>();
      for (DiningSectionSpot sectionSpot : spots) {
        diningSectionSpotIds.add(sectionSpot.getId());
      }
      List<DinerSession> sessions = diningPartyService.createDinerSessions(number);
      diningPartyService.createDiningParty(callback, diningSectionSpotIds, sessions, position);
    });
    seatsNumberDialog.show(((Activity) context).getFragmentManager());
  }

  private void onUpdateNumSeatsClick(final int position, final int seated) {
    int maxCapacity = 0;
    for (DiningSectionSpot sectionSpot : diningSection.getSpotsListOfParty(position)) {
      maxCapacity += sectionSpot.getMaximumCapacity();
    }
    SeatsNumberDialog seatsNumberDialog = SeatsNumberDialog.newInstanceSeated(maxCapacity);
    seatsNumberDialog.setOnNumberOfSeatsClick(number -> {
      if (number <= seated) {
        Toast.makeText(context, context.getString(R.string.message_reducing_is_not_implemented), Toast.LENGTH_SHORT).show();
        return;
      }
      List<DinerSession> sessions = diningPartyService.createDinerSessions(seated + 1, number);
      Long partyId = diningSection.getDiningParties().get(position).getId();
      DiningParty party = null;
      for (DiningParty diningParty : diningSection.getDiningParties()) {
        if (diningParty.getId() != null && diningParty.getId().equals(partyId)) {
          party = diningParty;
        }
      }
      if (party != null) {
        diningPartyService.addNewSessionToParty(callback, position, party, sessions);
      }
    });
    seatsNumberDialog.show(((Activity) context).getFragmentManager());
  }

  private void merge(int firstPosition, int secondPosition) {
    if (firstPosition != -1 && secondPosition != -1) {
      //Merge only if one of spots are empty
      if (diningSection.getSpotStatus(firstPosition).equals(SpotStatus.available) ||
          diningSection.getSpotStatus(secondPosition).equals(SpotStatus.available)) {
        Long fPartyId = diningSection.getDiningParties().get(firstPosition).getId();
        Long sPartyId = diningSection.getDiningParties().get(secondPosition).getId();

        if (fPartyId != null && sPartyId != null) {
          // doesn't handle for now
        } else if (fPartyId == null && sPartyId == null) {
          List<DiningParty> diningParties = new ArrayList<>();
          diningParties.add(diningSection.getDiningParties().get(firstPosition));
          diningParties.add(diningSection.getDiningParties().get(secondPosition));
          diningPartyService.mergeDiningParty(mergeCallBack, diningParties);
        } else if (fPartyId != null || sPartyId != null) {
          DiningParty party = null;
          Long partyId = fPartyId != null ? fPartyId : sPartyId;
          for (DiningParty diningParty : diningSection.getDiningParties()) {
            if (diningParty.getId() != null && diningParty.getId().equals(partyId)) {
              party = diningParty;
            }
          }
          List<DiningSectionSpot> spots = diningSection.getSpotsListOfParty(fPartyId == null ? firstPosition : secondPosition);
          diningPartyService.mergeTableToDiningParty(callback, firstPosition, party, spots);
        }
      }
    }
  }

  private class SpotDragListener implements View.OnDragListener {

    private final int spotId;

    public SpotDragListener(int spotId) {
      this.spotId = spotId;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
      if (fistItemPosition == -1) {
        return false;
      }
      final int action = event.getAction();
      switch (action) {
        case DragEvent.ACTION_DRAG_ENDED:
          merge(fistItemPosition, secondItemPosition);
          fistItemPosition = -1;
          secondItemPosition = -1;
          return true;
        case DragEvent.ACTION_DRAG_ENTERED:
          secondItemPosition = -1;
          if (spotId != fistItemPosition) {
            secondItemPosition = spotId;
          }
          return true;
        case DragEvent.ACTION_DRAG_EXITED:
          secondItemPosition = -1;
          return true;
      }
      return true;

    }
  }

  private class SpotLongClickListener implements View.OnLongClickListener {
    private View view;
    private int position;

    public SpotLongClickListener(View view, int position) {
      this.view = view;
      this.position = position;
    }

    @Override
    public boolean onLongClick(View v) {
      ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
      String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
      ClipData dragData = new ClipData(Integer.toString(position), mimeTypes, item);
      View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
      view.startDrag(dragData, myShadow, null, 0);
      fistItemPosition = position;
      return true;
    }
  }

  private DiningPartyService.DiningPartyCallBack callback = (position, diningParty) -> {
    Intent intent = new Intent(LayoutModelManager.RECEIVER_REFRESH_TABLES);
    intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, diningSection.getId());
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  };

  private DiningPartyService.MergeDiningPartyCallBack mergeCallBack = (mergedDiningParty, diningParty) -> {
    Intent intent = new Intent(LayoutModelManager.RECEIVER_REFRESH_TABLES);
    intent.putExtra(LayoutModelManager.EXTRA_SECTION_ID, diningSection.getId());
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  };

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TableNameBarView tableNameBarView;
    public TextView closeTableView;
    public TextView payBill;
    public TextView numSeatsView;
    public RSGridView rsGridView;
    public TextView tblNumSeatsView;
    public TextView reservedView;
    public View view;

    public ViewHolder(View view, SpotStatus viewType) {
      super(view);
      this.view = view;
      tableNameBarView = (TableNameBarView) view.findViewById(R.id.table_name_bar);
      closeTableView = (TextView) view.findViewById(R.id.table_close_table_btn);
      payBill = (TextView) view.findViewById(R.id.table_pay_bill_btn);
      numSeatsView = (TextView) view.findViewById(R.id.table_num_seats_btn);
      switch (viewType) {
        case available:
          tblNumSeatsView = (TextView) view.findViewById(R.id.table_empty_seats);
          break;
        case busy:
          rsGridView = (RSGridView) view.findViewById(R.id.table_members);
          break;
        case reserved:
          reservedView = (TextView) view.findViewById(R.id.table_reserved);
          break;
      }
    }
  }

}
