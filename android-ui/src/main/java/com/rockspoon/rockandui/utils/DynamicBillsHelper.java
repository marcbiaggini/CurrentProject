package com.rockspoon.rockandui.utils;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DynamicBillsHelper.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 2/26/16.
 */
public class DynamicBillsHelper {

  /* Routine:
    * 1 - Find first solution: round up the bill value for the closer multiple of 5
    * 2 - After that is initiated a bunch of test combinations:
    * 3 - If the first solution ends with 5, the second solution will be add 5 more.
    * 4 - Try multiples os 20.
    * 5 - Try paper bill values (when total bill is under 100)
    * 6 - Round up the bill value
    * 7 - Try first solution plus 10.
    * 8 - Try multiples os 100.
    * 9 - Try last solution plus 10.
   */

  private static BigDecimal[] paperBillValuesUSA = {  //USA Standard - dollar bills (US$1 removed)
      new BigDecimal("5"),
      new BigDecimal("10"),
      new BigDecimal("20"),
      new BigDecimal("50"),
      new BigDecimal("100")};

  private static List<BigDecimal> dynamicBillOptionsList = new ArrayList<>();
  private static boolean firstNumEndsWithFive = false;

  private static BigDecimal findFirstSolution(BigDecimal totalBillValue) {
    BigDecimal firstOption;

    if (totalBillValue.remainder(new BigDecimal("5")).compareTo(BigDecimal.ZERO) == 0) {
      firstOption = totalBillValue;
    } else {
      BigDecimal remainder = totalBillValue.remainder(new BigDecimal("10"));

      if (remainder.compareTo(new BigDecimal("0.01")) > 0 && remainder.compareTo(new BigDecimal("4.99")) < 0) {
        firstOption = new BigDecimal(totalBillValue.divide(new BigDecimal("10"), 0, RoundingMode.DOWN) + "5");
        firstNumEndsWithFive = true;
      } else {
        firstOption = new BigDecimal(totalBillValue.divide(new BigDecimal("10"), 0, RoundingMode.DOWN) + "0")
            .add(new BigDecimal("10"));
      }
    }

    return firstOption;
  }

  private static void optionsWithPaperBillValues(BigDecimal amount) {
    if (amount.compareTo(new BigDecimal("100")) < 0) {
      for (BigDecimal paperBillValue : paperBillValuesUSA) {
        if (amount.compareTo(paperBillValue) < 0) {
          addBillOption(paperBillValue);
        }
      }
    }
  }

  private static void findPossibleCombinations(BigDecimal firstOptionFound, BigDecimal amount) {
    BigDecimal option;

    /* FIVE next number multiple of five */
    if (firstNumEndsWithFive) {
      option = firstOptionFound.add(paperBillValuesUSA[0]);
      addBillOption(option);
    }

    /* TWENTY multiples */
    option = BigDecimal.ZERO;
    while (option.compareTo(firstOptionFound) < 0) {
      option = option.add(paperBillValuesUSA[2]);
    }
    addBillOption(option);

    /* Find and fill options with paper bill values */
    if (hasOpenOptions()) {
      optionsWithPaperBillValues(amount);
    }

    /* Adjustment: Add the closest number rounded up of the bill value */
    addBillOption(amount.setScale(0, RoundingMode.UP));

    /* TEN plus the first solution */
    option = firstOptionFound.add(paperBillValuesUSA[1]);
    if (!option.remainder(new BigDecimal("10")).equals(new BigDecimal("5"))) {
      addBillOption(option);
    }

    // THOUSAND multiples
    option = BigDecimal.ZERO;
    while (option.compareTo(firstOptionFound) < 0) {
      option = option.add(paperBillValuesUSA[4]);
    }
    addBillOption(option);

    /* TEN plus the last solution */
    option = dynamicBillOptionsList.get(dynamicBillOptionsList.size() - 1).add(paperBillValuesUSA[1]);
    if (!option.remainder(new BigDecimal("10")).equals(new BigDecimal("5"))) {
      addBillOption(option);
    }

    // Adjustments - Add a zero as solution. Perfectly there not be open options at this step.
    if (hasOpenOptions()) {
      addBillOption(BigDecimal.ZERO);
    }
  }

  private static void printSolution(BigDecimal totalBillValue) {
    Log.i("DynamicBillsHelper", "Available Options: " + dynamicBillOptionsList.size());
    Log.i("DynamicBillsHelper", "Total Bill Value: " + totalBillValue);
    Log.i("DynamicBillsHelper", "Options List: ");

    for (int index = 0; index < dynamicBillOptionsList.size(); index++) {
      BigDecimal solution = dynamicBillOptionsList.get(index);
      Log.i("DynamicBillsHelper", solution.toString());

      if (index < dynamicBillOptionsList.size() - 1) {
        Log.i("DynamicBillsHelper", " - ");
      }
    }
  }

  private static void addBillOption(BigDecimal option) {
    if (!dynamicBillOptionsList.contains(option) && hasOpenOptions()) {
      dynamicBillOptionsList.add(option);
    }
  }

  private static boolean hasOpenOptions() {
    return dynamicBillOptionsList.size() < 4;
  }

  public static List<BigDecimal> populateBills(BigDecimal amount) {
    dynamicBillOptionsList.clear();
    firstNumEndsWithFive = false;

    BigDecimal firstOptionFound = findFirstSolution(amount);
    addBillOption(firstOptionFound);

    findPossibleCombinations(firstOptionFound, amount);
    Collections.sort(dynamicBillOptionsList);

    printSolution(amount);
    return dynamicBillOptionsList;
  }
}
