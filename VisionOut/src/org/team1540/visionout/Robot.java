package org.team1540.visionout;

import ccre.channel.*;
import ccre.cluck.*;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonEncoder;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.*;

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
    
    private FloatOutput leftSide;
    private FloatOutput rightSide;
    private final TalonExtendedMotor leftFrontMotor = FRC.talonCAN(4);
    private final TalonExtendedMotor leftCenterMotor = FRC.talonCAN(5);
    private final TalonExtendedMotor leftBackMotor = FRC.talonCAN(6);
    private final TalonExtendedMotor rightFrontMotor = FRC.talonCAN(1);
    private final TalonExtendedMotor rightCenterMotor = FRC.talonCAN(2);
    private final TalonExtendedMotor rightBackMotor = FRC.talonCAN(3);
    
    private final TalonEncoder leftEncoder = leftFrontMotor.modEncoder();
    
    //private static final TalonEncoder rightCenterEncoder = FRC.talonCAN(2).modEncoder();
    
    //pid values
    private static final FloatCell p = new FloatCell(-0.005f);
    private static final FloatCell i = new FloatCell(-0.00f);
    private static final FloatCell d = new FloatCell(0f);
    
    private final FloatCell target = new FloatCell(0f);
    private final PIDController mainPID = new PIDController(leftEncoder.getEncoderPosition(), target, p, i, d);
    
    //calibration
    private FloatCell turnCalibration = new FloatCell(21.35f);

    @Override
    public void setupRobot() throws ExtendedMotorFailureException {
    	System.out.println("Started setupRobot");
    	
    	Cluck.publish(".P",p);
    	Cluck.publish(".I", i);
    	Cluck.publish(".D", d);
    	Cluck.publish("Target", target);
    	Cluck.publish("Turn Calibration", turnCalibration);
    	
    	leftSide = leftFrontMotor.simpleControl().combine(leftCenterMotor.simpleControl().combine(leftBackMotor.simpleControl()));
    	rightSide = rightFrontMotor.simpleControl().combine(rightCenterMotor.simpleControl().combine(rightBackMotor.simpleControl()));
    	
    	Drive.tank(FRC.joystick1.axis(2).deadzone(0.95f), FRC.joystick1.axis(6).negated().deadzone(0.95f), leftSide, rightSide);
    	
    	Cluck.publish("Left Center Encoder", leftEncoder.getEncoderPosition());
    	
    	FRC.joystick1.button(1).onPress().send(() -> turnRobot(90));
    	mainPID.setOutputBounds(0.7f);
    	mainPID.send(leftSide);
    	mainPID.send(rightSide);
    	mainPID.updateWhen(FRC.constantPeriodic);
    	
    	FRC.startTele.send(() -> mainPID.reset());
    	
    	//because PIDController doesn't implement FloatInput
    	Cluck.publish("ROTATE PID", mainPID.negated().negated());
    }
    
    public void turnRobot(float degrees) {
    	Float startPosition = leftEncoder.getEncoderPosition().get();
    	target.set(degrees*turnCalibration.get()+startPosition);
    	mainPID.reset();
    }
}
