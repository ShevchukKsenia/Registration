package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.*;

public class MessageDialog {
  public static void showError(Component parentComponent, String message, String title) {
    JOptionPane.showMessageDialog(parentComponent, message, title,
      JOptionPane.ERROR_MESSAGE);
  }

  public static void showWarn(Component parentComponent, String message, String title) {
    JOptionPane.showMessageDialog(parentComponent, message, title,
      JOptionPane.WARNING_MESSAGE);
  }

  public static boolean showConfirm(Component parentComponent, String message, String title) {
    return JOptionPane.showConfirmDialog(parentComponent, message, title,
      JOptionPane.YES_NO_OPTION
    ) == 0;
  }

  public static int showOption(Component parentComponent, String message, String title) {
    return JOptionPane.showConfirmDialog(parentComponent, message, title,
      JOptionPane.YES_NO_CANCEL_OPTION
    );
  }

  public static void showInfo(Component parentComponent, String message, String title) {
    JOptionPane.showMessageDialog(parentComponent, message, title,
      JOptionPane.INFORMATION_MESSAGE);
  }
}
