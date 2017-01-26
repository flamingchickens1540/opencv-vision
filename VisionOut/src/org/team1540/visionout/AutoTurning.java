package org.team1540.visionout;

import ccre.channel.*;
import ccre.cluck.Cluck;
import ccre.ctrl.*;
import ccre.drivers.ctre.talon.TalonEncoder;
import ccre.frc.*;


/**
 * A class for turning robots.
 * If the goal is not seen, sets the output to a constant value.
 * If the goal is seen, uses PID to change the output to move the provided number of degrees.
 * @author Jonathan Edelman
 */
public class AutoTurning {
    
    private FloatIO encoderPosition;
    
    //pid values
    public static FloatCell p = new FloatCell(-0.005f);
    public static FloatCell i = new FloatCell(-0.00f);
    public static FloatCell d = new FloatCell(-0f);
    
    //calibration
    public static FloatCell turnCalibration = new FloatCell(21.35f);
    
    private StateMachine currentValue = new StateMachine("staticTurner","staticTurner", "pid");
    
    private FloatInput degreesToTurn;
    private BooleanInput goalIsSeen; 
	private FloatInput target;
	private FloatCell startPosition = new FloatCell();
    private PIDController pid;
    
    private FloatCell staticTurner = new FloatCell(1f);
    
    /**
     * The one and only constructor.
     * @param encoder The encoder on the wheel to be used for measuring turning progress.
     * @param goalIsSeen If the goal has been seen or not.
     * @param degreesToTurn The number of degrees to turn once the goal is seen.
     */
    public AutoTurning(TalonEncoder encoder, BooleanInput goalIsSeen, FloatInput degreesToTurn) {
    	this.encoderPosition = encoder.getEncoderPosition();
    	this.goalIsSeen = goalIsSeen;
    	this.degreesToTurn = degreesToTurn;
    	startPosition.set(encoderPosition.get());
    	
    	target = degreesToTurn.multipliedBy(turnCalibration).plus(startPosition);
    	
    	pid = new PIDController(encoderPosition, target, p, i, d);
    	
    	//set up the extra stuff for the pid properly
    	pid.setOutputBounds(1.0f);
    	pid.updateWhen(FRC.constantPeriodic);
    	
    	//set up the state machine. when there is a goal, change states from noGoal to goal.
    	//noGoal corresponds with a static value of either 1 or 0, goal corresponds with the PID
    	currentValue.setStateWhen("staticTurner", goalIsSeen.onRelease());
    	currentValue.setStateWhen("pid", goalIsSeen.onPress());
    	
    	//when the goal is seen, reset the start position
    	degreesToTurn.send(a -> startPosition.set(encoderPosition.get()));
    	
    	Cluck.publish("target", target);
    	Cluck.publish("pid", pid.negated().negated());
    	Cluck.publish("encoderPosition",encoderPosition);
    	Cluck.publish("startPosition",startPosition);
    	Cluck.publish("degreesToTurn", degreesToTurn);
    	Cluck.publish("asInput", asInput());
    }
    
    /**
     * Gets the current value of the output of an instance of the AutoTurning class.
     * It's up to you to decide what to do with this value. Hint: in a tank drive robot, try sending this value
     * to one side and to the other side negated.
     * @return A FloatInput ranging from -1 to 1 inclusive.
     */
    public FloatInput asInput() {
    	return currentValue.selectByState(staticTurner, pid);
    }
}
