package org.xenia.registration.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SetupReader {
  private static Workbook wb;
  private static FormulaEvaluator evaluator;
  private static SetupReader instance = null;
  private static Map<String, Vector<Map<String, String>>> allMaps;

  private SetupReader() { }

  public static SetupReader getInstance(String fileName) {
    if (instance == null) load(fileName);
    return instance;
  }

  private static void load(String fileName) {
    instance = new SetupReader();
    allMaps = new HashMap<>();
    File setupFile = new File("config/" + fileName);
    try {
      wb = WorkbookFactory.create(new FileInputStream(setupFile));
      evaluator = wb.getCreationHelper().createFormulaEvaluator();
      for (int iSheet = 0; iSheet < wb.getNumberOfSheets(); iSheet++) {
        String sheetName = wb.getSheetName(iSheet);
        Vector<Map<String, String>> sheetMap = createMaps(sheetName);
        allMaps.put(sheetName, sheetMap);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Vector<Map<String, String>> getMaps(String key) { return allMaps.get(key); }

  @SuppressWarnings("unchecked")
  private static Vector<Map<String, String>> createMaps(String sheetName) throws Exception {
    Vector<Map<String, String>> maps = new Vector<>();
    Sheet sheet = wb.getSheet(sheetName);
    Row header = sheet.getRow(0);
    for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
      Row row = sheet.getRow(iRow);
      String tabKey = getString(row.getCell(0));
      Map<String, String> data = new HashMap<>();
      for (int iCell = 0; iCell <= row.getLastCellNum(); iCell++) {
        Cell cell = header.getCell(iCell);
        if (cell != null && header.getCell(iCell) != null) {
          String rowKey = getString(header.getCell(iCell));
          data.put(rowKey, (row.getCell(iCell) == null ? "" : getString(row.getCell(iCell))));
        }
      }
      maps.add(data);
    }
    return maps;
  }

  private static String getString(Cell cell) {
    return (cell.getCellType().equals(CellType.FORMULA)) ? evaluator.evaluateInCell(cell).getRichStringCellValue().getString() :
      cell.getCellType().equals(CellType.NUMERIC) ? String.valueOf((int)cell.getNumericCellValue()) :
        cell.toString();
  }

}
