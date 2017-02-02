package org.team1540.visionout;

import ccre.channel.BooleanCell;
import ccre.channel.EventCell;
import ccre.channel.FloatCell;
import ccre.channel.FloatIO;
import ccre.channel.FloatOutput;
import ccre.ctrl.ExtendedMotorFailureException;
import ccre.drivers.ctre.talon.TalonExtendedMotor;
import ccre.frc.FRC;
import ccre.instinct.InstinctModule;

public class TurningCalibration extends InstinctModule {

    private final TalonExtendedMotor leftFrontMotor = FRC.talonCAN(4);
    private final TalonExtendedMotor leftCenterMotor = FRC.talonCAN(5);
    private final TalonExtendedMotor leftBackMotor = FRC.talonCAN(6);
    private final TalonExtendedMotor rightFrontMotor = FRC.talonCAN(1);
    private final TalonExtendedMotor rightCenterMotor = FRC.talonCAN(2);
    private final TalonExtendedMotor rightBackMotor = FRC.talonCAN(3);
    
    private FloatOutput leftSide;
    private FloatOutput rightSide;
	
    private FloatIO encoderPosition = leftFrontMotor.modEncoder().getEncoderPosition();
    
    private BooleanCell keepTurning = new BooleanCell(false);
    private FloatCell degreesOffsetFromGoal;
    private FloatCell toTurn = new FloatCell(0f);
    private EventCell update = new EventCell();
    
	private AutoTurning autoTurner;
	
	public TurningCalibration(FloatCell degreesOffsetFromGoal) throws ExtendedMotorFailureException {
		this.degreesOffsetFromGoal = degreesOffsetFromGoal;
		autoTurner = new AutoTurning(encoderPosition, keepTurning, toTurn, update);
		leftSide = leftFrontMotor.simpleControl().combine(leftCenterMotor.simpleControl().combine(leftBackMotor.simpleControl()));
    	rightSide = rightFrontMotor.simpleControl().combine(rightCenterMotor.simpleControl().combine(rightBackMotor.simpleControl()));
    	
    	autoTurner.asInput().send(leftSide);
    	autoTurner.asInput().send(rightSide);
	}
	
	public TurningCalibration(FloatCell degreesOffsetFromGoal, float startingCalibration) throws ExtendedMotorFailureException {
		this.degreesOffsetFromGoal = degreesOffsetFromGoal;
		autoTurner = new AutoTurning(encoderPosition, keepTurning, toTurn, update);
		autoTurner.turnCalibration.set(startingCalibration);
		
		leftSide = leftFrontMotor.simpleControl().combine(leftCenterMotor.simpleControl().combine(leftBackMotor.simpleControl()));
    	rightSide = rightFrontMotor.simpleControl().combine(rightCenterMotor.simpleControl().combine(rightBackMotor.simpleControl()));
    	
    	autoTurner.asInput().send(leftSide);
    	autoTurner.asInput().send(rightSide);
	}
	
	@Override
	protected void autonomousMain() throws Throwable {
		float inaccuracy = 0;
		while (inaccuracy<.95 | inaccuracy>1.05) {
			float startTarget = degreesOffsetFromGoal.get();
			toTurn.set(degreesOffsetFromGoal.get());
			
			//check if the robot has moved in the last 0.5 seconds
			boolean hasChanged = true;
			while (hasChanged) {
				float startPosition = encoderPosition.get();
				waitForTime(500);
				if (encoderPosition.get() == startPosition) {
					hasChanged = false;
				}
			}
		
			inaccuracy = startTarget/(startTarget+encoderPosition.get());
			autoTurner.turnCalibration.set(autoTurner.turnCalibration.get()+encoderPosition.get()/startTarget);
		}
	}
	
	public FloatCell getCalibration() {
		return autoTurner.turnCalibration;
	}

}
