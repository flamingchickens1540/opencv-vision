package org.team1540.visionout;

import ccre.channel.BooleanCell;
import ccre.channel.FloatCell;
import ccre.channel.FloatOutput;
import ccre.cluck.Cluck;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.frc.FRCApplication;

/**
 * This is the core class of a CCRE project. The CCRE launching system will make
 * sure that this class is loaded, and will have set up everything else before
 * loading it. If you change the name, use Eclipse's rename functionality. If
 * you don't, you will have to change the name in Deployment.java.
 *
 * Make sure to set {@link #TEAM_NUMBER} to your team number.
 */
public class Robot implements FRCApplication {

    /**
     * This is where you specify your team number. It is used to find your
     * roboRIO when you download code.
     */
    public static final int TEAM_NUMBER = 1540;
    
    private final TalonExtendedMotor leftFrontMotor = FRC.talonCAN(4);
    private final TalonExtendedMotor leftCenterMotor = FRC.talonCAN(5);
    private final TalonExtendedMotor leftBackMotor = FRC.talonCAN(6);
    private final TalonExtendedMotor rightFrontMotor = FRC.talonCAN(1);
    private final TalonExtendedMotor rightCenterMotor = FRC.talonCAN(2);
    private final TalonExtendedMotor rightBackMotor = FRC.talonCAN(3);
    
    private FloatOutput leftSide;
    private FloatOutput rightSide;
    
    private BooleanCell trueCell = new BooleanCell(true);
    private FloatCell degreesToTurn = new FloatCell(0);
    
    private AutoTurning testTurner = new AutoTurning(leftFrontMotor.modEncoder(), trueCell, degreesToTurn);

    @Override
    public void setupRobot() throws ExtendedMotorFailureException {
    	leftSide = leftFrontMotor.simpleControl().combine(leftCenterMotor.simpleControl().combine(leftBackMotor.simpleControl()));
    	rightSide = rightFrontMotor.simpleControl().combine(rightCenterMotor.simpleControl().combine(rightBackMotor.simpleControl()));
    	
    	testTurner.asInput().multipliedBy(0.5f).send(leftSide);
    	testTurner.asInput().multipliedBy(0.5f).send(rightSide);
    	
    	Cluck.publish("turnerInput", testTurner.asInput());
    	
        FRC.joystick1.button(1).onPress().send(() -> test());
    }
    
    public void test() {
    	degreesToTurn.set(90);;
    }
    
    
}
