package com.rockspoon.models.venue.hr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.user.User;
import com.rockspoon.models.venue.Venue;
import com.rockspoon.models.venue.businessentity.OperatingEntity;
import com.rockspoon.models.venue.ordering.ProductionAreaStationFiredItem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

  @JsonProperty("employeeId")
  private String id;
  private OperatingEntity operatingEntity;
  private Set<Venue> workingAt;
  private User user;
  private Role role;
  private Date workAnniversary;
  private Object emergencyContact;
  private EmployeeStatus status;
  private EmploymentStatus employmentStatus;
  private BigDecimal salary;
}
