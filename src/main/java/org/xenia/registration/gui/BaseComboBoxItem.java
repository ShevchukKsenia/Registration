package org.xenia.registration.gui;

public class BaseComboBoxItem<T> {
  private T value;
  private String toolTip;
  private Integer index;
  private Integer status;

  public BaseComboBoxItem(T value, String toolTip, Integer index, Integer status) {
    this.value = value;
    this.toolTip = toolTip;
    this.index = index;
    this.status = status;
  }

  @Override
  public String toString() { return value.toString(); }

  public T getValue() { return value; }

  public Integer getIndex() { return index; }

  public Integer getStatus() { return status; }

  public String getToolTip() {
/*
// Example - toolTip at several lines
    if (toolTip != null) {
      String newToolTip = "";
      String toolTipLines[] = toolTip.split(" ");
      int n = toolTipLines.length;
      for (int i = 0; i < n; i++) {
        newToolTip = (i < 1 ? "<html>" : newToolTip) + toolTipLines[i] + (i < n - 1 ? "<br>  " : "</html>");
      }
      return newToolTip;
    }
// End of example
*/
    return toolTip;
  }
}
