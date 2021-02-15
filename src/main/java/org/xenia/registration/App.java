package org.xenia.registration;

import org.xenia.registration.gui.MainWindow;
import org.xenia.registration.utils.ConfigReader;
import org.xenia.registration.utils.SetupReader;

import java.awt.*;

public class App {
  public static ConfigReader configReader;
  public static SetupReader setupReader;
  public static MainWindow mainWindow;
  private static String configFileName = "Settings.xls";

  public static void main(String[] args) throws Exception {
    configReader = ConfigReader.getInstance(configFileName);
    setupReader = SetupReader.getInstance(configFileName);
    mainWindow = new MainWindow();
    mainWindow.setDefaultCloseOperation(mainWindow.EXIT_ON_CLOSE);
    mainWindow.pack();
    center(mainWindow);
    mainWindow.setColumnsWidth();
    mainWindow.setVisible(true);
  }

  public static void center(Component c) {
    Dimension screenSize = c.getToolkit().getScreenSize();
    Dimension componentSize = c.getSize();
    int xPos = (screenSize.width - componentSize.width) / 2;
    xPos = Math.max(xPos, 0);
    int yPos = (screenSize.height - componentSize.height) / 2;
    yPos = Math.max(yPos, 0);
    c.setLocation(new Point(xPos, yPos));
  }

}
