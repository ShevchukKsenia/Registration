package org.xenia.registration.utils;

import org.xenia.registration.gui.BaseJTextField;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class Utils {

  public static Vector<String> getSortedKeys(Vector<Map<String, String>> maps) {
    Vector<String> vector = new Vector<>();
    Vector<Integer> nums = getSortedNums(maps);
    for (Integer num : nums) {
      vector.add(maps.get((int)num).get("key"));
    }
    return vector;
  }
  public static Vector<Integer> getSortedNums(Vector<Map<String, String>> maps) {
    Vector<Integer> vector = new Vector<>();
    if (maps != null && maps.size() > 0) {
      double[] numbers = new double[maps.size()];
      int iTmp = -1;
      for (Map<String, String> map : maps) {
        iTmp++;
        numbers[iTmp] = Double.valueOf(map.get("number")).doubleValue();
      }
      Arrays.sort(numbers);
      for (iTmp = 0; iTmp < numbers.length; iTmp++) {
        for (Map<String, String> tmpMap : maps) {
          if (Double.valueOf(tmpMap.get("number")).doubleValue() == numbers[iTmp] && !vector.contains(Integer.valueOf(iTmp))) {
            vector.add(Integer.valueOf(iTmp));
            break;
          }
        }
      }
    }
    return vector;
  }

  public static Vector<Integer> getSortedNums(Vector<Map<String, String>> maps, String tabKey) {
    Vector<Integer> tmpVector = getSortedNums(maps);
    Vector<Integer> vector = new Vector();
    for (Integer num : tmpVector) {
      if (maps.get(num).get("key").startsWith(tabKey + ".")) vector.add(num);
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
