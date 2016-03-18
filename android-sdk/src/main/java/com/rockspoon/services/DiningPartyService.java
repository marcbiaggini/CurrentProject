package com.rockspoon.services;

import com.rockspoon.helpers.RockServices;
import com.rockspoon.models.venue.layout.DiningSectionSpot;
import com.rockspoon.models.venue.layout.DiningSpot;
import com.rockspoon.models.venue.layout.DiningSpotList;
import com.rockspoon.models.venue.layout.DiningSpotsListRequest;
import com.rockspoon.models.venue.ordering.DinerSession;
import com.rockspoon.models.venue.ordering.DiningParty;
import com.rockspoon.models.venue.ordering.DiningPartyCreateRequest;
import com.rockspoon.models.venue.ordering.DiningPartyListRequest;
import com.rockspoon.models.venue.ordering.DiningPartyStatus;
import com.rockspoon.models.venue.ordering.DiningPartyUpdateRequest;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eugen K. on 3/2/16.
 */
@EBean
public class DiningPartyService {

  public interface DiningPartyCallBack {
    void diningPartyCallBack(int position, DiningParty diningParty);
  }

  public interface MergeDiningPartyCallBack {
    void diningPartyCallBack(DiningParty mergedDiningParty, List<DiningParty> diningParty);
  }

  @Background
  public void createDiningParty(DiningPartyCallBack callback, List<Long> diningSectionSpotIds, List<DinerSession> diners, int position) {
    DiningPartyCreateRequest request = new DiningPartyCreateRequest("name", diningSectionSpotIds, diners);
    if (callback != null) {
      DiningParty diningParty = RockServices.getOrderService().createDiningParty(request);
      diningPartyCallBack(callback, position, diningParty);
    }
  }

  @UiThread
  void diningPartyCallBack(DiningPartyCallBack callback, int position, DiningParty diningParty) {
    callback.diningPartyCallBack(position, diningParty);
  }

  public List<DinerSession> createDinerSessions(int seats) {
    List<DinerSession> sessions = new ArrayList<>();
    for (int i = 1; i <= seats; i++) {
      DinerSession dinerSession = new DinerSession(null, i, new Timestamp(System.currentTimeMillis()), null);
      sessions.add(dinerSession);
    }
    return sessions;
  }

  public List<DinerSession> createDinerSessions(int fromSeat, int toSeat) {
    List<DinerSession> sessions = new ArrayList<>();
    for (int i = fromSeat; i <= toSeat; i++) {
      DinerSession dinerSession = new DinerSession(null, i, new Timestamp(System.currentTimeMillis()), null);
      sessions.add(dinerSession);
    }
    return sessions;
  }

  public DiningSpotList requestDiningSpotList(Long sectionId) {
    if (sectionId == null) {
      return null;
    }
    List<Long> list = new ArrayList<>();
    list.add(sectionId);
    DiningSpotsListRequest diningSpotsListRequests = new DiningSpotsListRequest(list);
    return RockServices.getOrderService().spotsListBySectionId(diningSpotsListRequests);
  }

  public List<DiningParty> requestDiningPartyList(DiningSpotList spotList) {
    if (spotList == null || spotList.getSpots() == null || spotList.getSpots().size() == 0) {
      return new ArrayList<DiningParty>();
    }
    List<Long> ids = new ArrayList<>();
    for (DiningSpot diningSpot : spotList.getSpots()) {
      if (diningSpot.getDiningPartyId() != null) {
        ids.add(diningSpot.getDiningPartyId());
      }
    }
    DiningPartyListRequest request = new DiningPartyListRequest(ids);
    return ids.size() > 0 ? RockServices.getOrderService().listDiningParty(request) : new ArrayList<DiningParty>();
  }

  @Background
  public void addNewSessionToParty(DiningPartyCallBack callBack, int position, DiningParty party, List<DinerSession> sessionsToAdd) {
    if (party != null && sessionsToAdd != null && sessionsToAdd.size() > 0) {
      String name = party.getName();
      DiningPartyStatus status = party.getStatus();
      String partyIdToUpdate = party.getId().toString();
      DiningPartyUpdateRequest requestToAddSpotsAndSession = new DiningPartyUpdateRequest(name,
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          sessionsToAdd,
          null);
      DiningParty responseParty = RockServices.getOrderService().updateDiningParty(partyIdToUpdate, requestToAddSpotsAndSession);
      if (callBack != null) {
        diningPartyCallBack(callBack, position, responseParty);
      }
    }
  }

  @Background
  public void swapSession(DiningPartyCallBack callBack, int position, DiningParty party) {

  }

