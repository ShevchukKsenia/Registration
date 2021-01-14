package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

public class BaseJMenuItem extends JMenuItem {
  private int rowCount;
  private String actionName;

  public BaseJMenuItem(String text, int rows, boolean dialog, String action, String query, String header, BaseTab tab) {
    super(text);
    rowCount = rows;
    actionName = action;
    this.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean fireAction = true;
        if (dialog) {
          Component c = (Component) e.getSource();
          fireAction = fireAction && MessageDialog.showConfirm(c, query, header);
        }
        if (fireAction) {
          try {
            if (tab != null) {
              Method m = tab.getClass().getMethod(action);
              m.invoke(tab);
            }
          } catch (Exception ex) {
          }
        }
      }
    });
  }

  public int getRowCount() {
    return rowCount;
  }

  public String getActionName() { return actionName; }
/*
  public boolean isEnable() {
    return MainWindow.getCanEdit();
  }
*/
}
