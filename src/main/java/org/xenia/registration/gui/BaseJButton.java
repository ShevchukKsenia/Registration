package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

public class BaseJButton extends JButton {
  private Thread actionThread;
  private BaseTab baseTab;
  private String action;

  public BaseJButton(String text, String toolTip, String action, boolean inThread, BaseTab tab) {
    super(text);
    this.baseTab = tab;
    this.action = action;
    this.setToolTipText(toolTip);
    this.setActionCommand(action);
    this.addActionListener(e -> {
      if (inThread) {
        doClick();
      } else {
        doClick(action, tab);
      }
    });
  }

  private void doClick(String action, BaseTab tab) {
    try {
      if (tab != null) {
        Method m = tab.getClass().getMethod(action);
        m.invoke(tab);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void doClick() {
    BaseJButton button = this;
    actionThread = new Thread() {
      public void run() {
        try {
          if (baseTab != null) {
            button.setEnabled(false);
            Method m = baseTab.getClass().getMethod(action);
            m.invoke(baseTab);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        button.setEnabled(true);
      }
    };
    if (actionThread.getState().equals(Thread.State.NEW)) {
      actionThread.start();
    } else if (actionThread.getState().equals(Thread.State.TERMINATED)) {
      actionThread.run();
    } else {
      MessageDialog.showInfo(null, "Process is still running", "Warning");
    }
  };
}
