package org.xenia.registration.gui;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class BaseHeaderRenderer extends DefaultTableCellRenderer implements UIResource {
  private boolean horizontalTextPositionSet;
  private static String [] sortIcons = new String [] {"Table.ascendingSortIcon", "Table.descendingSortIcon", "Table.naturalSortIcon"};
  private int alignment = SwingConstants.LEFT;
  private String toolTip = null;

  public BaseHeaderRenderer() {
    setHorizontalAlignment(JLabel.CENTER);
  }

  public BaseHeaderRenderer(int alignment) {
    setHorizontalAlignment(alignment);
  }

  public BaseHeaderRenderer(int alignment, String toolTip) {
    this.alignment = alignment;
    this.toolTip = toolTip;
    setHorizontalAlignment(alignment);
    setToolTipText(toolTip);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    Icon sortIcon = null;
    boolean isPaintingForPrint = false;
    if (table != null) {
      JTableHeader header = table.getTableHeader();
      if (header != null) {
        setForeground(hasFocus ? UIManager.getColor("TableHeader.focusCellForeground") : header.getForeground());
        setBackground(hasFocus ? UIManager.getColor("TableHeader.focusCellBackground") : header.getBackground());
        setFont(header.getFont());
        isPaintingForPrint = header.isPaintingForPrint();
      }
      if (!isPaintingForPrint && table.getRowSorter() != null) {
        if (!horizontalTextPositionSet) {
// There is a row sorter, and the developer hasn't
// set a text position, change to leading.
          setHorizontalTextPosition(JLabel.LEADING);
        }
        SortOrder sortOrder = getColumnSortOrder(table, column);
        sortIcon = sortOrder != null ? UIManager.getIcon(sortIcons[sortOrder.ordinal()]) : sortIcon;
      }
    }
    setText(value == null ? "" : value.toString());
    setIcon(sortIcon);
    setBorder(UIManager.getBorder(hasFocus ? "TableHeader.focusCellBorder" : "TableHeader.cellBorder"));
    if (table.getColumnModel().getColumn(column).getPreferredWidth() < 1) {
      setText("");
      setIcon(null);
      setBorder(null);
    }
    return this;
  }

  public static SortOrder getColumnSortOrder(JTable table, int column) {
    SortOrder rv = null;
    if (table.getRowSorter() == null) {
      return rv;
    }
    java.util.List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
    if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == table.convertColumnIndexToModel(column)) {
      rv = sortKeys.get(0).getSortOrder();
    }
    return rv;
  }
}
