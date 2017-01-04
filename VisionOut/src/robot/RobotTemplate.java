package robot;

import ccre.channel.*;
import ccre.cluck.*;
import ccre.ctrl.*;
import ccre.frc.*;

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
    
    private static FloatInput yaw = Cluck.subscribeFI("Heading Yaw Angle", true);
    private static FloatOutput leftSide;
    private static FloatOutput rightSide;
    
    //pid values
    private static final FloatCell p = new FloatCell(0.5f);
    private static final FloatCell i = new FloatCell(0f);
    private static final FloatCell d = new FloatCell(0f);

    @Override
    public void setupRobot() throws ExtendedMotorFailureException {
    	leftSide = FRC.talonCAN(4).simpleControl().combine(FRC.talonCAN(5).simpleControl().combine(FRC.talonCAN(6).simpleControl()));
    	rightSide = FRC.talonCAN(1).simpleControl().combine(FRC.talonCAN(2).simpleControl().combine(FRC.talonCAN(3).simpleControl()));
    	
    	PIDController rotate = turnRobot(90);
    	rotate.send(leftSide);
    	rotate.send(rightSide.negate());
    }
    
    public PIDController turnRobot(float degreesToTurn) {
    	float initialYaw = yaw.get();
    	return new PIDController(yaw, new FloatCell(Math.abs(yaw.get()-degreesToTurn)), p, i, d);
    }
}
