package org.xenia.registration.gui;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import org.joda.time.format.DateTimeFormatter;
import org.xenia.registration.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created by Ilya Zakharov on 10/5/2015
 */
public class CalendarField extends JPanel {
  private JTextFieldDateEditor editor;
  private JDateChooser dateChooser;
  private List<Consumer<KeyEvent>> listeners = new ArrayList<>();

  private final Long initialTimestamp;

  public CalendarField(Long timestamp, DateTimeFormatter dateTimeFormatter) {
    super();

    this.initialTimestamp = timestamp;

    createGuiComponents(App.configReader.getProperty("datetimeFormat", "yyyy-MM-dd HH:mm:ss"));
  }

  public CalendarField(Long timestamp, String datetimeFormat) {
    super();

    this.initialTimestamp = timestamp;

    createGuiComponents(datetimeFormat);
  }

  private void createGuiComponents(String datetimeFormat) {
    setLayout(new BorderLayout());

    editor = new JTextFieldDateEditor();
    editor.setDateFormatString(datetimeFormat);

    dateChooser = new JDateChooser(editor) {
      @Override
      public void setDate(Date date) {
        Date d = date == null ? date : new Date(date.getTime() / (60 * 60 * 1000) * (60 * 60 * 1000));
        editor.setDate(d);
//                editor.setDate(date);
        if (getParent() != null) {
          getParent().invalidate();
        }
      }
    };
    dateChooser.setLocale(new Locale(App.configReader.getProperty("guiLanguage", "en"),
      App.configReader.getProperty("guiCountry", "US")));
    dateChooser.setDateFormatString(datetimeFormat);
    dateChooser.setDate(initialTimestamp == null ? new Date() : new Date(initialTimestamp));

    add(dateChooser);

    dateChooser.validate();
  }

  public Long getTimestamp() {
    return (dateChooser == null || dateChooser.getDate() == null) ? initialTimestamp :
      dateChooser.getDate().getTime();
  }

  public void setTimestamp(Long timestamp) {
    if (timestamp == null) {
      dateChooser.setDate(new Date());
    } else {
      dateChooser.setDate(new Date(timestamp));
    }
  }

  public void addKeyListener(Consumer<KeyEvent> listener) {
    this.listeners.add(listener);
    editor.addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        listener.accept(e);
      }
    });
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    dateChooser.setEnabled(enabled);
    editor.setEnabled(enabled);
    for (Consumer<KeyEvent> listener : listeners) {
      listener.accept(null);
    }

  }
}