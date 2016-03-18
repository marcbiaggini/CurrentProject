package com.rockspoon.helpers.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rockspoon.libraries.printer.PrinterJob;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Created by lucas on 10/03/16.
 */
@Wither
@Value
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintEvent implements Serializable, CacheBusEvent {
  PrinterJob job;
}
