package com.rockspoon.rockandui.Managers;

import android.content.Context;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.layout.DiningSection;
import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.models.venue.layout.VenueFloorPlan;
import com.rockspoon.rockandui.Interfaces.SectionModelInterface;
import com.rockspoon.rockandui.Objects.SectionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Eugen K. on 2/15/16.
 */
public class LayoutModelManager implements SectionModelInterface {

  public enum CheckedTablesState {
    NO_CHECKED, ONE_CHECKED, MORE_THEN_ONE_CHECKED, MERGED_PARTY_CHECKED;
  }

  public static final String EXTRA_TABLE_STATE = "extraTableState";
  public static final String EXTRA_TABLE_ID = "extraTableId";
  public static final String EXTRA_SECTION_ID = "extraSectionId";
  public static final String RECEIVER_TABLE_STATE = "receiverTableState";
  public static final String RECEIVER_SPOTS_MERGED = "receiverSpotsMerged";
  public static final String RECEIVER_DRAG_AND_DROP_MERGE = "receiverDragAndDropMerge";
  public static final String RECEIVER_UN_MERGE_TABLE = "receiverUnMergeTables";
  public static final String RECEIVER_MERGE_TABLE = "receiverMergeTables";
  public static final String RECEIVER_CLOSE_TABLE = "receiverCloseTables";
  public static final String RECEIVER_REFRESH_TABLES = "receiverRefreshTables";
  public static final String EXTRA_PARTY_ID = "extraPartyId";

  private VenueFloorPlan venueFloorPlan;
  private Context context;
  private HashMap<Long, List<Long>> checkedTables;
  private HashMap<Long, SectionModel> sectionModels;

  public VenueFloorPlan getVenueFloorPlan() {
    return venueFloorPlan;
  }

  public List<DiningSection> getDiningSections() {
    return venueFloorPlan.getDiningSections();
  }

  @Override
  public SectionModel getDiningSectionById(Long id) {
    return sectionModels.get(id);
  }

  public static String getSpotName(Context context, DiningSectionSpot spot) {
    return context.getString(com.rockspoon.rockandui.R.string.table_name, spot.getNumber());
  }

  public LayoutModelManager(Context context) {
    this.context = context;
    venueFloorPlan = RockServices.getDataService().getCurrentFloorPlan();
    checkedTables = new HashMap<>();
    sectionModels = new HashMap<>();
    for (DiningSection section : venueFloorPlan.getDiningSections()) {
      sectionModels.put(section.getId(), convertToSectionModel(section));
    }
  }

  @Override
  public CheckedTablesState setTableState(Long sectionId, Long[] tableId, boolean isChecked) {
    if (isChecked) {
      if (checkedTables.get(sectionId) == null) {
        checkedTables.put(sectionId, new ArrayList<>());
      }
      for (long id : tableId) {
        checkedTables.get(sectionId).add(id);
      }
    } else {
      if (checkedTables.get(sectionId) != null) {
        for (long id : tableId) {
          checkedTables.get(sectionId).remove(id);
        }
      }
    }
    return checkedTablesState(sectionId);
  }

  @Override
  public void merge(Long sectionId) {
    if (checkedTables.get(sectionId) == null || checkedTables.get(sectionId).size() == 0) {
      return;
    }
  }

  @Override
  public CheckedTablesState checkedTablesState(Long sectionId) {
    if (checkedTables.get(sectionId) == null || checkedTables.get(sectionId).size() == 0) {
      return CheckedTablesState.NO_CHECKED;
    }
    return checkedTables.get(sectionId).size() > 1 ? CheckedTablesState.MORE_THEN_ONE_CHECKED : CheckedTablesState.ONE_CHECKED;
  }

  private boolean spotsInChecked(long sectionId, List<DiningSectionSpot> diningSectionSpots) {
    for (long spotId : checkedTables.get(sectionId)) {
      for (DiningSectionSpot spot : diningSectionSpots) {
        if (spot.getId().equals(spotId)) {
          return true;
        }
      }
    }
    return false;
  }

  private SectionModel convertToSectionModel(DiningSection section) {
    SectionModel model = new SectionModel();
    model.setId(section.getId());
    model.setCreated(section.getCreated());
    model.setDescription(section.getDescription());
    model.setName(section.getName());
    return model;
  }

  private DiningSectionSpot getSpotById(Long sectionId, Long spotId) {
    for (DiningSection section : venueFloorPlan.getDiningSections()) {
      if (section.getId().equals(sectionId)) {
        for (DiningSectionSpot spot : section.getSpots()) {
          if (spot.getId().equals(spotId)) {
            return spot;
          }
        }
      }
    }
    return null;
  }


}
