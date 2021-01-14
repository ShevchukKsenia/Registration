package org.xenia.registration.gui;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseTableModel extends DefaultTableModel {
  private List<Integer> editableColumns;
  private boolean isFilter;
  private Map<Integer, Class> columnClass;

  public BaseTableModel() {
    super();
    editableColumns = new ArrayList<>();
    columnClass = new HashMap<>();
    isFilter = false;
  }

  public BaseTableModel(boolean filter) {
    super();
    editableColumns = new ArrayList<>();
    columnClass = new HashMap<>();
    isFilter = filter;
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return isFilter;
  }

  public boolean canEditCell(int rowIndex, int columnIndex) {
/*
    if (isFilter) {
      return this.getColumnClass(columnIndex) == String.class;
    } else {
      return editableColumns.contains(Integer.valueOf(columnIndex));
    }
*/
    return isFilter ? true : editableColumns.contains(Integer.valueOf(columnIndex));
  }

  @Override
  public Class getColumnClass(int col) {
    if (isFilter) {
      return String.class;
    } else {
      Integer column = Integer.valueOf(col);
      return columnClass != null && columnClass.keySet().contains(column) ? columnClass.get(column) : Object.class;
    }
  }

  public void setColumnClass(int col, Class cls) {
    columnClass.put(Integer.valueOf(col), cls);
  }

}
