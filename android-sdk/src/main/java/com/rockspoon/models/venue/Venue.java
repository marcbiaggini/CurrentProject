package com.rockspoon.models.venue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.address.Address;
import com.rockspoon.models.venue.brand.Brand;
import com.rockspoon.models.venue.businessentity.OperatingEntity;
import com.rockspoon.models.venue.device.Device;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.models.venue.image.VenueImage;
import com.rockspoon.models.venue.network.Network;
import com.rockspoon.models.venue.settingsprices.SettingsAndPrices;
import com.rockspoon.status.Status;

import java.io.Serializable;
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
public class Venue implements Serializable {

  @JsonProperty("venueId")
  private String id;
  private Address address;
  private Set<VenueImage> gallery;
  private Set<SettingsAndPrices> settingsAndPrices;
  private Set<Device> devices;
  private OperatingEntity operatingEntity;
  private Brand brand;
  private List<Map<String, Object>> phones;
  private List<Map<String, Object>> internet;
  private String email;
  private Timestamp created;
  private Status status;
  private List<Network> networks;
  private Set<Employee> employees;
  private Boolean useOperatingAddress;
}
