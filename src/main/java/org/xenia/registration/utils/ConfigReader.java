package org.xenia.registration.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
  private static ConfigReader instance = null;
  private static Properties props;

  private ConfigReader() { }

  public static ConfigReader getInstance(String fileName) {
    if (instance == null) load(fileName);
    return instance;
  }

  private static void load(String fileName) {
    instance = new ConfigReader();
    props = new Properties();
    File setupFile = new File("config/" + fileName);
    try {
      Workbook wb = WorkbookFactory.create(new FileInputStream(setupFile));
      Sheet sheet = wb.getSheet("properties");
      Row row = sheet.getRow(0);
      int iKey = -1;
      int iVal = -1;
      for (int iCell = 0; iCell < row.getLastCellNum() && (iKey < 0 || iVal < 0); iCell++) {
        String cellString = row.getCell(iCell).toString();
        if (cellString.equals("key") && iKey < 0) {
          iKey = iCell;
        }
        if (cellString.equals("value") && iVal < 0) {
          iVal = iCell;
        }
      }
      for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
        String key = sheet.getRow(iRow).getCell(iKey).toString();
        String val = sheet.getRow(iRow).getCell(iVal).toString();
        if (!key.isEmpty() && !val.isEmpty()) {
          props.setProperty(key, val);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getProperty(String propertyName) {
    return props.getProperty(propertyName);
  }

  public String getProperty(String propertyName, String defaultValue) {
    return props.getProperty(propertyName, defaultValue);
  }

}
