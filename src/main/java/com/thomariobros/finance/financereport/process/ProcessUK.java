package com.thomariobros.finance.financereport.process;

import com.thomariobros.finance.financereport.datasource.DataSourceBoursoramaType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ProcessUK extends Process {

  public void process() throws Exception {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String subject = "Rapport financier UK au " + dateFormat.format(new Date());
    super.process(DataSourceBoursoramaType.UK, subject);
  }

}
