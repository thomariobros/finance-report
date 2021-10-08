package com.thomariobros.finance.financereport.html;

import com.thomariobros.finance.financereport.process.Computation;
import com.thomariobros.finance.financereport.process.Quotation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

public class HtmlBuilder {

  private static final Logger LOGGER = Logger.getLogger(HtmlBuilder.class.getName());

  public String build(Map<String, Map<Date, Quotation>> values) {
    StringBuilder contentBuilder = new StringBuilder("<!DOCTYPE html><html><body>");
    contentBuilder.append("<table border=\"1\">");
    contentBuilder.append("<tr>");
    contentBuilder.append("<th>Code</th>");
    contentBuilder.append("<th>Libellé</th>");
    contentBuilder.append("<th>%R de Williams (14)</th>");
    contentBuilder.append("</tr>");
    for (Map.Entry<String, Map<Date, Quotation>> entry : values.entrySet()) {
      Computation computation = new Computation(entry.getValue());
      double percentageRWilliams = computation.calculatePercentageRWilliams();
      String companyLabel = new ArrayList<Quotation>(entry.getValue().values()).get(0).getCompanyLabel();
      companyLabel = companyLabel.substring(0, Math.min(30, companyLabel.length()));
      LOGGER.info("Company " + entry.getKey() + " " + companyLabel + " : " + "%R Williams : " + percentageRWilliams);
      contentBuilder.append("<tr>");
      contentBuilder.append("<td>").append(entry.getKey()).append("</td>");
      contentBuilder.append("<td>").append(companyLabel).append("</td>");
      String style = "text-align:right;";
      if (percentageRWilliams <= 0 && percentageRWilliams > -15) {
        style += "font-weight:bold;background-color:#31961D;";
      } else if (percentageRWilliams <= -15 && percentageRWilliams >= -20) {
        style += "font-weight:bold;background-color:#98EB13;";
      } else if (percentageRWilliams <= -80 && percentageRWilliams > -85) {
        style += "font-weight:bold;background-color:#F0A669;";
      } else if (percentageRWilliams <= -85) {
        style += "font-weight:bold;background-color:#EB1337;";
      }
      contentBuilder.append("<td style='").append(style).append("'>").append(percentageRWilliams).append("</td>");
      contentBuilder.append("</tr>");
    }
    contentBuilder.append("</table></body></html>");
    return contentBuilder.toString();
  }

}
