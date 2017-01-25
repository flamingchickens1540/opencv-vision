package org.team1540.optometry.comm;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.cluck.Cluck;

public class CommOutput {	
	public void publish(String name, BooleanInput input) {
		Cluck.publish(name, input);
	}
	
	public void publish(String name, FloatInput input) {
		Cluck.publish(name, input);
	}
}
