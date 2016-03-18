package com.rockspoon.models.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {
  @JsonProperty("addressId")
  private String id;
  private String address1;
  private String address2;
  private String city;
  private String country;
  private String instructions;
  private Double latitude;
  private Double longitude;
  private String timezone;
  private String[] media;
  private Boolean publicArea;
  private String region;
  private String zipcode;
}
