package com.rockspoon.rockandui.Objects;

import android.content.Context;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.layout.DiningSection;
import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.models.venue.layout.DiningSpot;
import com.rockspoon.models.venue.layout.DiningSpotList;
import com.rockspoon.models.venue.layout.SpotStatus;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.rockandui.Managers.LayoutModelManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Eugen K. on 2/16/16.
 */
@Getter
@Setter
public class SectionModel {

  private Long id;
  private Timestamp created;
  private String name;
  private String description;
  //  private List<DiningSectionSpot> spots;
  private List<DiningParty> diningParties;
  private List<Integer> checkedPartiesPosition;

  public SectionModel() {
    checkedPartiesPosition = new ArrayList<>();
  }

  public String[] getSpotNames(Context context, int id) {
    int size = diningParties.get(id).getLayoutSpots().size();
    String[] names = new String[size];
    for (int i = 0; i < size; i++) {
      names[i] = LayoutModelManager.getSpotName(context, diningParties.get(id).getLayoutSpots().get(i));
    }
    return names;
  }

  public void setDiningParties(List<DiningParty> diningParties, DiningSpotList diningSpotList) {
    this.diningParties = diningParties;
    createDiningParties(diningSpotList);
  }

  public SpotStatus getSpotStatus(int position) {
    return diningParties.get(position).getLayoutSpots().get(0).getStatus();
  }

  public int getMaxCapacity(int position) {
    DiningParty party = diningParties.get(position);
    int maxCapacity = 0;
    for (DiningSectionSpot spot : party.getLayoutSpots()) {
      maxCapacity += spot.getMaximumCapacity();
    }
    return maxCapacity;
  }

  public List<DiningSectionSpot> getSpotsListOfParty(int position) {
    return diningParties.get(position).getLayoutSpots();
  }

  public List<DinerSession> getDinerSessionList(int position) {
    return diningParties.get(position).getDinerSessions();
  }

  public void setCheckedSpotsId(int position, boolean isChecked) {
    if (isChecked) {
      checkedPartiesPosition.add(position);
    } else {
      checkedPartiesPosition.remove(new Integer(position));
    }
  }

  public boolean isPartyChecked(int position) {
    return checkedPartiesPosition.contains(position);
  }

  public LayoutModelManager.CheckedTablesState getCheckedTableState() {
    if (checkedPartiesPosition.size() == 0) {
      return LayoutModelManager.CheckedTablesState.NO_CHECKED;
    } else if (checkedPartiesPosition.size() == 1) {
      DiningParty party = diningParties.get(checkedPartiesPosition.get(0));
      return party != null && party.getLayoutSpots().size() > 1 ? LayoutModelManager.CheckedTablesState.MERGED_PARTY_CHECKED : LayoutModelManager.CheckedTablesState.ONE_CHECKED;
    } else {
      return LayoutModelManager.CheckedTablesState.MORE_THEN_ONE_CHECKED;
    }
  }

  public List<DiningParty> getCheckedDiningParty() {
    List<DiningParty> parties = new ArrayList<>();
    for (int position : checkedPartiesPosition) {
      parties.add(diningParties.get(position));
    }
    return parties;
  }

  public void unCheckParties() {
    checkedPartiesPosition.clear();
  }

  private void createDiningParties(DiningSpotList diningSpotList) {
    Map<Long, SpotStatus> spotStatusMap = spotStatus(diningSpotList);
    List<Long> mergedSpots = new ArrayList<>();
    for (DiningParty party : diningParties) {
      for (DiningSectionSpot spot : party.getLayoutSpots()) {
        mergedSpots.add(spot.getId());
      }
    }
    VenueFloorPlan venueFloorPlan = RockServices.getDataService().getCurrentFloorPlan();
    List<DiningSectionSpot> spots = new ArrayList<>();
    for (DiningSection section : venueFloorPlan.getDiningSections()) {
      if (section.getId().equals(id)) {
        spots = section.getSpots();
      }
    }
    for (DiningSectionSpot spot : spots) {
      if (!mergedSpots.contains(spot.getId())) {
        List<DiningSectionSpot> layoutSpots = new ArrayList<>();
        DiningSectionSpot sectionSpot = spot.withStatus(spotStatusMap.get(spot.getId()));
        layoutSpots.add(sectionSpot);
        diningParties.add(new DiningParty(null, "", null, null, layoutSpots, new ArrayList<DinerSession>(), null, false));
      }
    }
  }

  private Map<Long, SpotStatus> spotStatus(DiningSpotList diningSpotList) {
    Map<Long, SpotStatus> spotStatusMap = new HashMap<>();
    for (DiningSpot spot : diningSpotList.getSpots()) {
      spotStatusMap.put(spot.getId(), spot.getStatus());
    }
    return spotStatusMap;
  }

  private Long getPartiesIds(int position) {
    return diningParties.get(position).getId();
  }

  public void removeParty(DiningParty diningParty) {
    if (diningParty.getId() != null) {
      int position = -1;
      for (int i = 0; i < diningParties.size(); i++) {
        if (diningParty.getId().equals(diningParty.getId())) {
          position = i;
        }
      }
      diningParties.remove(position);
    } else {
      int position = -1;
      for (int i = 0; i < diningParties.size(); i++) {
        Long fSpotId = diningParty.getLayoutSpots().get(0).getId();
        Long sSpotId = diningParties.get(i).getLayoutSpots().get(0).getId();
        if (fSpotId.equals(sSpotId)) {
          position = i;
        }
      }
      diningParties.remove(position);
    }
  }

}
