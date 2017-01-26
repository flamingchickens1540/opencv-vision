package robot;

import org.team1540.optometry.RobotVisionMain;

import ccre.cluck.Cluck;
import ccre.frc.FRCApplication;

/**
 * This is the core class of a CCRE project. The CCRE launching system will make
 * sure that this class is loaded, and will have set up everything else before
 * loading it. If you change the name, use Eclipse's rename functionality. If
 * you don't, you will have to change the name in Deployment.java.
 *
 * Make sure to set {@link #TEAM_NUMBER} to your team number.
 */
public class RobotTemplate implements FRCApplication {

    /**
     * This is where you specify your team number. It is used to find your
     * roboRIO when you download code.
     */
    public static final int TEAM_NUMBER = 1540;

    @Override
    public void setupRobot() {
    	Cluck.setupClient("10.15.40.68:4001", "kangaroo", "robot");
    	RobotVisionMain.setup();
    }
}
