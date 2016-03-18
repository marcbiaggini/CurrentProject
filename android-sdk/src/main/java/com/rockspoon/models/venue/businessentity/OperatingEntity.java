package com.rockspoon.models.venue.businessentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.address.Address;
import com.rockspoon.models.venue.hr.Employee;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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
public class OperatingEntity {

  @JsonProperty("operatingEntityId")
  private String id;
  private String doingBusinessAs;
  private Address address;
  private String businessType;
  private String taxId;
  private List<Map<String, Object>> internet;
  private List<Map<String, Object>> phone;
  private Timestamp created;
  private Set<Employee> employees;
  private String name;
}
