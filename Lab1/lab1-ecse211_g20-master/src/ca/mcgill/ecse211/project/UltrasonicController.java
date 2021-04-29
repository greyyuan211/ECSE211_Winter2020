package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import org.opencv.core.MatOfRect;

/**
 * Controls the robot's movements based on ultrasonic data.
 * <br><br>
 * Control of the wall follower is applied periodically by the UltrasonicController thread in the 
 * while loop in {@code run()}. Assuming that {@code usSensor.fetchSample()} and {@code 
 * processUsData()} take ~20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
public class UltrasonicController implements Runnable {


  /**
   * The distance remembered by the {@code filter()} method.
   */
  private int prevDistance;
  
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
   * The controller type.
   */
  private String type;
  /**
   * the record for current left wheel speed for printing on the screen
   */
  private int currentLeftSpeed =0;
  /**
   * the record for current right wheel speed for printing on the screen
   */
  private int currentRightSpeed =0;

  
  /**
   * Constructor for an abstract UltrasonicController. It makes the robot move forward.
   */
  public UltrasonicController(String type) {
    this.type = type;
    leftMotor.setSpeed(BANG_MOTOR_NORM);
    rightMotor.setSpeed(BANG_MOTOR_NORM);
    leftMotor.forward();
    rightMotor.forward();
  }

  /**
   * Process a movement based on the US distance passed in (BANG-BANG style).
   * 
   * @param distance the distance in cm
   */
  public void bangBangController(int distance) {
	  if(Math.abs(distance-WALL_DIST)>WALL_DIST_ERR_THRESH) {
          // Determine to perform a left or a right turn
		  if(distance>WALL_DIST) {
		    // Perform a left turn by setting the left wheel to low and right wheel to high
		    setMotorSpeed(BANG_MOTOR_LOW_LEFT,BANG_MOTOR_HIGH_LEFT);
		  }
		  else {
	        // Perform a right turn by setting the right wheel to low and left wheel to high
			setMotorSpeed(BANG_MOTOR_HIGH_RIGHT,BANG_MOTOR_LOW_RIGHT);
		  }   
	  }
	  else {
	      // Set the speed to nominal speed when the ideal distance is reached
	      setMotorSpeed(BANG_MOTOR_NORM, BANG_MOTOR_NORM);
	  }
	  // Make the robot go forward with the defined speed
	  forward();
  }

  /**
   * Process a movement based on the US distance passed in (P style)
   * 
   * @param distance the distance in cm
   */
  public void pTypeController(int distance) {
      // Calculated the delta between the idea distance and the current distance
	  int distanceDelta = Math.abs(distance-WALL_DIST);
	  //Perform the turn when the delta is large enough
	  if(distanceDelta>WALL_DIST_ERR_THRESH) {
	      //Determine to perform a left or a right turn
		  if(distance>WALL_DIST) {
		    // Perform a left turn by decrease the left wheel speed and keep right wheel to normial
		    //calculate the speed difference in a linear fashion to the delta
		    int speedDecrease= Math.min(MAX_LEFT_SPEED_DECREASE,distanceDelta*P_TYPE_COEFFICIENT);
		    //norm is used instead of high for right wheel because left turn tend to have lower radius of curvature
		    setMotorSpeed(P_MOTOR_HIGH-speedDecrease,P_MOTOR_NORM);    
		  }
		  else {
		    // Perform a right turn by decrease the right wheel speed and keep the left wheel to high
		    //calculate the speed difference in a linear fashion to the delta
		    int speedDecrease= Math.min(MAX_RIGHT_SPEED_DECREASE,distanceDelta*P_TYPE_COEFFICIENT);
			  setMotorSpeed(P_MOTOR_HIGH,P_MOTOR_NORM-speedDecrease);
		  }
	  }
	  else {
	    //Set the motor to the same nominal speed when the ideal distance is reached
	    setMotorSpeed(P_MOTOR_NORM,P_MOTOR_NORM);
      }
	   // Make the robot go forward with the defined speed
	  forward();
  }
  /**
   * A helper method to set the speed of both wheel 
   * @param leftSpeed
   * @param rightSpeed
   */
  private void setMotorSpeed(int leftSpeed,int rightSpeed) {
    // Set the left motor and right motor speed and update the global variable for status printing
    leftMotor.setSpeed(leftSpeed);
    currentLeftSpeed= leftSpeed;
    rightMotor.setSpeed(rightSpeed);
    currentRightSpeed=rightSpeed;
  }
  /**
   * A helper method to move forward and save redundency
   */
  private void forward() {
    // Set both motor as forward
    leftMotor.forward();
    rightMotor.forward();
  }
  /*
   * Samples the US sensor and invokes the selected controller on each cycle (non Javadoc).
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    if (type.equals("BangBang")) {
      while(true) {
        bangBangController(readUsDistance());
        Main.sleepFor(POLL_SLEEP_TIME);
      }
    } else if (type.equals("PType")) {
      while(true) {
        pTypeController(readUsDistance());
        Main.sleepFor(POLL_SLEEP_TIME);
      }
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
    return filter((int) (usData[0] * 100.0));
  }
  /**
   * Returns the speed of the two motor.
   * 
   * @return the speed of the two motor in string
   */
  public String reportCurrentSpeed() {
    String template = "L: "+currentLeftSpeed+" R: "+ currentRightSpeed;
    return template;
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
