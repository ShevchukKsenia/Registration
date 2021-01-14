package org.xenia.registration.gui;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xenia.registration.App;
import org.xenia.registration.utils.ConfigReader;
import org.xenia.registration.utils.SetupReader;
import org.xenia.registration.utils.Utils;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class BaseTab extends JPanel {
  protected JTable table;
  protected BaseTableModel tableModel;
  Vector<Map<String, String>> columns;
  protected int columnCount;
  protected String[] columnName, columnHead, columnToolTip;
  protected int[] columnWidth, columnAlignment;
  public static ConfigReader configReader;
  public static SetupReader setupReader;
  public MainWindow mainWindow;
  protected List<JButton> buttons;
  protected boolean filter;
  protected boolean simpleRowCount;

  private JTable filterTable;
  private BaseTableModel filterTableModel;
  private JButton applyFilterButton;
  private TableRowSorter<TableModel> sorter;

  private JDialog editFrame;
  private BaseJTextField editField;
  private int lastCol;
  private Color normalBackground;

  private JTable clickedTable;
  private JPanel menuPanel;
  private JScrollPane scrollPane, filterPane;
  private int clickedRow;
  private int clickedCol;
  private BaseTab clickedTab;
//  private BaseJButton clickedButton;

  BaseJPopupMenu popupMenu;

  protected String tabKey, baseName;

  private SwingConstants swingConstants;

  public int idColumn, nameColumn, statusColumn;

  protected static DateTimeFormatter formatter;
  private File excelFile = null;
  private XSSFWorkbook excelWB = null;
  private XSSFSheet excelSheet = null;

  protected Map<String, RowFilter.ComparisonType> matchMap;

  public BaseTab(String tabClass, boolean filter) {
    tabKey = tabClass;
    this.filter = filter;
    swingConstants = new SwingConstants() {
      @Override
      public int hashCode() {
        return super.hashCode();
      }
    };
    try {
      init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    createComponents();
  }

  protected void init() throws Exception {
    configReader = App.configReader;
    setupReader = App.setupReader;
    mainWindow = App.mainWindow;
    buttons = new ArrayList<>();
    initTable();
  }

  private void initTable() throws Exception {
    Map<String, Map<String, String>> items = App.setupReader.getMap("Column");
    Vector<String> colKeys = Utils.getSortedKeys(items, tabKey);
    columns = new Vector<>();
    columnCount = colKeys.size();
    lastCol = -1;
    idColumn = -1;
    nameColumn = -1;
    statusColumn = -1;
    columnName = new String[columnCount];
    columnHead = new String[columnCount];
    columnToolTip = new String[columnCount];
    columnWidth = new int[columnCount];
    columnAlignment = new int[columnCount];
    int iCol = 0;
    for (String colKey : colKeys) {
      Map<String, String> colData = items.get(colKey);
      columnName[iCol] = colKey.replace(tabKey + ".", "");
      columnHead[iCol] = colData.get("name");
      columnToolTip[iCol] = colData.get("description");
      columnWidth[iCol] = (int) Double.parseDouble(colData.get("width"));
      lastCol = columnWidth[iCol] > 0 ? iCol : lastCol;
      columnAlignment[iCol] = swingConstants.getClass().getField(colData.get("alignment").toUpperCase()).getInt(swingConstants);
      columns.add(colData);
      idColumn = columnName[iCol].equals("id") && idColumn < 0 ? iCol : idColumn;
      nameColumn = columnName[iCol].equals("name") && nameColumn < 0 ? iCol : nameColumn;
      statusColumn = columnName[iCol].equals("status.id") && statusColumn < 0 ? iCol : statusColumn;
      iCol++;
    }
  }

  public Vector<Map<String, String>> getColumns() {
    return columns;
  }

  protected void createComponents() {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 0;
    c.fill = GridBagConstraints.BOTH;
    createMenuPanel();
    add(menuPanel, c);
    c.gridx = 1;
    c.gridy = filter ? 2 : 1;
    c.weightx = 1;
    c.weighty = 1;
    createJScrollPane();
    add(scrollPane, c);
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 0;
    createFilterPane();
    if (filter) {
      add(filterPane, c);
      filterTable.setVisible(filter);
    }
  }

  private void createMenuPanel() {
    menuPanel = new JPanel();
    String buttonText = "Save page to MS Excel";
    String buttonToolTip = "Save table content to MS Excel";
    String voidName = "startExportToExcel";
    BaseJButton button = new BaseJButton(buttonText, buttonToolTip, voidName, false, this);
//    menuPanel.add(button);
  }

  protected void createJScrollPane() {
    createTable();
    table.setFillsViewportHeight(true);
    createPopup();
    table.setComponentPopupMenu(popupMenu);
    table.setAutoscrolls(false);
    scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    table.setAutoscrolls(true);
    table.setLayout(new FlowLayout(FlowLayout.TRAILING, -20, -25));
    table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      @Override
      public void columnAdded(TableColumnModelEvent e) {
      }

      @Override
      public void columnRemoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnMoved(TableColumnModelEvent e) {
      }

      @Override
      public void columnMarginChanged(ChangeEvent e) {
        if (filterTable != null) {
          for (int iCol = 0; iCol < columnCount; iCol++) {
            filterTable.getColumnModel().getColumn(iCol).setPreferredWidth(table.getColumnModel().getColumn(iCol).getPreferredWidth());
          }
        }
        table.requestFocus();
      }

      @Override
      public void columnSelectionChanged(ListSelectionEvent e) {
      }
    });

    URL imageUrl = getClass().getClassLoader().getResource("images/nosorted.gif");
    ImageIcon icon = imageUrl == null ? null : new ImageIcon(imageUrl);
    JButton noSortButton = new JButton("", icon);
    noSortButton.setPreferredSize(scrollPane.getVerticalScrollBar().getPreferredSize());
    noSortButton.setToolTipText("Default row sorting");
    noSortButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sorter.setSortKeys(null);
      }
    });
    noSortButton.setFocusable(false);
    noSortButton.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, noSortButton);
  }

  private void createFilterPane() {
    createFilterTable();
    filterPane = new JScrollPane(filterTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    filterPane.getHorizontalScrollBar().setPreferredSize(new Dimension(table.getPreferredSize().width, 7));
    filterPane.getHorizontalScrollBar().setBorder(null);
    filterTable.setLayout(new FlowLayout(FlowLayout.TRAILING, -20, -25));
    for (int i = filterPane.getVerticalScrollBar().getComponentCount() - 1; i >= 0; i--) {
      filterPane.getVerticalScrollBar().remove(i);
    }
    matchMap = new HashMap<>();
    matchMap.put("<", RowFilter.ComparisonType.BEFORE);
    matchMap.put(">", RowFilter.ComparisonType.AFTER);
    matchMap.put("=", RowFilter.ComparisonType.EQUAL);
    matchMap.put("!=", RowFilter.ComparisonType.NOT_EQUAL);
    URL imageUrl = getClass().getClassLoader().getResource("images/event.gif");
    ImageIcon icon = imageUrl == null ? null : new ImageIcon(imageUrl);
    applyFilterButton = new JButton("", icon);
    applyFilterButton.setPreferredSize(filterPane.getVerticalScrollBar().getPreferredSize());
    applyFilterButton.setToolTipText("Apply filter");
    applyFilterButton.setFocusable(false);
    applyFilterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean hasMask = (e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
        boolean filterEmpty = true;
        List<RowFilter<Object, Object>> filter = new ArrayList<>();
        for (int iCol = 0; iCol < filterTable.getColumnCount(); iCol++) {
          Object value = filterTable.getValueAt(0, iCol);
          if (value != null && value.toString().trim().length() > 0) {
            String text = value.toString().trim();
            try {
              if (table.getColumnClass(iCol).equals(String.class)) {
                text = (hasMask ? "" : "(?i)") + text;
                filter.add(RowFilter.regexFilter(Pattern.compile(text).toString(), iCol));
                filterEmpty = false;
              } else if (table.getColumnClass(iCol).equals(Integer.class)) {
                String comparisonKey = getComparisonKey(text);
                if (comparisonKey != null) {
                  RowFilter.ComparisonType comparisonType = matchMap.get(comparisonKey);
                  Integer intValue = Integer.valueOf(text.replace(comparisonKey, "").trim());
                  filter.add(RowFilter.numberFilter(comparisonType, intValue, iCol));
                  filterEmpty = false;
                }
              }
            } catch (PatternSyntaxException pse) {
            }
          }
        }
        if (filterEmpty) {
          sorter.setRowFilter(null);
        } else {
          sorter.setRowFilter(RowFilter.andFilter(filter));
        }
//       mainWindow.updateStatus();
      }
    });

    filterPane.getVerticalScrollBar().setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(0, 0, 0, 0);
    c.weightx = 1;
    c.weighty = 1;
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.BOTH;
    filterPane.getVerticalScrollBar().add(applyFilterButton, c);
  }

  private void createTable() {
    table = new JTable();
    tableModel = new BaseTableModel(false) {
      public Class getColumnClass(int column) {
        Class returnValue;
        if ((column >= 0) && (column < getColumnCount())) {
          returnValue = columnName[column].endsWith("id") ? Integer.class : String.class;
        } else {
          returnValue = Object.class;
        }
        return returnValue;
      }
    };
    for (int iCol = 0; iCol < columnCount; iCol++) {
      tableModel.addColumn(columnHead[iCol]);
      lastCol = columnWidth[iCol] > 0 ? iCol : lastCol;
    }
    table.setModel(tableModel);
    setColumnClasses();
    for (int iCol = 0; iCol < columnCount; iCol++) {
      table.getColumnModel().getColumn(iCol).setHeaderRenderer(
        (columnWidth[iCol] > 0) ? new BaseHeaderRenderer(columnAlignment[iCol], columnToolTip[iCol]) : new EmptyHeaderRenderer());
      table.getColumnModel().getColumn(iCol).setCellRenderer(new BaseCellRenderer(columnAlignment[iCol], columnName[iCol]));
    }
    table.getTableHeader().setReorderingAllowed(false);
    table.repaint();

    table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
    table.getActionMap().put("Enter", null);
    sorter = new TableRowSorter<TableModel>(tableModel);
    table.setRowSorter(sorter);

    Map<String, Map<String, String>> data = setupReader.getMap(tabKey);

    for (String key : data.keySet()) {
      Vector<Object> row = new Vector();
      row.add(Integer.valueOf(key));
      for (int iCol = 1; iCol < table.getColumnCount(); iCol++) {
        String str = data.get(key).get(columnName[iCol]);
        row.add(columnName[iCol].endsWith("id") ? Integer.valueOf(str) : str);
      }
      tableModel.addRow(row);
    }
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        boolean hasMask = (e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
        if (table.getSelectedRowCount() == 1 && e.getClickCount() > 1) {
          editRowAction(hasMask, false);
        }
      }
    });
    table.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        boolean hasMask = (e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
        int selectedRowsCount = table.getSelectedRowCount();
        if (selectedRowsCount == 1 && e.getKeyChar() == KeyEvent.VK_ENTER) {
          editRowAction(hasMask, false);
        }
      }
    });
  }

  public void createRow() {
    editRowAction(true, true);
  }

  public void viewRow() {
    editRowAction(false, false);
  }

  public void editRow() {
    editRowAction(true, false);
  }

  private void editRowAction(boolean hasMask, boolean isNew) {
    String yesButtonText = (String) UIManager.get("OptionPane.yesButtonText");
    UIManager.put("OptionPane.yesButtonText", "Save");
    Object[] fieldValues = InstanceEditDialog.showDialog(this, isNew, hasMask);
    if (fieldValues != null) {
      if (isNew) {
        Vector<Object> row = new Vector();
        for (int iCol = 0; iCol < table.getColumnCount(); iCol++) {
          row.add(fieldValues[iCol]);
        }
        tableModel.addRow(row);
      } else {
        for (int iCol = 0; iCol < table.getColumnCount(); iCol++) {
          tableModel.setValueAt(fieldValues[iCol], table.getSelectedRow(), iCol);
        }
      }
    }
    UIManager.put("OptionPane.yesButtonText", yesButtonText);
    table.revalidate();
    table.repaint();
  }

  public void deleteRows() {
    int[] rows = table.getSelectedRows();
    for (int i = rows.length - 1; i > -1; i--) {
      tableModel.removeRow(rows[i]);
    }
    tableModel.fireTableDataChanged();
    mainWindow.repaint();
  }

  private void createFilterTable() {
    filterTable = new BaseFilterTable();
    List<Integer> allColumns = new ArrayList<>();
    for (int iCol = 0; iCol < columnCount; iCol++) {
      allColumns.add(Integer.valueOf(iCol));
    }
    filterTableModel = new BaseTableModel(true);
    for (int iCol = 0; iCol < columnCount; iCol++) {
      filterTableModel.addColumn(columnHead[iCol]);
    }
    filterTable.setModel(filterTableModel);
    for (int iCol = 0; iCol < columnCount; iCol++) {
      filterTable.getColumnModel().getColumn(iCol).setHeaderRenderer(
        (columnWidth[iCol] > 0) ? new BaseHeaderRenderer(columnAlignment[iCol]) : new EmptyHeaderRenderer());
      filterTable.getColumnModel().getColumn(iCol).setCellRenderer(new FilterCellRenderer(columnAlignment[iCol]));
    }
    Vector<String> data = new Vector<>();
    for (int iCol = 0; iCol < filterTable.getColumnModel().getColumnCount(); iCol++) {
      data.add(null);
    }
    filterTableModel.addRow(data);
    filterTable.repaint();
//    filterTable.setFillsViewportHeight(true);
//    filterTable.setAutoscrolls(false);
    filterTable.setComponentPopupMenu(null);

    filterTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        clickedCol = filterTable.getSelectedColumn();
        Object value = filterTable.getCellEditor(0, clickedCol).getCellEditorValue();
        JTextField f = (JTextField) filterTable.getCellEditor(0, clickedCol).getTableCellEditorComponent(filterTable, value, true, 0, clickedCol);
        f.setBorder(BorderFactory.createLineBorder(table.getSelectionBackground()));
      }
    });
    filterTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        clickedCol = filterTable.getSelectedColumn();
      }
    });
    filterTable.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        boolean hasMask = (e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
          applyFilterButton.doClick();
          filterTable.changeSelection(0, clickedCol, false, false);
        } else if (e.getKeyChar() == KeyEvent.VK_TAB) {
          int newCol = filterTable.getSelectedColumn();
          while (newCol > -1 && newCol < table.getColumnCount() && table.getColumnModel().getColumn(newCol).getPreferredWidth() < 1) {
            newCol = newCol == 0 && hasMask ? table.getColumnCount() - 1 : newCol == table.getColumnCount() - 1 && !hasMask ? 0 : newCol + (hasMask ? -1 : 1);
          }
          filterTable.changeSelection(0, newCol, false, false);
        }
      }
    });
    filterTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        clickedCol = filterTable.getSelectedColumn();
      }
    });
  }

  public void setColumnsWidth() {
    int allWidth = 0;
    for (int iCol = 0; iCol < columnCount; iCol++) {
      allWidth = allWidth + columnWidth[iCol];
    }
    for (int iCol = 0; iCol < columnCount; iCol++) {
      int width = columnWidth[iCol];
      TableColumn column = table.getColumnModel().getColumn(iCol);
      column.setPreferredWidth(width * table.getWidth() / allWidth);
      if (width < 1) {
        column.setWidth(0);
        column.sizeWidthToFit();
        column.setResizable(false);
      }
    }
  }

  public void setFilterColumnsWidth() {
    for (int iCol = 0; iCol < columnCount; iCol++) {
      int width = columnWidth[iCol];
      TableColumn column = filterTable.getColumnModel().getColumn(iCol);
      column.setPreferredWidth(table.getColumnModel().getColumn(iCol).getPreferredWidth());
      if (width < 1) {
        column.setWidth(0);
        column.sizeWidthToFit();
        column.setResizable(false);
      }
    }
    filterTable.getTableHeader().setPreferredSize(new Dimension(filterTable.getPreferredSize().width, 0));
  }

  public JTable getTable() {
    return table;
  }

  protected void createPopup() {
    try {
      popupMenu = new BaseJPopupMenu();
      for (int i = 0; i < 4; i++) {
        String buttonText = i == 0 ? "Add (<Ins>)" : i == 1 ? "View (<Enter>)" :
          i == 2 ? "Edit (<Shift+Enter>)" : "Delete (<Del>)";
        int rowCount = i == 0 ? -1 : i == 1 ? 1 : i == 2 ? 1 : 0;
        boolean hasDialog = i >= 3;
        String voidName = i == 0 ? "createRow" : i == 1 ? "viewRow" : i == 2 ? "editRow" : "deleteRows";
        String dialogQuery = i < 3 ? "" : "Are you sure?";
        String dialogHeader = i < 3 ? "" : "Deleting of " + table.getSelectedRowCount() + " lines";
        BaseJMenuItem jMenuItem = new BaseJMenuItem(buttonText, rowCount, hasDialog, voidName, dialogQuery, dialogHeader, this);
        popupMenu.add(jMenuItem);
      }
    } catch (Exception e) {
    }
  }

  public int getMaxId() {
    int maxId = 0;
    for (int iRow = 0; iRow < tableModel.getRowCount() && idColumn > -1; iRow++) {
      int id = (int) tableModel.getValueAt(iRow, idColumn);
      if (id > maxId) maxId = id;
    }
    return maxId;
  }

  public int getIdCol(String name) {
    for (int iCol = 0; iCol < columnName.length; iCol++) {
      if (columnName[iCol].equals(name)) {
        return iCol;
      }
    }
    return -1;
  }

  public Vector<BaseComboBoxItem> getIds() {
    Vector<BaseComboBoxItem> ids = new Vector<>();
    ids.add(new BaseComboBoxItem("-", "not selected", 0, 0));
    if (idColumn > -1 && nameColumn > -1) {
      for (int iRow = 0; iRow < tableModel.getRowCount(); iRow++) {
        String value = (String) tableModel.getValueAt(iRow, nameColumn);
        Integer id = (Integer) tableModel.getValueAt(iRow, idColumn);
        Integer status = statusColumn > -1 ? (Integer) tableModel.getValueAt(iRow, statusColumn) : Integer.valueOf(0);
        if (id.intValue() == 0) {
          ids.removeAllElements();
        }
        BaseComboBoxItem item = new BaseComboBoxItem(value, value, id, status);
        ids.add(item);
      }
    }
    return ids;
  }

  public String getRowName(Integer id) {
    for (int iRow = 0; iRow < tableModel.getRowCount() && idColumn > -1 && nameColumn > -1; iRow++) {
      if (tableModel.getValueAt(iRow, idColumn).equals(id)) {
        return tableModel.getValueAt(iRow, nameColumn).toString();
      }
    }
    return null;
  }

  public int getStatusId(Integer id) {
    for (int iRow = 0; iRow < tableModel.getRowCount() && idColumn > -1 && statusColumn > -1; iRow++) {
      if (tableModel.getValueAt(iRow, idColumn).equals(id)) {
        return ((Integer)tableModel.getValueAt(iRow, statusColumn)).intValue();
      }
    }
    return 0;
  }

  protected void setColumnClasses() {
    for (int iCol = 0; iCol < tableModel.getColumnCount(); iCol++) {
      String columnClassStr = columnName[iCol].endsWith("id") ? "Integer" : "String";
      columnClassStr = "java.lang." + columnClassStr;
      try {
        Class columnClass = Class.forName(columnClassStr);
        tableModel.setColumnClass(iCol, Class.forName(columnClassStr));
      } catch (Exception e) {
      }
    }
  }

  public void startExportToExcel() {
    formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH-mm-ss");
    ZonedDateTime localDateTime = ZonedDateTime.now();
    String fileName = baseName + " " + formatter.format(localDateTime) + ".xlsx";
    String sheetName = baseName;
    boolean creationResult = false;
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File("config"));
    FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("XLSX", "xlsx");
    fileChooser.addChoosableFileFilter(fileFilter);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setSelectedFile(new File(fileName));
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      excelFile = fileChooser.getSelectedFile();
      excelWB = new XSSFWorkbook();
      excelSheet = excelWB.createSheet(sheetName);
      try {
        creationResult = excelFile.createNewFile();
      } catch (Exception e) {
        creationResult = false;
        excelFile = null;
        excelWB = null;
        excelSheet = null;
      }
    }
    if (creationResult) {
      unloadPageToExcel();
    }
  }

  protected void unloadPageToExcel() {
    int colCount = tableModel.getColumnCount();
    Row row = excelSheet.createRow(0);
    for (int iCol = 0; iCol < colCount; ++iCol) {
      Object colHeader = table.getColumnModel().getColumn(iCol).getHeaderValue();
      Cell cell = row.createCell(iCol);
      cell.setCellValue(colHeader != null ? colHeader.toString() : "");
    }
    for (int iRow = 0; iRow < tableModel.getRowCount(); ++iRow) {
      row = excelSheet.createRow(iRow + 1);
      for (int iCol = 0; iCol < colCount; ++iCol) {
        Cell cell = row.createCell(iCol);
        Object cellValue = tableModel.getValueAt(iRow, iCol);
        try {
          if (cellValue instanceof Integer && cellValue != null) {
            cell.setCellValue(((Integer) cellValue).doubleValue());
          } else {
            cell.setCellValue(cellValue == null ? "" : cellValue.toString());
          }
        } catch (Exception e) {
        }
      }
    }
    try {
      finishExportToExcel();
    } catch (Exception e) {
    }
  }

  protected void finishExportToExcel() throws Exception {
    excelSheet.createFreezePane(0, 1, 0, 1);
    for (int iCol = 0; iCol < table.getColumnModel().getColumnCount(); ++iCol) {
      excelSheet.autoSizeColumn(iCol);
    }
    excelWB.setSheetName(0, excelSheet.getSheetName() + " (" + excelSheet.getLastRowNum() + ")");
    FileOutputStream fileOut = new FileOutputStream(excelFile.getPath());
    excelWB.write(fileOut);
    fileOut.close();
  }

  protected String getComparisonKey(String str) {
    for (String key : matchMap.keySet()) {
      if (str.trim().startsWith(key)) {
        return key;
      }
    }
    try {
      Integer val = Integer.valueOf(str.trim());
      return "=";
    } catch(Exception e) {
      return null;
    }
  }
}
