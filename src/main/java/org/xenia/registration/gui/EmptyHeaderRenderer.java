package org.xenia.registration.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class EmptyHeaderRenderer extends DefaultTableCellRenderer {
  public EmptyHeaderRenderer() {}
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    setText("");
    setIcon(null);
    setBorder(null);
    return this;
  }
}
