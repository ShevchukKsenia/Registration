package org.xenia.registration.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BaseComboBox<T> extends JComboBox<BaseComboBoxItem<T>> {
  private int[] indexes;
  public boolean keyIsEnter = false;
  protected List<BaseComboBoxItem<T>> srcList;
  private String currentPattern = "";

  public BaseComboBox(Vector<BaseComboBoxItem<T>> items) {
    super(items);
    setActionListener();
    setSpecialListeners();
    indexes = new int[items.size()];
    srcList = new ArrayList<BaseComboBoxItem<T>>();
    for (int i = 0; i < items.size(); i++) {
      indexes[i] = i;
      srcList.add(items.get(i));
    }
    initFilter();
  }

  // Possibility of filter for comboBox items
  private void initFilter() {
    removeKeyListener(getKeyListeners()[0]);
    addKeyListener(editListener);
    addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        super.focusLost(e);
// Clear filter pattern when focus lost
        currentPattern = "";
        reloadList(currentPattern);
      }
    });
    reloadList(currentPattern);
  }

  KeyListener editListener = new KeyListener() {
    private String savedPattern = currentPattern;
    // Edit filter pattern
    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !currentPattern.equals("")) {
        currentPattern = currentPattern.substring(0, currentPattern.length() - 1);
      } else if (Character.isDefined(e.getKeyChar()) && e.getKeyCode() != KeyEvent.VK_ESCAPE
        && e.getKeyCode() != KeyEvent.VK_DELETE && e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
        currentPattern += e.getKeyChar();
      }
      if (!currentPattern.equals(savedPattern)) {
        reloadList(currentPattern.toLowerCase().trim());
        savedPattern = currentPattern;
        hidePopup();
        showPopup();
        showTooltip();
      }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
  };

  // Show filter pattern as comboBox toolTip
  private void showTooltip() {
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

  void setActionListener() {
    addActionListener((e) -> {
// Setup comboBox toolTip when select item
        Object source = e.getSource();
        if (source == null || !(source instanceof BaseComboBox)) return;
        BaseComboBox<T> comboBox = (BaseComboBox<T>) source;
        Object selectedItem = comboBox.getSelectedItem();
        if (selectedItem == null) return;
        String toolTip = selectedItem.toString();
        if (toolTip == null || toolTip.trim().isEmpty()) return;
        int textWidth = getFontMetrics(getFont()).stringWidth(toolTip);
        int buttonWidth = (int) comboBox.getComponents()[0].getPreferredSize().getWidth() + 5 + 5; // 5 - left gap, 5 - right gap
        int comboBoxWidth = comboBox.getSize().width - buttonWidth;
        comboBox.setToolTipText(comboBoxWidth > 0 && textWidth > comboBoxWidth ? toolTip : null);
      }
    );
  }

  void setSpecialListeners() {
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        keyIsEnter = e.getKeyCode() == KeyEvent.VK_ENTER || !isPopupVisible();
      }
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        keyIsEnter = true;
      }
    });
  }

  // Apply filter to comboBox items
  private void reloadList(String pattern) {
    Object selectedItem = getSelectedItem();
    int modelSize = getModel().getSize();
    for (int iItem = modelSize - 1; iItem > -1; iItem--) {
      Object item = getItemAt(iItem);
      if (!item.equals(selectedItem) && item != null) {
        removeItemAt(iItem);
      }
    }
    boolean toAdd = false;
    for (Object item : srcList) {
      toAdd = toAdd || selectedItem == null || item.equals(selectedItem);
      String str = item.toString();
      if (pattern.length() < 1 || (str != null && str.toLowerCase().contains(pattern))) {
        if (!item.equals(selectedItem) && item != null) {
          if (toAdd) {
            ((DefaultComboBoxModel<T>)getModel()).addElement((T) item);
          } else {
            int iItem = getModel().getSize() - 1;
            ((DefaultComboBoxModel<T>)getModel()).insertElementAt((T) item, iItem);
          }
        }
      }
    }
    setToolTipText(currentPattern.length() > 0 ? currentPattern : null);
  }
}
