package com.rockspoon.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.venue.brand.Brand;
import com.rockspoon.models.venue.businessentity.OperatingEntity;
import com.rockspoon.models.venue.hr.Employee;
import com.rockspoon.status.Status;

import java.io.Serializable;
import java.util.Date;
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
public class User implements Serializable {
  @JsonProperty("userId")
  private String id;
  private Image avatar;
  private Date birthday;
  private Date created;
  private String email;
  private boolean emailValidated;
  private String firstName;
  private UserGender gender;
  private Date lastLogin;
  private String lastName;
  private Object metadata;
  private String password;
  private String shortPassword;
  private Map<String, String> phone;
  private boolean phoneValidated;
  private Status status;
  private String userName;
  private Set<Employee> jobs;
  private Set<UserAddress> addresses;
  private Set<OperatingEntity> operatingEntities;
  private String[] permissions;
  private Set<Brand> brands;
}
