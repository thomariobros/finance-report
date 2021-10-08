package com.thomariobros.finance.financereport;

import com.thomariobros.finance.financereport.process.ProcessDefault;
import com.thomariobros.finance.financereport.process.ProcessSBF120;
import com.thomariobros.finance.financereport.process.ProcessUK;

import java.util.Properties;

public class Main {

  public static void main(String[] args) throws Exception {
    // default
    new ProcessDefault().process();
    // sbf 120
    new ProcessSBF120().process();
    // uk
    new ProcessUK().process();
  }

}
