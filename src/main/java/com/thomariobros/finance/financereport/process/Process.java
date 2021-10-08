package com.thomariobros.finance.financereport.process;

import com.thomariobros.finance.financereport.datasource.DataSourceBoursorama;
import com.thomariobros.finance.financereport.datasource.DataSourceBoursoramaType;
import com.thomariobros.finance.financereport.html.HtmlBuilder;
import com.thomariobros.finance.financereport.mail.SendMail;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class Process {

  private static final Logger LOGGER = Logger.getLogger(Process.class.getName());

  protected void process(final DataSourceBoursoramaType type, final String subject) throws Exception {
    // data
    final DataSourceBoursorama dataSource = new DataSourceBoursorama(type);
    final String username = System.getenv().getOrDefault("DATA_SOURCE_BOURSORAMA_USERNAME", "");
    final String password = System.getenv().getOrDefault("DATA_SOURCE_BOURSORAMA_PASSWORD", "");
    dataSource.login(username, password);
    final Map<String, Map<Date, Quotation>> values = dataSource.load(14);
    dataSource.logout();

    // computation
    final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    for (Map.Entry<String, Map<Date, Quotation>> entry1 : values.entrySet()) {
      LOGGER.info("Company " + entry1.getKey() + " :");
      for (Map.Entry<Date, Quotation> entry2 : entry1.getValue().entrySet()) {
        LOGGER.info(dateFormat.format(entry2.getKey()) + " : minValue=" + entry2.getValue().getMinValue());
        LOGGER.info(dateFormat.format(entry2.getKey()) + " : maxValue=" + entry2.getValue().getMaxValue());
        LOGGER.info(dateFormat.format(entry2.getKey()) + " : lastValue=" + entry2.getValue().getLastValue());
      }
    }
    // output
    final String html = new HtmlBuilder().build(values);
    if (StringUtils.isEmpty(html)) {
      return;
    }
    if (Boolean.valueOf(System.getenv().getOrDefault("EMAIL_SEND", Boolean.FALSE.toString()))) {
      new SendMail().send(subject, html);
    } else {
      // save to file
      FileUtils.writeStringToFile(new File("output-" + type.toString().toLowerCase() + ".html"), html, Charset.forName("UTF-8"));
    }
  }

  public abstract void process() throws Exception;

}
