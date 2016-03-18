package com.rockspoon.kitchentablet.UI.SetupScreen;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * KitchenSetupData.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 3/1/16.
 */
@Getter
@Setter
@NoArgsConstructor
public class KitchenSetupData {

  public enum OrderViewMode {INBOX, IN_PROGRESS, DELIVER}

  private OrderViewMode orderViewMode = OrderViewMode.INBOX;

}
