package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class BaseJTextField extends JTextField {
  public BaseJTextField(String s) {
    super(s);
  }

  public void showTooltip() {
    final ToolTipManager ttm = ToolTipManager.sharedInstance();
    final int oldDelay = ttm.getInitialDelay();
    ttm.setInitialDelay(0);
    ttm.mouseMoved(new MouseEvent(this, 0, 0, 0, 0, 0, 0, false));
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        ttm.setInitialDelay(oldDelay);
      }
    });
  }
}
