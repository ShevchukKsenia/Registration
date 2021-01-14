package org.xenia.registration.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class BaseFilterTable extends JTable {
  public BaseFilterTable() {
    super();
  }
  @Override
  public Component prepareEditor(TableCellEditor editor, int row, int column) {
    final Component editorComponent = super.prepareEditor(editor, row, column);
    if (editorComponent instanceof JComponent) {
      ((JComponent)editorComponent).setBorder(new LineBorder(getSelectionBackground()));
    }
    return editorComponent;
  }
}
