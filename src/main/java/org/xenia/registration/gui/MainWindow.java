package org.xenia.registration.gui;

import org.apache.poi.ss.usermodel.*;
import org.xenia.registration.App;
import org.xenia.registration.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainWindow extends JFrame {
  private JTabbedPane tabbedPane;
  public Map<String, BaseTab> tabs;

  public MainWindow() throws Exception {
    super();
    initLookAndFeel();
    init();
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        dataSave();
        shutdown();
      }
    });
    Container contentPane = getContentPane();
    try {
      createTabbedPane();
      contentPane.add(tabbedPane, BorderLayout.CENTER);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void initLookAndFeel() {
    try {
      String lookAndGFeel = App.configReader.getProperty("LookAndFeel", "CrossPlatform");
      if (lookAndGFeel.equals("System")) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } else {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      }
    } catch (Exception e) {
    }
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource) {
        javax.swing.plaf.FontUIResource fontUIResource = (javax.swing.plaf.FontUIResource) value;
        UIManager.put(key, new javax.swing.plaf.FontUIResource(fontUIResource.getName()/*"Segoe UI"*/, fontUIResource.getStyle(), fontUIResource.getSize() + 1));
      }
    }
  }

  public void init() {
    UIManager.put("OptionPane.yesButtonText", App.configReader.getProperty("yesButtonText", "Yes"));
    UIManager.put("OptionPane.noButtonText", App.configReader.getProperty("noButtonText", "No"));
    UIManager.put("OptionPane.cancelButtonText", App.configReader.getProperty("cancelButtonText", "Cancel"));
    UIManager.put("FileChooser.approveButtonText", App.configReader.getProperty("approveButtonText", "Ready"));
    UIManager.put("FileChooser.cancelButtonText", App.configReader.getProperty("cancelButtonText", "Cancel"));
    setTitle(App.configReader.getProperty("mainHeader", "Framework for Simple Data"));
    double heightPercent = Double.parseDouble(App.configReader.getProperty("heightPercent", "50"));
    double widthPercent = Double.parseDouble(App.configReader.getProperty("widthPercent", "50"));
    Dimension dim = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
    dim.setSize(dim.getWidth() * widthPercent / 100, dim.getHeight() * heightPercent / 100);
    setPreferredSize(dim);
    setExtendedState(JFrame.NORMAL);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    tabs = new HashMap<>();
  }

  public void shutdown() {
    this.dispose();
    System.exit(0);
  }

  private void createTabbedPane() throws Exception{
    tabbedPane = new JTabbedPane();
    ClassLoader classLoader = getClass().getClassLoader();
    Vector<Map<String, String>> items = App.setupReader.getMaps("Tab");
    Vector<Integer> tabNums = Utils.getSortedNums(items);
    int iTab = 0;
    for (Integer tabNum : tabNums) {
      Map<String, String> tabData = items.get((int)tabNum);
      String tabKey = tabData.get("key");
      Boolean filter = Boolean.parseBoolean(tabData.get("filter"));
      Boolean visible = Boolean.parseBoolean(tabData.get("visible"));
      Boolean addable = Boolean.parseBoolean(tabData.get("addable"));
      Boolean deletable = Boolean.parseBoolean(tabData.get("deletable"));
      BaseTab tab = new BaseTab(tabKey, filter.booleanValue(), visible.booleanValue(), addable.booleanValue(), deletable.booleanValue());
      String tabName = tabData.get("name");
      tab.baseName = tabName;
      String tabDescription = tabData.get("description");
      URL imageUrl = classLoader.getResource(tabData.get("icon"));
      ImageIcon icon = (imageUrl == null) ? null : new ImageIcon(imageUrl);
      if (tab.isVisible) {
        tabbedPane.insertTab(tabName, icon, tab, tabDescription, iTab);
        iTab++;
      }
      tabs.put(tabKey, tab);
    }
  }

  public void setColumnsWidth() {
    for (int iTab = 0; iTab < tabbedPane.getComponentCount(); iTab++) {
      BaseTab tab = (BaseTab) tabbedPane.getComponent(iTab);
      if (tab != null) {
        tab.setColumnsWidth();
        tab.setFilterColumnsWidth();
      }
    }
  }

  public Vector<BaseComboBoxItem> getIds(String tabKey) {
    return tabs.get(tabKey).getIds();
  }

  public String getRowName(String tabKey, Integer id) {
    return tabs.get(tabKey).getRowName(id);
  }

  public int getStatusId(String tabKey, Integer id) {
    return tabs.get(tabKey).getStatusId(id);
  }

  public boolean dataSave() {
    String query = App.configReader.getProperty("saveDataQueryText", "Do you want to save data?");
    String header = App.configReader.getProperty("saveDataQueryHeader", "Data Saving");
    boolean toSave = MessageDialog.showConfirm(null, query, header);
    if (!toSave) {
      return true;
    }
    String fileName = App.configReader.getProperty("setupFile", "Settings.xls");
    File setupFile = new File("config/" + fileName);
    try {
      FileInputStream fileIn = new FileInputStream(setupFile);
      Workbook wb = WorkbookFactory.create(fileIn);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH-mm-ss");
      ZonedDateTime localDateTime = ZonedDateTime.now();
      File saveFile = new File("config/SAVE/" + fileName.replace(".xls", " ") + formatter.format(localDateTime) + ".xls");
      if (saveFile.createNewFile()) {
        FileOutputStream fileOut = new FileOutputStream(saveFile.getPath());
        wb.write(fileOut);
        fileOut.close();
        fileIn.close();
        Vector<String> tabKeys = Utils.getSortedKeys(App.setupReader.getMaps("Tab"));
        for (int iSheet = 0; iSheet < wb.getNumberOfSheets(); iSheet++) {
          Sheet sheet = wb.getSheetAt(iSheet);
          String sheetName = sheet.getSheetName();
          BaseTab tab = tabs.get(sheetName);
          if (tabKeys.contains(sheetName) && tab.isVisible) {
            CellStyle[] style = new CellStyle[sheet.getRow(0).getLastCellNum()];
            for (int iRow = sheet.getLastRowNum(); iRow > 0 ; iRow --) {
              Row row = sheet.getRow(iRow);
              if (iRow == sheet.getLastRowNum()) {
                for (int iCell = 0; iCell < row.getLastCellNum(); iCell++) {
                  style[iCell] = row.getCell(iCell).getCellStyle();
                }
              }
              sheet.removeRow(row);
            }
            for (int iRow = 0; iRow < tab.getTable().getModel().getRowCount(); iRow++) {
              Row row = sheet.createRow(iRow + 1);
              for (int iCol = 0; iCol < tab.getTable().getModel().getColumnCount(); ++iCol) {
                Cell cell = row.createCell(iCol);
                Object cellValue = tab.getTable().getModel().getValueAt(iRow, iCol);
                try {
                  if (cellValue instanceof Integer && cellValue != null) {
                    cell.setCellValue(((Integer) cellValue).doubleValue());
                  } else {
                    cell.setCellValue(cellValue == null ? "" : cellValue.toString());
                  }
                  cell.setCellStyle(style[iCol]);
                } catch (Exception e) {
                }
              }
            }
            for (int iCol = 0; iCol < tab.getTable().getColumnModel().getColumnCount(); ++iCol) {
              sheet.autoSizeColumn(iCol);
            }
          }
        }
        if (setupFile.delete()) {
          if (setupFile.createNewFile()) {
            fileOut = new FileOutputStream(setupFile.getPath());
            wb.write(fileOut);
            fileOut.close();
          }
        }
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

}

