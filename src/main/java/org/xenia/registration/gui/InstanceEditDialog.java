package org.xenia.registration.gui;

import org.xenia.registration.App;
import org.xenia.registration.lang.BaseTimestamp;
import org.xenia.registration.utils.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.awt.Toolkit.getDefaultToolkit;

public class InstanceEditDialog extends JDialog {
  protected Vector<JLabel> labels;
  protected Vector<BaseJTextField> fields;
  protected static Object[] fieldValues, values;
  protected JTable table;
  protected BaseTab baseTab;
  protected int nField;
  protected int selectedRow;
  protected BaseJTextField firstFocusedField;
  protected JButton okButton;
  protected JButton cancelButton;
  protected JScrollPane scrollPane;
  protected JPanel mainPane, controlPane;
  protected Vector<Map<String, String>> formFields;
  protected boolean isNew;
  protected boolean onlyView;
  protected Border invisibleBorder, normalBorder, focusBorder;
  protected Map<Integer, BaseComboBox> possibleIds = new HashMap<>();
  protected Vector<Integer> fieldColumns, fieldRows;

  GridBagConstraints constraints;

  private Color normalBackground, pinkBackground = null;

  public InstanceEditDialog(BaseTab tab, boolean newInstance, boolean canEdit) {
    baseTab = tab;
    isNew = newInstance;
    onlyView = !canEdit && !isNew;
    table = baseTab.getTable();
    selectedRow = table.getSelectedRow();
    pinkBackground = Color.pink;
    try {
      formFields = baseTab.getColumns();
      nField = formFields.size();
      fieldValues = new Object[nField];
      if (formFields.size() < 1) {
        onlyView = true;
      }
      for (int iField = 0; iField < nField; iField++) {
        Map<String, String> formField = formFields.elementAt(iField);
        String key = formField.get("col");
        if (key.endsWith(".id")) {
          Integer selectedId = 0;
          Vector<BaseComboBoxItem> ids = App.mainWindow.getIds(key.split("\\.")[0]);
          if (!isNew) {
            selectedId = (Integer) table.getValueAt(table.getSelectedRow(), iField);
            try {
              BaseComboBoxItem selectedItem = ids.elementAt(selectedId);
            } catch (Exception e) {
              selectedId = null;
            }
            selectedId = selectedId == null ? 0 : selectedId;
          }
          BaseComboBox idsComboBox = new BaseComboBox(ids);
          idsComboBox.setRenderer(new BaseComboBoxRenderer());
          idsComboBox.setSelectedIndex(selectedId);
          idsComboBox.setEnabled(!onlyView && baseTab.editableColumns.contains(Integer.valueOf(iField)));
          possibleIds.put(Integer.valueOf(iField), idsComboBox);
        }
        if (isNew) {
          fieldValues[iField] = iField == tab.idColumn ? tab.getMaxId() + 1 :
            (key.endsWith("Time") ? new BaseTimestamp(System.currentTimeMillis(), App.configReader.getProperty("datetimeFormat", "yyyy-MM-dd hh:mm:ss")) :
              "");
        } else {
          fieldValues[iField] = table.getValueAt(table.getSelectedRow(), iField);
        }
      }
      firstFocusedField = null;
      createGuiComponents();
      addWindowFocusListener(new WindowFocusListener() {
        @Override
        public void windowGainedFocus(WindowEvent e) {
          if (firstFocusedField != null) {
            firstFocusedField.requestFocus();
          }
        }
        @Override
        public void windowLostFocus(WindowEvent e) {
        }
      });
      getContentPane().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);
          if (e.getClickCount() > 1) {
            firstFocusedField.requestFocus();
            pack();
          }
        }
      });
      addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          super.componentResized(e);
        }
      });
      String titleText = onlyView ? App.configReader.getProperty("formViewHeader", "Record View") :
        isNew ? App.configReader.getProperty("formAddHeader", "Record Create") :
          App.configReader.getProperty("formEditHeader", "Record Edit");
      setTitle(titleText);
    } catch (Exception e) {
    }
  }

  private void createGuiComponents() {
    final JPanel pane = new JPanel();
    createControlPanel();
    createJScrollPane();
    pane.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(1, 1, 1, 1);
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    pane.add(scrollPane, c);
    c.gridy = 1;
    c.weighty = 0;
    pane.add(controlPane, c);
    setContentPane(pane);
  }

  private void createControlPanel() {
    controlPane = new JPanel();
    String okButtonText = App.configReader.getProperty("saveButtonText", "Save");
    okButton = new JButton(okButtonText);
    String okButtonToolTip = isNew ? App.configReader.getProperty("saveNewToolTip", "Save Input Results") :
      App.configReader.getProperty("saveEditToolTip", "Save Edit Results");
    okButton.setToolTipText(okButtonToolTip);
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        values = new Object[nField];
        for (int i = 0; i < nField; i++) {
          if (possibleIds.containsKey(Integer.valueOf(i))) {
            fieldValues[i] = ((BaseComboBoxItem) possibleIds.get(Integer.valueOf(i)).getSelectedItem()).getIndex();
          }
          values[i] = fieldValues[i];
        }
        dispose();
      }
    });
    String cancelButtonText = App.configReader.getProperty("cancelButtonText", "Cancel");
    cancelButton = new JButton(cancelButtonText);
    String cancelButtonToolTip = onlyView ? App.configReader.getProperty("cancelViewToolTip", "Close Form") :
      isNew ? App.configReader.getProperty("cancelNewToolTip", "Don't Save Input Results") :
        App.configReader.getProperty("cancelEditToolTip", "Don't Save Edit Results");
    cancelButton.setToolTipText(cancelButtonToolTip);
    cancelButton.addActionListener(e -> {
      values = null;
      dispose();
    });
    controlPane.setLayout(new FlowLayout());
    if (!onlyView) {
      controlPane.add(okButton);
    }
    controlPane.add(cancelButton);
  }

  private void createJScrollPane() {
    mainPane = new JPanel();
    mainPane.setLayout(new GridBagLayout());
    scrollPane = new JScrollPane(mainPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    constraints = new GridBagConstraints();
    constraints.insets = new Insets(1, 1, 1, 1);
    constraints.gridy = -1;
    constraints.weighty = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.anchor = GridBagConstraints.WEST;
    labels = new Vector<>(nField);
    fields = new Vector<>(nField);

    for (int iField = 0; iField < nField; iField++) {
      final int finalIField = iField;
      Map<String, String> formField = formFields.elementAt(iField);
      String key = formField.get("col");
      Integer fieldColumn = Integer.valueOf(formField.get("column"));
      Integer fieldRow = Integer.valueOf(formField.get("row"));
      JLabel label = new JLabel(formField.get("name"));
      label.setToolTipText(formField.get("description"));
      constraints.weightx = 0;
      constraints.gridx = fieldColumn.intValue() * 2;
      constraints.gridy = fieldRow.intValue();
      mainPane.add(label, constraints);
      labels.add(label);

      Object fieldValue = fieldValues[iField];
      BaseJTextField field = new BaseJTextField(fieldValue != null ? fieldValue.toString() : "");
      invisibleBorder = iField < 1 ? BorderFactory.createLineBorder(label.getBackground()) : invisibleBorder;
      normalBorder = iField < 1 ? field.getBorder() : normalBorder;
      focusBorder = iField < 1 ? BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(table.getSelectionBackground()),
        BorderFactory.createEmptyBorder(0, 2, 0, 2)) : focusBorder;
      field.setBorder(normalBorder);
      label.setBorder(invisibleBorder);
      field.setEditable(!onlyView && !formField.get("col").equals("id") && baseTab.editableColumns.contains(Integer.valueOf(iField)));
      if (field.isEditable()) {
        firstFocusedField = firstFocusedField == null ? field : firstFocusedField;
        normalBackground = normalBackground == null ? field.getBackground() : normalBackground;
        Class cls = null;
        try {
          cls = Class.forName(key.endsWith("id") ? "java.lang.Integer" :
            (key.endsWith("Time") ? "org.xenia.registration.lang.BaseTimestamp" : "java.lang.String"));
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
        final Class finalCls = cls;
        final BaseJTextField finalField = field;
        field.getDocument().addDocumentListener(new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            changedUpdate(e);
          }
          @Override
          public void removeUpdate(DocumentEvent e) {
            changedUpdate(e);
          }
          @Override
          public void changedUpdate(DocumentEvent e) {
            Object value = Utils.getValue(field.getText(), finalCls);
            if ((value != null && field.getText().equals(value.toString())) || (field.getText().length() < 1)) {
              field.setBackground(field.isEditable() ? normalBackground : field.getBackground());
              fieldValues[finalIField] = value;
              okButton.setEnabled(true);
              field.showTooltip();
            } else {
              field.setBackground(pinkBackground);
              okButton.setEnabled(false);
            }
            Utils.setupToolTip(field);
          }
        });
      }
      field.addFocusListener(new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
          if (label.getBorder().equals(invisibleBorder)) {
            firstFocusedField = fields.elementAt(finalIField);
            label.setBorder(onlyView ? invisibleBorder : field.getBorder());
            field.setBorder(!onlyView && field.isEditable() ? focusBorder : normalBorder);
            field.requestFocus();
          }
        }
        @Override
        public void focusLost(FocusEvent e) {
          label.setBorder(invisibleBorder);
          field.setBorder(normalBorder);
        }
      });
      field.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
          super.keyTyped(e);
          boolean hasMask = (e.getModifiers() & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK;
          if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            cancelButton.doClick();
          } else if (e.getKeyChar() == KeyEvent.VK_ENTER && hasMask && !onlyView && okButton.isEnabled()) {
            okButton.doClick();
          }
        }
      });
      constraints.weightx = 1;
      constraints.gridx = fieldColumn.intValue() * 2 + 1;
      if (key.endsWith(".id")) {
        mainPane.add(possibleIds.get(Integer.valueOf(iField)), constraints);
      } else {
        mainPane.add(field, constraints);
      }
      fields.add(field);
    }
    scrollPane = new JScrollPane(mainPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        Insets insets = scrollPane.getBorder().getBorderInsets(mainPane);
        int addedWidth = mainPane.getWidth() + insets.left + insets.right - scrollPane.getWidth();
        for (BaseJTextField field : fields) {
          field.setSize(new Dimension(field.getWidth() - addedWidth, field.getHeight()));
          Utils.setupToolTip(field);
        }
        scrollPane.revalidate();
        scrollPane.repaint();
      }
    });
  }

  @SuppressWarnings("unchecked")
  public static Object[] showDialog(BaseTab tab, boolean newInstance, boolean canEdit) {
    InstanceEditDialog instanceEditDialog = new InstanceEditDialog(tab, newInstance, canEdit);
    instanceEditDialog.pack();
    final Toolkit toolkit = getDefaultToolkit();
    final Dimension screenSize = toolkit.getScreenSize();
    int x = (screenSize.width - instanceEditDialog.getWidth()) / 2;
    int y = (screenSize.height - instanceEditDialog.getHeight()) / 2;
    x = x < 0 ? 0 : x;
    y = y < 0 ? 0 : y;
    instanceEditDialog.setLocation(x, y);
    instanceEditDialog.setModal(true);
    instanceEditDialog.setVisible(true);
    return values;
  }

}
