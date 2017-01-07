package org.team1540.optometry.comm;

import ccre.channel.BooleanInput;
import ccre.channel.FloatInput;

public class KangarooInput {
	private static CommInput input = new CommInput("kangaroo");
	
	public static FloatInput highGoalOffsetYaw = input.subscribeFI("highGoalOffsetYaw");
	public static FloatInput highGoalOffsetPitch = input.subscribeFI("highGoalOffsetPitch");
	public static FloatInput lowGoalOffsetYaw = input.subscribeFI("lowGoalOffsetYaw");
	public static FloatInput lowGoalOffsetPitch = input.subscribeFI("lowGoalOffsetPitch");
	public static BooleanInput highGoalAvailable = input.subscribeBI("highGoalAvailable");
	public static BooleanInput lowGoalAvailable = input.subscribeBI("lowGoalAvailable");
}
