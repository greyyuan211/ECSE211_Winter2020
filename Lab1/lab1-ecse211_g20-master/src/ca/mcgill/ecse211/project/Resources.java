package ca.mcgill.ecse211.project;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Class for static resources (things that stay the same throughout the entire program execution),
 * like constants and hardware.
 * <br><br>
 * Use these resources in other files by adding this line at the top (see examples):<br><br>
 * 
 * {@code import static ca.mcgill.ecse211.project.Resources.*;}
 */
public class Resources {
  //Parameters: adjust these for desired performance
  
  /**
   * Ideal distance between the sensor and the wall (cm).
   */
  public static final int WALL_DIST = 35;
  /**
   * Width of the maximum tolerated deviation from the ideal {@code WALL_DIST}, also known as the
   * dead band. This is measured in cm.
   */
  public static final int WALL_DIST_ERR_THRESH = 4;
  
  /**
   * Speed of the normal rotating wheel (deg/sec).
   */
  public static final int BANG_MOTOR_NORM = 160;
  
  /**
   * Right turn speed of slower rotating wheel (deg/sec).
   */
  public static final int BANG_MOTOR_LOW_RIGHT= 30; 
  
  /**
   * Right turn Speed of the faster rotating wheel (deg/sec).
   */
  public static final int BANG_MOTOR_HIGH_RIGHT= 220;
  
  /**
   * Left turn speed of slower rotating wheel (deg/sec).
   */
  public static final int BANG_MOTOR_LOW_LEFT = 90; 
  
  /**
   * Left turn speed of the faster rotating wheel (deg/sec).
   */
  public static final int BANG_MOTOR_HIGH_LEFT = 150;
  
  
  /**
   * P Speed of the nominal rotating wheel (deg/sec).
   */
  public static final int P_MOTOR_NORM = 170;
  
  /**
   * P Speed of the faster rotating wheel (deg/sec).
   */
  public static final int P_MOTOR_HIGH = 250;
  
  /**
   * Amount of speed decrease for every cm of delta distance (deg/m*sec).
   */
  public static final int P_TYPE_COEFFICIENT = 19;

  /**
   * Maximum amount of speed should be deducted when a extreme value is reached for a left turn (deg/sec).
   */
  public static final int MAX_LEFT_SPEED_DECREASE = 160;
  
  /**
   * Maximum amount of speed should be deducted when a extreme value is reached for a right turn (deg/sec).
   */
  public static final int MAX_RIGHT_SPEED_DECREASE = 170;

  
  /**
   * The limit of invalid samples that we read from the US sensor before assuming no obstacle.
   */
  public static final int INVALID_SAMPLE_LIMIT = 20;
  
  /**
   * The poll sleep time, in milliseconds.
   */
  public static final int POLL_SLEEP_TIME = 30;
  
  // Hardware resources
  /**
   * The LCD screen used for displaying text.
   */
  public static final TextLCD TEXT_LCD = LocalEV3.get().getTextLCD();
  
  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
  
  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);

}
