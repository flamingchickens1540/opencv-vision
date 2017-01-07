package org.team1540.optometry.comm;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;
import ccre.cluck.CluckNode;
import ccre.cluck.CluckPublisher;

public class CommOutput {	
	private final CluckNode node = new CluckNode();
	
	public void publish(String name, BooleanInput input) {
		CluckPublisher.publish(node, name, input);
	}
	
	public void publish(String name, FloatInput input) {
		CluckPublisher.publish(node, name, input);
	}
}