  @Background
  public void mergeTableToDiningParty(DiningPartyCallBack callBack, int position, DiningParty party, List<DiningSectionSpot> spotsList) {
    String name = party.getName();
    String partyIdToUpdate = party.getId().toString();
    List<Long> spotsIdToAdd = new ArrayList<>();
    for (DiningSectionSpot sectionSpot : spotsList) {
      spotsIdToAdd.add(sectionSpot.getId());
    }

    DiningPartyUpdateRequest requestToAddSpots = new DiningPartyUpdateRequest(name,
        new ArrayList<>(),
        spotsIdToAdd,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        null);
    DiningParty responseParty = RockServices.getOrderService().updateDiningParty(partyIdToUpdate, requestToAddSpots);
    if (callBack != null) {
      diningPartyCallBack(callBack, position, responseParty);
    }
  }

  @Background
  public void mergeDiningParty(MergeDiningPartyCallBack callBack, List<DiningParty> partyList) {
    if (partyList != null && partyList.size() > 0) {

      List<DiningParty> parties = new ArrayList<>();
      List<DiningParty> emptyParties = new ArrayList<>();
      for (DiningParty party : partyList) {
        if (party.getId() != null) {
          parties.add(party);
        } else {
          emptyParties.add(party);
        }
      }

      if (parties.size() == 0 && emptyParties.size() > 0) {
        mergeEmptySpots(callBack, emptyParties);
      } else if (parties.size() > 0 && emptyParties.size() > 0) {
        mergeToExistingParty(callBack, parties, emptyParties);
      } else if (parties.size() > 0 && emptyParties.size() == 0) {
        //mergeExistingParties(callBack, parties);
      }
    }
  }

  private void mergeExistingParties(MergeDiningPartyCallBack callBack, List<DiningParty> partyList) {
    String name = partyList.get(0).getName();

    String partyIdToUpdate = partyList.get(0).getId().toString();
    List<DiningSectionSpot> spotsToAdd = new ArrayList<>();
    List<DinerSession> newSession = new ArrayList<>();

    for (int i = 1; i < partyList.size(); i++) {
      spotsToAdd.addAll(partyList.get(i).getLayoutSpots());
      newSession.addAll(partyList.get(i).getDinerSessions());
      removeSpotsAndSession(partyList.get(i));
    }
    List<DinerSession> sessionsToAdd = new ArrayList<>();
    int startingSeatNumber = partyList.get(0).getDinerSessions().size() + 1;
    for (int i = 0; i < spotsToAdd.size(); i++) {
      sessionsToAdd.add(newSession.get(i).withSeatNumber(i + startingSeatNumber));
    }

    List<Long> spotsIdToUpdate = new ArrayList<>();
    for (DiningSectionSpot spot : spotsToAdd) {
      spotsIdToUpdate.add(spot.getId());
    }
    DiningPartyUpdateRequest requestToAddSpotsAndSession = new DiningPartyUpdateRequest(name,
        new ArrayList<>(),
        spotsIdToUpdate,
        new ArrayList<>(),
        new ArrayList<>(),
        sessionsToAdd,
        null);
    DiningParty party = RockServices.getOrderService().updateDiningParty(partyIdToUpdate, requestToAddSpotsAndSession);
    if (callBack != null) {
      partyList.remove(0);
      mergeDiningPartyCallBack(callBack, party, partyList);
    }
  }

  private void mergeToExistingParty(MergeDiningPartyCallBack callBack, List<DiningParty> parties, List<DiningParty> emptyParties) {

    String name = parties.get(0).getName();
    String partyIdToUpdate = parties.get(0).getId().toString();

    List<Long> spotsIdToAdd = new ArrayList<>();
    for (DiningParty party : emptyParties) {
      spotsIdToAdd.add(party.getLayoutSpots().get(0).getId());
    }

    List<DinerSession> sessionsToAdd = new ArrayList<>();

    if (parties.size() > 1) {
      for (int i = 1; i < parties.size(); i++) {
        sessionsToAdd.addAll(parties.get(i).getDinerSessions());
        spotsIdToAdd.addAll(spotsId(parties.get(i)));
        removeSpotsAndSession(parties.get(i));
      }
    }

    DiningPartyUpdateRequest requestToAddSpots = new DiningPartyUpdateRequest(name,
        new ArrayList<>(),
        spotsIdToAdd,
        new ArrayList<>(),
        new ArrayList<>(),
        sessionsToAdd,
        null);
    DiningParty responseParty = RockServices.getOrderService().updateDiningParty(partyIdToUpdate, requestToAddSpots);
    if (callBack != null) {
      mergeDiningPartyCallBack(callBack, null, null);
    }
  }

