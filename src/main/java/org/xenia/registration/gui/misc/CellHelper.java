package org.xenia.registration.gui.misc;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xenia.registration.App;

//import java.time.format.DateTimeFormatter;

public class CellHelper {
  public static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(App.configReader.getProperty("datetimeFormat", "yyyy-MM-dd HH:mm:ss"));
  public static DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(App.configReader.getProperty("timeFormat","HH:mm:ss"));
  public static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(App.configReader.getProperty("dateFormat","yyyy-MM-dd"));

}
