package ca.mcgill.ecse211.project;

// static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Button;

/**
 * The main driver class for the lab.
 */
public class Main {

  /**
   * The main entry point.
   * 
   * @param args not used
   */
  public static void main(String[] args) {
    display = new Display();
    Display.showText(
        new String[] {"Press Left for mode A", "Press Right for mode B", "Press Up for mode C"});
    int option = Button.waitForAnyPress();
    // Wait here until button pressed to select the mode
    if (option == Button.ID_LEFT) {
      modeController = new ModeController(1);
    } else if (option == Button.ID_RIGHT) {
      modeController = new ModeController(2);
    } else if (option == Button.ID_UP) {
      modeController = new ModeController(3);
    } else if (option == Button.ID_DOWN) {
      MoveController.setSpeed(ROTATE_SPEED);
      MoveController.turnBy(720);
    } else {
      Main.showErrorAndExit("Error - invalid button!");
    }
    // Start the main thread to perform the action.
    Thread moveModeThread = new Thread(modeController);
    moveModeThread.start();

    // Any key press terminate the program
    while (option != Button.ID_ESCAPE) {
      option = Button.waitForAnyPress();
    }
    System.exit(0);
  }

  /**
   * Shows error and exits program.
   */
  public static void showErrorAndExit(String errorMessage) {
    TEXT_LCD.clear();
    System.err.println(errorMessage);

    // Sleep for 2s so user can read error message
    sleepFor(2000);

    System.exit(-1);
  }

  /**
   * Sleeps for the specified duration.
   * 
   * @param millis the duration in milliseconds
   */
  public static void sleepFor(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      // Nothing to do here
    }
  }

}
