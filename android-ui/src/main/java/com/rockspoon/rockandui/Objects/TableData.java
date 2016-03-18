package com.rockspoon.rockandui.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 15/07/15.
 */
public class TableData {

  private final String tableName;
  private final List<MemberData> members = new ArrayList<>();
  private TableStatus status;
  private boolean isTableChecked;
  private long spotId;

  public TableData() {
    this("Unknown");
  }

  public TableData(String tableName) {
    this.tableName = tableName;
    status = TableStatus.EMPTY;
  }

  public void addMember(final MemberData member) {
    members.add(member);
  }

  public String getTableName() {
    return tableName;
  }

  public TableStatus getStatus() {
    return status;
  }

  public void setStatus(TableStatus status) {
    this.status = status;
  }

  public List<MemberData> getMembers() {
    return members;
  }

  public List<MemberData> getNonFreeMembers() {
    final List<MemberData> nonFree = new ArrayList<MemberData>(members.size());
    for (final MemberData member : members)
      if (!member.isFree())
        nonFree.add(member);

    return nonFree;
  }

  public boolean isTableChecked() {
    return isTableChecked;
  }

  public void setTableCheckedValue(boolean value) {
    isTableChecked = value;
  }

  public enum TableStatus {
    EMPTY,
    SEATED,
    PAID
  }

}
