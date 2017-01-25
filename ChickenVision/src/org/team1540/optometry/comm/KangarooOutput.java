package org.team1540.optometry.comm;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanIO;
import ccre.channel.FloatCell;
import ccre.channel.FloatIO;

public class KangarooOutput {
	private static final CommOutput output = new CommOutput();
	public static final FloatIO goalOffsetYaw = new FloatCell();
	public static final BooleanIO goalAvailable = new BooleanCell();
	
	public static void setup() {		
		output.publish("goalOffsetYaw", goalOffsetYaw.asInput());
		output.publish("goalAvailable", goalAvailable.asInput());
	}
}
