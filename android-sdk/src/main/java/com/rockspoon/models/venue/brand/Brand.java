package com.rockspoon.models.venue.brand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.image.Image;
import com.rockspoon.models.user.User;

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
public class Brand {
  @JsonProperty("brandId")
  private String id;
  private String name;
  private Image logo;
  private String description;
  private List<Map<String, Object>> internet;
  private Set<User> owners;
  private Timestamp created;
}
