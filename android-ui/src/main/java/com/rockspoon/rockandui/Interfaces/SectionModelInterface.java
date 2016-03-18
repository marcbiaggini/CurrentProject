package com.rockspoon.rockandui.Interfaces;

import com.rockspoon.models.venue.layout.DiningSpotList;
import com.rockspoon.rockandui.Managers.LayoutModelManager;
import com.rockspoon.rockandui.Objects.SectionModel;

/**
 * Created by Eugen K. on 2/16/16.
 */
public interface SectionModelInterface {

  SectionModel getDiningSectionById(Long id);

  LayoutModelManager.CheckedTablesState setTableState(Long sectionId, Long[] tableId, boolean isChecked);

  void merge(Long sectionId);

  LayoutModelManager.CheckedTablesState checkedTablesState(Long sectionId);

//  void setDiningSpotListForSection(Long sectionId, DiningSpotList spotList);

}
