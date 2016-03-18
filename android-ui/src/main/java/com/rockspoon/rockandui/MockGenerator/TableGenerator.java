package com.rockspoon.rockandui.MockGenerator;

import android.content.Context;

import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.item.ItemInstanceStatus;
import com.rockspoon.models.venue.ordering.item.ItemSummary;
import com.rockspoon.models.venue.ordering.ListCartDetails;
import com.rockspoon.rockandui.Objects.MemberData;
import com.rockspoon.rockandui.Objects.TableData;
import com.rockspoon.rockandui.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by lucas on 15/07/15.
 */
public class TableGenerator {

  private final List<TableData> tables;
  public List<ListCartDetails> details;

  public TableGenerator(Context ctx) {
    final TableData table1 = new TableData("Table 1");
    final TableData table2 = new TableData("Table 2");
    final TableData table3 = new TableData("Table 3");
    final TableData table4 = new TableData("Table 4");
    final TableData table5 = new TableData("Table 5");

    tables = new ArrayList<>(5);
    /**
     * Table 1
     */

    table1.setStatus(TableData.TableStatus.SEATED);

    table1.addMember(new MemberData("Person A", "Jul-14", 15, 4000, 10, 3.2f, com.rockspoon.rockandui.R.drawable.test));
    table1.addMember(new MemberData("Person B", "Jan-10", 21, 5000, 40, 1.f, com.rockspoon.rockandui.R.drawable.thumbnail));
    table1.addMember(new MemberData("Person C", "Mar-18", 13, 5432, 50, 3.f, com.rockspoon.rockandui.R.drawable.test));

    table1.addMember(new MemberData("Person with long name", "Jul-14", 15, 4000, 10, 3.2f, R.drawable.test));
    table1.addMember(new MemberData("Person D", "Jul-14", 15, 4000, 10, 3.2f, R.drawable.thumbnail));
    table1.addMember(new MemberData());

    table1.addMember(new MemberData());
    table1.addMember(new MemberData());
    tables.add(table1);

    /**
     * Table 2
     */

    table2.setStatus(TableData.TableStatus.PAID);

    table2.addMember(new MemberData("Person A", "Jul-14", 15, 4000, 10, 3.2f, com.rockspoon.rockandui.R.drawable.test));
    table2.addMember(new MemberData("Person B", "Jan-10", 21, 5000, 40, 1.f, com.rockspoon.rockandui.R.drawable.thumbnail));
    table2.addMember(new MemberData("Person C", "Mar-18", 13, 5432, 50, 3.f, com.rockspoon.rockandui.R.drawable.test));

    table2.addMember(new MemberData());
    table2.addMember(new MemberData());
    tables.add(table2);

    /**
     * Other Tables
     */

    tables.add(table3);
    tables.add(table4);
    tables.add(table5);

    details = new ArrayList<>();
    List<DinerSession> sessions1 = Collections.singletonList(new DinerSession(1L, 1, new Timestamp(new Date().getTime()), null));
    List<ItemSummary> items1 = Arrays.asList(
        new ItemSummary(11L, "Item1", "Description1", ItemInstanceStatus.pending, new Timestamp(new Date().getTime())),
        new ItemSummary(12L, "Item2", "Description2", ItemInstanceStatus.in_progress, new Timestamp(new Date().getTime()))
    );
    details.add(new ListCartDetails(sessions1, items1));

    List<DinerSession> sessions2 = Arrays.asList(
        new DinerSession(1L, 1, new Timestamp(new Date().getTime()), null),
        new DinerSession(2L, 2, new Timestamp(new Date().getTime()), null)
    );
    List<ItemSummary> items2 = Collections.singletonList(new ItemSummary(21L, "Item1", "Description1", ItemInstanceStatus.pending, new Timestamp(new Date().getTime())));
    details.add(new ListCartDetails(sessions2, items2));

    List<DinerSession> sessions3 = Collections.singletonList(new DinerSession(3L, 3, new Timestamp(new Date().getTime()), null));
    List<ItemSummary> items3 = Collections.emptyList();
    details.add(new ListCartDetails(sessions3, items3));

  }

  public List<TableData> getTables() {
    return tables;
  }

}
