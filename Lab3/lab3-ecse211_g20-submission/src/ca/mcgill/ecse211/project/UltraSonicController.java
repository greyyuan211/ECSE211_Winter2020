package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.INVALID_SAMPLE_LIMIT;
import static ca.mcgill.ecse211.project.Resources.POLL_SLEEP_TIME;
import static ca.mcgill.ecse211.project.Resources.usSensor;

public class UltraSonicController implements Runnable {

  /**
   * Flag for stopping recording.
   */
  private boolean flag = true;
  /**
   * The distance remembered by the {@code filter()} method.
   */
  private int prevDistance;

  /**
   * The distance to be shown in the display.
   */
  private int dist = Integer.MAX_VALUE;

  /**
   * The number of invalid samples seen by {@code filter()} so far.
   */
  private int invalidSampleCount;

  /**
   * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
   * array each time {@code readUsSample()} is called.
   */
  private float[] usData = new float[usSensor.sampleSize()];

  /**
   * To store the current tic count to keep track of number poll made.
   */
  private int ticCount = 0;

  /**
   * Keep the ticCount when sensor see the newest lowest distance.
   */
  private int minTic = -1;


  /**
   * The lowest distance seem.
   */
  private int minDist = Integer.MAX_VALUE;

  /**
   * For calculating the central point.
   */
  private int width = 0;

  /**
   * reset the tick count and all measurement related parameter.
   */
  public void reset() {
    ticCount = 0;
    width = 0;
    minTic = -1;
    minDist = Integer.MAX_VALUE;
  }

  /**
   * return the current distance
   * 
   * @return current status of the us sensor
   */
  public String getCurStatus() {
    return "Current Distance" + dist;
  }

  /**
   * Returns number of index away from the minimum value
   * 
   * @return number of index away from the minimum value
   */
  public int getAvgMinDistIndex() {
    return minTic + width / 2;
  }

  /**
   * Returns number of index away from the minimum value
   * 
   * @return number of index away from the minimum value
   */
  public int getMinDistIndex() {
    return minTic;
  }

  /**
   * return the total tick/measurement made so far.
   * 
   * @return total tick at the current monent
   */
  public int getTotalTick() {
    return ticCount;
  }

  /**
   * return the average distance for a given detection.
   * 
   * @return average distance for given detection
   */
  public double getCurDist() {
    return dist;
  }

  /*
   * Samples the US sensor and invokes the selected controller on each cycle (non Javadoc).
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (flag) {
      readUsDistance();
      Main.sleepFor(POLL_SLEEP_TIME);
    }
  }

  /**
   * Returns the filtered distance between the US sensor and an obstacle in cm.
   * 
   * @return the filtered distance between the US sensor and an obstacle in cm
   */
  public int readUsDistance() {
    usSensor.fetchSample(usData, 0);
    // extract from buffer, convert to cm, cast to int, and filter
    int curDist = filter((int) (usData[0] * 100.0));
    if (curDist < minDist) {
      minDist = curDist;
      minTic = ticCount;
      width = 0;
    } else if (curDist == minDist) {
      width++;
    }
    ticCount++;
    this.dist = curDist;
    Display.showText(new String[] {"Distance: " + dist});
    return curDist;
  }

  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * 
   * @param distance raw distance measured by the sensor in cm
   * @return the filtered distance in cm
   */
  int filter(int distance) {
    if (distance >= 255 && invalidSampleCount < INVALID_SAMPLE_LIMIT) {
      // bad value, increment the filter value and return the distance remembered from before
      invalidSampleCount++;
      return prevDistance;
    } else {
      if (distance < 255) {
        // distance went below 255: reset filter and remember the input distance.
        invalidSampleCount = 0;
      }
      prevDistance = distance;
      return distance;
    }
  }
}
