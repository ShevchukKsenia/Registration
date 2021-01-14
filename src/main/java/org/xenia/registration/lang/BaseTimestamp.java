package org.xenia.registration.lang;

import org.xenia.registration.App;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BaseTimestamp extends Timestamp {
  String format;

  public BaseTimestamp(long time) {
    super(time);
  }

  public BaseTimestamp(long time, String format) {
    super(time);
    this.setTime(time);
    this.format = format;
  }
/*
  public BaseTimestamp(String timeStr, String format) {
    super(0L);
    this.setTime(super.valueOf(timeStr).getTime());
    this.format = format;
  }
*/
  public static BaseTimestamp valueOf(String timeStr) {
    try {
      String format = App.configReader.getProperty("datetimeFormat", "yyyy-MM-dd hh:mm:ss");
      SimpleDateFormat dateFormat = new SimpleDateFormat(format);
      return new BaseTimestamp(dateFormat.parse(timeStr).getTime(), format);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String toString() {
    return (this == null ? null : (format == null || format.isEmpty()) ? super.toString() : DateTimeFormatter.ofPattern(format).format(((Timestamp) this).toLocalDateTime()));
  }

  public String getFormat() {
    return format;
  }
}
