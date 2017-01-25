package org.team1540.optometry;

import org.team1540.optometry.comm.KangarooInput;
import org.team1540.optometry.comm.RoboRIOOutput;

import ccre.cluck.Cluck;

public class RobotVisionMain {
	
	public static void setup() {
		Cluck.setupClient("10.15.40.163:4001", "kangaroo", "robot");
		
		RoboRIOOutput.setup();
		KangarooInput.setup();
		
		Cluck.publish("Vision Goal Available", KangarooInput.goalAvailable);
		Cluck.publish("Vision Goal Offset", KangarooInput.goalOffsetYaw);
	}
	
	/*
	 * To be run on the robot. See #visionaries in Slack for impl details.
	 */
	public static void autonomouslyLocateTarget() {
		/*
		 * Algorithm:
		 * - if goal available
		 * 	- let angle to turn = correct latency of camera angle to turn
		 *  - set a PID to target current angle + angle to turn
		 *  - when within reasonable accuracy and stopped
		 *   - go forward slowly until accuracy is unreasonable (hopefully never) or until reasonably close
		 * - else
		 *  - turn at slow speed until goal is available
		 */
	}
}
