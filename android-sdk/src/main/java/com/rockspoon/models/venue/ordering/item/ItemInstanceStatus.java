package com.rockspoon.models.venue.ordering.item;

/**
 * Created by lucas on 03/02/16.
 */
public enum ItemInstanceStatus {
  pending,               // The item has been added to the order (dining party cart) and not yet fired by waiter
  scheduled,             // Waiter has scheduled a firing for the item in the future
  fired,                 // The item has been fired (pops up at the kitchen tablet)
  in_progress,           // By the time any production area started working on that item
  deleted,
  canceled,
  voided,                // A customer returns the item for some reason
  ready_for_delivery,    // The item has met the conditions to be delivered for the customer
  delivered              // Runner did his job
}
