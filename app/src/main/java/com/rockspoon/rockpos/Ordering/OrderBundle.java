package com.rockspoon.rockpos.Ordering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rockspoon.models.venue.ordering.DiningParty;

import java.io.IOException;
import java.io.Serializable;

import timber.log.Timber;

public class OrderBundle implements Serializable {
  public DiningParty diningParty = null;
  public long diningSessionId = -1;
  public Integer tableNumber = -1;
  public Integer seatNumber = -1;
  public boolean isAllSelected = false;
  private String tableName;

  public void restorePartyFromJSON(String json) {
    if (json != null && !json.isEmpty()) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        diningParty = mapper.readValue(json, DiningParty.class);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      Timber.i("Restored party instance is empty or null");
    }
  }

  public String convertPartyToJSON() {
    String party = "";
    try {
      ObjectMapper mapper = new ObjectMapper();
      party = mapper.writeValueAsString(diningParty);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    diningParty = null;
    return party;
  }

  public void setDiningParty(DiningParty diningParty) {
    this.diningParty = diningParty;
  }

  public void setDiningSessionId(Long diningSessionId) {
    this.diningSessionId = diningSessionId;
  }

  public void setTableNumber(int tableNumber, boolean clear) {
    this.tableNumber = tableNumber;
    this.isAllSelected = false;
    if (clear) {
      this.seatNumber = -1;
    }
  }

  public void setSeatNumber(int seatNumber, boolean clear) {
    this.seatNumber = seatNumber;
    this.isAllSelected = false;
    if (clear) {
      this.tableNumber = -1;
    }
  }

  public void setNumbers(int tableNumber, int seatNumber) {
    this.tableNumber = tableNumber;
    this.seatNumber = seatNumber;
    this.isAllSelected = false;
  }

  public void setAllSelected(boolean allSelected) {
    this.isAllSelected = allSelected;
    this.seatNumber = -1;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableName() {
    return tableName;
  }
  @Override
  public String toString() {
    return "OrderBundle{" +
        "diningParty=" + diningParty +
        ", diningSessionId=" + diningSessionId +
        ", tableNumber=" + tableNumber +
        ", seatNumber=" + seatNumber +
        ", isAllSeatsSelected=" + isAllSelected +
        ", tableName=" + tableName +
        '}';
  }
}