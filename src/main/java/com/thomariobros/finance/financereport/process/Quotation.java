package com.thomariobros.finance.financereport.process;

public class Quotation {

  private String companyCode;

  private String companyLabel;

  private double minValue;

  private double maxValue;

  private double lastValue;

  public Quotation(String companyCode, String companylabel, double minValue, double maxValue, double lastValue) {
    this.companyCode = companyCode;
    this.companyLabel = companylabel;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.lastValue = lastValue;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public void setCompanyCode(String companyCode) {
    this.companyCode = companyCode;
  }

  public String getCompanyLabel() {
    return companyLabel;
  }

  public void setCompanyLabel(String companyLabel) {
    this.companyLabel = companyLabel;
  }

  public double getMinValue() {
    return minValue;
  }

  public void setMinValue(double minValue) {
    this.minValue = minValue;
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }

  public double getLastValue() {
    return lastValue;
  }

  public void setLastValue(double lastValue) {
    this.lastValue = lastValue;
  }

}
