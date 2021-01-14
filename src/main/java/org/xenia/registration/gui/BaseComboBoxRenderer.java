package org.xenia.registration.gui;

import org.xenia.registration.gui.BaseComboBoxItem;

import javax.swing.*;
import java.awt.*;

public class BaseComboBoxRenderer extends DefaultListCellRenderer {
  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    setText((value == null) ? "" : value.toString());
    if (value instanceof BaseComboBoxItem) {
      setToolTipText(((BaseComboBoxItem)value).getToolTip());
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      }
      else {
        setBackground(((BaseComboBoxItem)value).getStatus().intValue() == 0 ? list.getBackground() : Color.pink);
        setForeground(list.getForeground());
      }
    }
    return this;
  }
}
