package org.team1540.optometry.comm;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;

public class KangarooInput {
	private static final CommInput input = new CommInput("kangaroo");
	
	public static FloatInput goalOffsetYaw;
	public static BooleanInput goalAvailable;
	
	public static void setup() {
		goalOffsetYaw = input.subscribeFI("goalOffsetYaw");
		goalAvailable = input.subscribeBI("goalAvailable");
	}
}