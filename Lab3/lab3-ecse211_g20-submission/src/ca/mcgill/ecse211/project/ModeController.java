package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.util.ArrayList;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class ModeController implements Runnable {

  /**
   * The US controller thread to poll and provide infomation.
   */
  private Thread ultrasonicThread;

  /**
   * The CS controller thread to poll and provide infomation.
   */
  private Thread colorThread;


  /**
   * Option for selecting mode.
   */
  private int option = -1;

  /**
   * Constructor for the class. Set the option set by the user.
   * 
   * @param option for setup
   */
  public ModeController(int option) {
    this.option = option;
  }

  /**
   * Run the corresponding mode based on the option.
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    if (option == 1) {
      moveModeA();
    } else if (option == 2) {
      moveModeB();
    } else if (option == 3) {
      moveModeC();
    }
    LCD.clear();
    Display.showText(new String[] {"Program terminated."});
  }

  /**
   * The movement mode A. Uses ultrasonic detection twice and find the min and travel distance based
   * on sensor data.
   */
  public void moveModeA() {
    // Start the us controller
    ultraSonicController = new UltraSonicController();
    ultrasonicThread = new Thread(ultraSonicController);
    ultrasonicThread.start();
    // perform the following operation 2 time. Face the closest wall and backup until a suitable
    // distance based on the us sensor
    for (int i = 0; i < 2; i++) {
      faceTheClosestWall();
      backup();
    }
    // The robot now face one of the two wall. If it is the right most wall. Perform a 90 degree
    // right turn. Else perform a 180 degree turn (2*90 degree). We first assume case 1.
    MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    // Case 2
    if (ultraSonicController.getCurDist() < DISTANCE_THRESHOLD) {
      MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    }
    // Stop the us sensor thread
    ultrasonicThread.interrupt();
  }

  /**
   * The movement mode B. Uses ultra-sonic detection once and find the min and travel distance based
   * on the ultra-sonic sensor. Then turn 90 degree and check if you are facing another wall. If you
   * are, then perform distance measurement and move accordingly. Otherwise, do a 180 turn and
   * perform move.
   */
  public void moveModeB() {
    // Start the us controller
    ultraSonicController = new UltraSonicController();
    ultrasonicThread = new Thread(ultraSonicController);
    ultrasonicThread.start();
    // Face the closest wall and backup until a suitable
    // distance based on the us sensor
    faceTheClosestWall();
    localizeAngle();
    Button.waitForAnyPress();
    // The robot now face one of the two wall. If it is the left most wall. Perform a 90 degree
    // right turn. Else perform a 90 degree left turn turn. We first assume case 1 then validate
    // using the us sensor
    MoveController.turnBy(U_TURN_ROTATION);
    minorAngleCorrection();
    backup();
    MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    minorAngleCorrection();
    backup();
    MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    ultrasonicThread.interrupt();
  }

  /**
   * Perform a localization of the angle based on the us sensor.
   */
  private void localizeAngle() {
    MoveController.setSpeed(ROTATE_SPEED);
    MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    if (ultraSonicController.getCurDist() < DISTANCE_THRESHOLD) {
      // Case 1
      MoveController.turnBy(RIGHT_ANGLE_ROTATION);
    }
  }

  /**
   * The movement mode C. Localize using the light sensor
   */
  public void moveModeC() {
    // Start the us controller
    
    colorController = new ColorController();
    colorThread = new Thread(colorController);
    MoveController.setSpeed(100);
    colorThread.start();
    MoveController.turnBy(FULL_ROTATION);
    colorThread.interrupt();
    int totalTick = colorController.getTotalTick();
    ArrayList<Integer> ticks = colorController.getBlackTicks();
    System.out.println("Total Tick " + totalTick);
    
    if (ticks.size() != 4) {
      System.out.println("Tick size:" + ticks.size());
      for (int val : ticks) {
        System.out.println(val);
      }
      return;
    }
    colorLocalize(ticks, totalTick);
    for (int val : ticks) {
      System.out.println(val);
    }
  }

  /**
   * Localized based on tick received from the color sensor.
   * 
   * @param an array list of tick where black line was detected.
   * @param total amount of tick made throughout the 360 degree turn.
   */
  private void colorLocalize(ArrayList<Integer> ticks, int totalTick) {
    //Perform x correction
    double angle = CS_ANGLE_OFFSET+(ticks.get(0) * FULL_ROTATION) / totalTick;
    System.out.println("Angle: " + angle);
    double deltaTheta = getDTheta(ticks.get(0), ticks.get(2), totalTick);
    System.out.println("Dtheta: " + deltaTheta);
    double angletoTurn = angle + deltaTheta;
    MoveController.turnBy(angle);
    MoveController.turnBy(deltaTheta);
    double offset = COLOR_SENSOR_CENTER_OFFSET * Math.cos(Math.toRadians(deltaTheta));
    MoveController.moveStraightFor(-offset);
    // Perform the y correction
    angle = CS_ANGLE_OFFSET+(ticks.get(1) * FULL_ROTATION) / totalTick;
    deltaTheta = getDTheta(ticks.get(1), ticks.get(3), totalTick);
    angletoTurn = angle - angletoTurn;
    MoveController.turnBy(angletoTurn);
    MoveController.turnBy(deltaTheta);
    offset = COLOR_SENSOR_CENTER_OFFSET * Math.cos(Math.toRadians(deltaTheta));
    MoveController.moveStraightFor(-offset);
  }

  /**
   * Perform the computation and return the delta angle. uses angle between the two tick from color
   * sensor
   * 
   * @param first tick that black line was detected
   * @param opposite tick that black line was detected
   * @param total amount of tick made
   * @return the delta angle from the first line position to facing the line
   */
  private double getDTheta(int tick1, int tick2, int totalTick) {

    double dtheta = (tick2 - tick1) * 360.0 / totalTick;
    if (dtheta > 180.0) {
      dtheta = 360.0 - dtheta;
    }
    dtheta /= 2.0;
    return dtheta;
  }

  /**
   * Helper method to face towards the closest wall. Perform a 360 sweep to turn to the aggregate
   * angle and a slow 60 degree sweep to turn get a accurate turn.
   */
  private void faceTheClosestWall() {
    // perform 360 turn and locate the approximate angle and use minor correction to determine the
    // exact angle.
    ultraSonicController.reset();
    MoveController.setSpeed(ROTATE_SPEED);
    MoveController.turnBy(FULL_ROTATION);
    int totalTic = ultraSonicController.getTotalTick();
    int minTic = ultraSonicController.getMinDistIndex();
    double angleToTurn = 0;
    // locate the angle away from the start position.
    angleToTurn = ((double) minTic) * FULL_ROTATION / totalTic;
    if (angleToTurn > 180.0) {
      angleToTurn = angleToTurn - 360.0;
    } // perform the turn
    MoveController.turnBy(angleToTurn);
    minorAngleCorrection();
  }

  /**
   * Assume start at an angle close to the reall value. Perform us localization and face toward the
   * closest wall.
   */
  private void minorAngleCorrection() {
    // perform small turn and locate the average Min distance angle.
    MoveController.turnBy(-1 * HALF_SMALL_ROTATION);
    MoveController.setSpeed(SMALL_ROTATE_SPEED);
    ultraSonicController.reset();
    MoveController.turnBy(SMALL_ROTATION);
    int totalTic = ultraSonicController.getTotalTick();
    int minTic = ultraSonicController.getAvgMinDistIndex();
    double angleToTurn = 0;
    angleToTurn = (double) (minTic - totalTic) * SMALL_ROTATION / (totalTic);
    MoveController.turnBy(angleToTurn);
  }

  /**
   * Assume the robot faces a wall. backup until a distance of a tile distance.
   */
  private void backup() {
    ultraSonicController.reset();
    Main.sleepFor(DEFAULT_SLEEP_TIME);
    double curDist = ultraSonicController.getCurDist();
    System.out.println(curDist);
    MoveController.setSpeed(ROTATE_SPEED);
    MoveController.moveStraightFor(curDist + US_SENSOR_CENTER_OFFSET - TILE_SIZE);
  }
}
