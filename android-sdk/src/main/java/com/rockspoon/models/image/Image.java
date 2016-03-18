package com.rockspoon.models.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rockspoon.models.image.resolution.ImageResolution;

import java.io.Serializable;
import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

@Wither
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Serializable {

  @JsonProperty("imageId")
  private String id;
  private Date created;
  private String title;
  private String description;
  private ImageResolution loResolution;
  private ImageResolution noResolution;
  private ImageResolution hiResolution;
  private String buffer;

  public Image(final String buffer) {
    this(null, null, null, null, null, null, null, buffer);
  }
}
