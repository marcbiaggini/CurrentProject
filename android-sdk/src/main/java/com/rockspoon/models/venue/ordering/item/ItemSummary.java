package com.rockspoon.models.venue.ordering.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Created by greenfrvr
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemSummary implements Serializable {
  protected final Long itemInstanceId;
  protected final String name;
  protected final String modifiersDescription;
  protected final ItemInstanceStatus status;
  protected final Timestamp preparationStartedAt;
}
