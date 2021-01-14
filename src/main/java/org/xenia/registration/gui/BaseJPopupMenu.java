package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.*;

public class BaseJPopupMenu extends JPopupMenu {

  @Override
  protected void firePopupMenuWillBecomeVisible() {
    if (this != null) {
      for (int ii = 0; ii < getComponentCount(); ii++) {
        BaseJMenuItem item = ((BaseJMenuItem)getComponent(ii));
        int len = ((BaseJMenuItem)getComponent(ii)).getRowCount();
        int sel = ((JTable) getInvoker()).getSelectedRowCount();
// Visibility: < 0 - always; 0 - there are selected records; > 0 - exactly this number of records are selected
//        item.setVisible(item.isEnable() && (len < 0 ? true : (len == 0 ? sel > 0 : sel == len)));
        item.setVisible((len < 0 ? true : (len == 0 ? sel > 0 : sel == len)));
      }
    }
  }

  public BaseJMenuItem getMenuItemByActionName(String actionName) {
    for (Component menuItem : getComponents()) {
      if (menuItem instanceof BaseJMenuItem && ((BaseJMenuItem) menuItem).getActionName().equals(actionName)) {
        return (BaseJMenuItem) menuItem;
      }
    }
    return null;
  }

}
