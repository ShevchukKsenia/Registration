package org.xenia.registration.lang;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BaseBigDecimal extends BigDecimal {
  private BigDecimal bdValue;
  private String format;

  public BaseBigDecimal(String val) {
    super(val);
  }

  public BaseBigDecimal(String val, String format) {
    super(val);
    this.bdValue = new BigDecimal(val);
    this.format = format;
  }

  @Override
  public double doubleValue() {
    return this.bdValue.doubleValue();
  }

  @Override
  public String toString() {
    return (this == null ? null : format == null ? super.toString() : new DecimalFormat(format).format(bdValue));
  }
}
