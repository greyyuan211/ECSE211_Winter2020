package ca.mcgill.ecse211.project;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>
 * {@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * The us sensor controller class used as thread.
   */
  public static UltraSonicController ultraSonicController;

  /**
   * The Color Sensor controller class used as thread.
   */
  public static ColorController colorController;

  /**
   * The display class used to show data.
   */
  public static Display display;

  /**
   * The mode controller used for choosing mode.
   */
  public static ModeController modeController;

  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.130;// 2.130

  /**
   * The robot width in centimeters.
   */
  public static final double BASE_WIDTH = 15.900;// 15.8

  /**
   * The offset between ultra sonic sensor and the center of the robot width in centimeters.
   */
  public static final double US_SENSOR_CENTER_OFFSET = 6.0;

  
  /**
   * The offset between color sensor and the center of the robot width in centimeters.
   */
  public static final double COLOR_SENSOR_CENTER_OFFSET = 14.0;

  /**
   * The offset of the angle of the color angle
   */
  public static final double CS_ANGLE_OFFSET = 5.0;
  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 150;

  /**
   *  The speed at which the robot rotates in degrees per second.
   */
  public static final int CS_ROTATE_SPEED = 100;
  
  /**
   * The speed at which the robot slowly rotates in degrees per second.
   */
  public static final int SMALL_ROTATE_SPEED = 40;

  /**
   * Angle to do a full rotation.
   */
  public static final double FULL_ROTATION = 360.0;

  /**
   * Right angle rotation for secondary rotation.
   */
  public static final double U_TURN_ROTATION = 180.0;

  /**
   * Right angle rotation for secondary rotation.
   */
  public static final double RIGHT_ANGLE_ROTATION = 90.0;

  /**
   * Small rotation for secondary rotation.
   */
  public static final double SMALL_ROTATION = 60.0;

  /**
   * Half small rotation for secondary rotation.
   */
  public static final double HALF_SMALL_ROTATION = 30.0;

  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double TILE_SIZE = 30.00;// 30.48

  /**
   * The distance threshold for identifying if the sensor is facing a wall.
   */
  public static final double DISTANCE_THRESHOLD = 80.0;

  /**
   * The poll sleep time, in milliseconds.
   */
  public static final int POLL_SLEEP_TIME = 30;

  /**
   * The color sensor poll sleep time, in milliseconds.
   */
  public static final int CS_POLL_SLEEP_TIME = 30;

  /**
   * The default sleep time to reset, in milliseconds.
   */
  public static final int DEFAULT_SLEEP_TIME = 1000;

  /**
   * The limit of invalid samples that we read from the US sensor before assuming no obstacle.
   */
  public static final int INVALID_SAMPLE_LIMIT = 20;

  // Hardware resources

  /**
   * The color sensor hardware class.
   */
  public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
  /**
   * The LCD screen used for displaying text.
   */
  public static final TextLCD TEXT_LCD = LocalEV3.get().getTextLCD();

  /**
   * The ultrasonic sensor hardware class.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S2);

  /**
   * The left motor hardware class.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);

  /**
   * The right motor hardware class.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
}