  private List<Long> spotsId(DiningParty diningParty) {
    List<Long> list = new ArrayList<>();
    for (DiningSectionSpot spot : diningParty.getLayoutSpots()) {
      list.add(spot.getId());
    }
    return list;
  }

  private void mergeEmptySpots(MergeDiningPartyCallBack callBack, List<DiningParty> partyList) {
    List<Long> diningSectionSpotIds = new ArrayList<>();
    for (DiningParty party : partyList) {
      Long id = party.getLayoutSpots().get(0).getId();
      diningSectionSpotIds.add(id);
    }
    DiningPartyCreateRequest request = new DiningPartyCreateRequest("name", diningSectionSpotIds, new ArrayList<>());
    DiningParty diningParty = RockServices.getOrderService().createDiningParty(request);
    if (callBack != null) {
      mergeDiningPartyCallBack(callBack, diningParty, partyList);
    }
  }

  @UiThread
  public void mergeDiningPartyCallBack(MergeDiningPartyCallBack callBack, DiningParty party, List<DiningParty> partyList) {
    callBack.diningPartyCallBack(party, partyList);
  }

  @Background
  public void unMergeParty(MergeDiningPartyCallBack callBack, DiningParty diningParty) {
    DiningParty party = null;
    List<DiningParty> diningParties = new ArrayList<>();
    if (diningParty.getDinerSessions().size() == 0) {
      party = closeTable(diningParty);
    } else {
      List<Long> spotsIdsToRemove = new ArrayList<>();
      List<DiningSectionSpot> spotsList = diningParty.getLayoutSpots();
      for (int i = 1; i < diningParty.getLayoutSpots().size(); i++) {
        spotsIdsToRemove.add(diningParty.getLayoutSpots().get(i).getId());
        List<DiningSectionSpot> layoutSpots = new ArrayList<>();
        layoutSpots.add(spotsList.get(i));
        diningParties.add(new DiningParty(null, "name", null, null, layoutSpots, new ArrayList<DinerSession>(), null, false));
      }
      DiningPartyUpdateRequest requestToUnMergeParty = new DiningPartyUpdateRequest(diningParty.getName(),
          spotsIdsToRemove,
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          null);
      party = RockServices.getOrderService().updateDiningParty(Long.toString(diningParty.getId()), requestToUnMergeParty);
    }
    if (callBack != null) {
      unMergePartyCallBack(callBack, party, diningParties);
    }
  }

  @UiThread
  public void unMergePartyCallBack(MergeDiningPartyCallBack callBack, DiningParty party, List<DiningParty> diningParties) {
    callBack.diningPartyCallBack(party, diningParties);
  }

  @Background
  public void closeTable(MergeDiningPartyCallBack callBack, DiningParty diningParty) {
    DiningParty party = closeTable(diningParty);
    if (callBack != null) {
      closePartyCallBack(callBack);
    }
  }

  private DiningParty closeTable(DiningParty diningParty) {
    List<Long> spotsIdsToRemove = new ArrayList<>();
    List<DinerSession> sessionsToRemove = diningParty.getDinerSessions();
    for (DiningSectionSpot spot : diningParty.getLayoutSpots()) {
      spotsIdsToRemove.add(spot.getId());
    }
    DiningPartyUpdateRequest requestToCloseParty = new DiningPartyUpdateRequest(diningParty.getName(),
        spotsIdsToRemove,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        sessionsToRemove,
        DiningPartyStatus.closed);
    return RockServices.getOrderService().updateDiningParty(Long.toString(diningParty.getId()), requestToCloseParty);
  }

  @UiThread
  public void closePartyCallBack(MergeDiningPartyCallBack callBack) {
    callBack.diningPartyCallBack(null, null);
  }

  private void removeSpotsAndSession(DiningParty diningParty) {
    List<Long> spotsIdsToRemove = new ArrayList<>();
    for (DiningSectionSpot spot : diningParty.getLayoutSpots()) {
      spotsIdsToRemove.add(spot.getId());
    }
    DiningPartyUpdateRequest requestToRemoveSpotsAndSession = new DiningPartyUpdateRequest(diningParty.getName(),
        spotsIdsToRemove,
        new ArrayList<>(),
        diningParty.getDinerSessions(),
        new ArrayList<>(),
        new ArrayList<>(),
        null);
    RockServices.getOrderService().updateDiningParty(Long.toString(diningParty.getId()), requestToRemoveSpotsAndSession);
  }

}
