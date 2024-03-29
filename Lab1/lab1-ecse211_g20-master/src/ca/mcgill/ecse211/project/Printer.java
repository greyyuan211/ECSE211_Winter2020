package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

/**
 * The printer thread prints status information in the background. Since the thread sleeps for 
 * 200 ms each time through the loop, screen updating is limited to 5 Hz.
 */
public class Printer implements Runnable {
  
  /**
   * Printer sleep time in ms.
   */
  private static final int SLEEP_TIME = 200; 

  private UltrasonicController controller;

  public Printer() {
    controller = Main.selectedController;
  }

  public void run() {
    while (true) { // operates continuously
      TEXT_LCD.clear();
      TEXT_LCD.drawString("Controller Type is... ", 0, 0);
      TEXT_LCD.drawString(Main.controllerType, 0, 1);
      
      // print last US reading
      TEXT_LCD.drawString("US Distance: " + controller.readUsDistance(), 0, 2);
      // print the current left and right wheel's speed for infomational purposes
      TEXT_LCD.drawString(controller.reportCurrentSpeed(), 0, 3);
      Main.sleepFor(SLEEP_TIME);
    }
  }

  /**
   * Prints the main menu. Note that this is a static method.
   */
  public static void printMainMenu() {
    TEXT_LCD.clear();
    TEXT_LCD.drawString("left = bangbang", 0, 0);
    TEXT_LCD.drawString("right = p type", 0, 1);
  }
}
