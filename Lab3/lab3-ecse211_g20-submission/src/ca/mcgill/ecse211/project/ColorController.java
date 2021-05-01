package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.lcd.LCD;
import java.util.ArrayList;

public class ColorController implements Runnable {

  /**
   * Record the total tick count started from the begining of the thread start Increment every
   * measurement to keep track of time elapse.
   */
  private int totalTick = 0;

  /**
   * The tick where the first black was detected.
   */
  private int blackTick = -1;

  /**
   * The number of measurement the black tick is associated with. Used to calculate the middle
   * point.
   */
  private int width = 0;

  /**
   * Record the ticks that black stripe occured.
   */
  ArrayList<Integer> blackTicks = new ArrayList<Integer>();

  /**
   * Constantly poll the sensor and sleep
   */
  public void run() {
    while (true) {
      readAndUpdate();
      Main.sleepFor(CS_POLL_SLEEP_TIME);
    }
  }

  /**
   * Read the color sensor and update the value
   */
  private void readAndUpdate() {
    int cid = colorSensor.getColorID();
    boolean black = isWithinInterval(cid, 13, 0.5);
    LCD.clear();
    if (black) {
      if (width == 0) {
        blackTick = totalTick;
      }
      width++;
    } else {
      if (width >= 1) {
        int tick = blackTick + width;
        blackTicks.add(tick);
        System.out.println("Tick: " + tick);
        System.out.println("Width: " + width);
        blackTick = -1;
        width = 0;
      }
    }
    totalTick++;
  }

  /**
   * Get the total tick of the color sensor since the last reset.
   * 
   * @return totaltick
   */
  public int getTotalTick() {
    return totalTick;
  }

  /**
   * Return the ticks that black occured
   * 
   * @return the tick
   */
  public ArrayList<Integer> getBlackTicks() {
    return blackTicks;
  }

  /**
   * The moveUntilLine method moves forward or backward until a line is detected.
   * 
   * @param forward : true to move forward, false to move backward
   * @param delay : delay in ms to wait until after the line is detected
   * @param pos : 0 if the axis to be crossed is the x-axis, 1 for y
   */
  public static void moveUntilLine(boolean forward) {
    if (forward) {
      leftMotor.forward();
      rightMotor.forward();
    } else {
      leftMotor.backward();
      rightMotor.backward();
    }

    // Let the motors go forward/backward until the sensor senses a black line.
    while (!isWithinInterval(colorSensor.getColorID(), 13, 0.5));
    // The delay allows the robot to move slightly past the black line before stopping,
    // to avoid accidentally reading the same line twice in a row.
    Main.sleepFor(DEFAULT_SLEEP_TIME);
    leftMotor.stop(true);
    rightMotor.stop();
  }

  /**
   * check if value is within the interval
   * 
   * @param value
   * @param center
   * @param offset
   * @return value within interval
   */
  private static boolean isWithinInterval(double value, double center, double offset) {
    return value >= center - offset && value <= center + offset;
  }
}
