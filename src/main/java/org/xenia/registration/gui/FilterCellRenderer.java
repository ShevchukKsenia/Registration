package org.xenia.registration.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class FilterCellRenderer extends DefaultTableCellRenderer {
  private int alignment = SwingConstants.LEFT;

  public FilterCellRenderer(int align) {
    super();
    alignment = align;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(alignment);
    c.setBackground(table.getBackground());
    c.setForeground(table.getForeground());
    if (table.getColumnModel().getColumn(column).getPreferredWidth() < 1) {
      c.setVisible(false);
    }
    return c;
  }
}
