package com.thomariobros.finance.financereport.process;

import java.util.*;

public class Computation {

  private Map<Date, Quotation> last14weeksValues;

  public Computation(Map<Date, Quotation> values14weeks) {
    this.last14weeksValues = values14weeks;
  }

  public double calculatePercentageRWilliams() {
    double minValue = -1;
    double maxValue = -1;
    for (Quotation quotation : this.last14weeksValues.values()) {
      if (minValue == -1) {
        minValue = quotation.getMinValue();
      }
      if (maxValue == -1) {
        maxValue = quotation.getMaxValue();
      }
      minValue = Math.min(minValue, quotation.getMinValue());
      maxValue = Math.max(maxValue, quotation.getMaxValue());
    }
    List<Date> dates = new ArrayList<Date>(this.last14weeksValues.keySet());
    Collections.sort(dates, new Comparator<Date>() {
      public int compare(Date d1, Date d2) {
        return d2.compareTo(d1);
      }
    });
    Date date = dates.get(0);
    Quotation lastQuotation = this.last14weeksValues.get(date);
    double result = 0;
    if (maxValue - minValue != 0) {
      result = -100 * (maxValue - lastQuotation.getLastValue()) / (maxValue - minValue);
      result = (double) Math.round(result * 10) / 10;
    }

    return result;
  }

}
