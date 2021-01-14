package org.xenia.registration.utils;

import org.xenia.registration.App;
import org.xenia.registration.gui.BaseJTextField;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class Utils {

  public static Vector<String> getSortedKeys(Map<String, Map<String, String>> map) {
    Vector<String> vector = new Vector<>();
    if (map != null && map.size() > 0) {
      double[] numbers = new double[map.size()];
      int iTmp = -1;
      for (String key : map.keySet()) {
        iTmp++;
        numbers[iTmp] = Double.valueOf(map.get(key).get("number")).doubleValue();
      }
      Arrays.sort(numbers);
      for (iTmp = 0; iTmp < numbers.length; iTmp++) {
        for (String key : map.keySet()) {
          if (Double.valueOf(map.get(key).get("number")).doubleValue() == numbers[iTmp] && !vector.contains(key)) {
            vector.add(key);
            break;
          }
        }
      }
    }
    return vector;
  }

  public static Vector<String> getSortedKeys(Map<String, Map<String, String>> map, String tabKey) {
    Vector<String> tmpVector = getSortedKeys(map);
    Vector<String> vector = new Vector();
    for (String key : tmpVector) {
      if (key.startsWith(tabKey + ".")) vector.add(key);
    }
    return vector;
  }

  public static Object getValue(String s, Class c) {
    Object result = null;
    try {
      if (c == null || s.getClass().equals(c)) {
        result = s;
      } else {
        Method m = c.getMethod("valueOf", String.class);
        result = m.invoke(null, s);
      }
    } catch (Exception e) {
      try {
        Class[] classes = {String.class};
        result = c.getConstructor(classes).newInstance(s);
      } catch (Exception ee) {
      }
    }
    return result;
  }

  public static void setupToolTip(BaseJTextField field) {
    if (field.getText() != null && field.getFontMetrics(field.getFont()).stringWidth(field.getText()) > field.getSize().getWidth()) {
      field.setToolTipText(field.getText());
    } else field.setToolTipText(null);
  }

}
