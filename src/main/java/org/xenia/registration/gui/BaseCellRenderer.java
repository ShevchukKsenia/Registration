package org.xenia.registration.gui;

import org.xenia.registration.App;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class BaseCellRenderer extends DefaultTableCellRenderer {
  private int alignment = SwingConstants.LEFT;
  private String columnName = null;

  public BaseCellRenderer(int align) {
    super();
    alignment = align;
  }

  public BaseCellRenderer(int align, String columnName) {
    super();
    alignment = align;
    this.columnName = columnName;
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(alignment);

    String toolTip = null;
    if (columnName.endsWith(".id")) {
      toolTip = App.mainWindow.getRowName(columnName.replace(".id", ""), (Integer)value);
      int statusId = 0;
      if (columnName.equals("status.id")) {
        statusId = ((Integer)value).intValue();
      } else {
        statusId = App.mainWindow.getStatusId(columnName.replace(".id", ""), (Integer)value);
      }
      setBackground(isSelected ? table.getSelectionBackground() : statusId == 0 ? table.getBackground() : Color.pink);
    }
    if (getText() != null &&
      getFontMetrics(getFont()).stringWidth(getText()) > table.getColumnModel().getColumn(column).getWidth() &&
      toolTip == null) {
      setToolTipText(getText());
    } else setToolTipText(toolTip);

    if (table.getColumnModel().getColumn(column).getPreferredWidth() < 1) {
      c.setVisible(false);
    }
    return c;
  }

}
