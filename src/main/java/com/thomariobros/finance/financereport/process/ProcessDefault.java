package com.thomariobros.finance.financereport.process;

import com.thomariobros.finance.financereport.datasource.DataSourceBoursoramaType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ProcessDefault extends Process {

  public void process() throws Exception {
    final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    final String subject = "Rapport financier au " + dateFormat.format(new Date());
    super.process(DataSourceBoursoramaType.LIST, subject);
  }

}
