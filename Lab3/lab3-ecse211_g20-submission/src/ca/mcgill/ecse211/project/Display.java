package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

/**
 * This class is used to display the content of the odometer variables (x, y, theta).
 */
public class Display implements Runnable {

  private static final long DISPLAY_PERIOD = 50;

  /**
   * Constantly print out the current status from the us controller class.
   */
  public void run() {
    TEXT_LCD.clear();
    String cur = ultraSonicController.getCurStatus();
    TEXT_LCD.drawString(cur, 0, 1);
    Main.sleepFor(DISPLAY_PERIOD);
  }

  /**
   * Shows the text on the LCD, line by line.
   * 
   * @param strings comma-separated list of strings, one per line
   */
  public static void showText(String[] strings) {
    TEXT_LCD.clear();
    for (int i = 0; i < strings.length; i++) {
      TEXT_LCD.drawString(strings[i], 0, i);
    }
  }

}
