package org.xenia.registration.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
  public Properties props;

  public ConfigReader() {
    props = new Properties();
    try {
      props.load(new FileInputStream(String.format(".%1$sconfig%1$sproject.properties", File.separator)));
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
