package org.team1540.optometry.comm;

import ccre.channel.BooleanCell;
import ccre.channel.BooleanIO;
import ccre.channel.FloatCell;
import ccre.channel.FloatIO;

public class KangarooOutput {
	private static CommOutput output = new CommOutput();
	
	public static FloatIO highGoalOffsetYaw = new FloatCell();
	public static FloatIO highGoalOffsetPitch = new FloatCell();
	public static FloatIO lowGoalOffsetYaw = new FloatCell();
	public static FloatIO lowGoalOffsetPitch = new FloatCell();
	public static BooleanIO highGoalAvailable = new BooleanCell();
	public static BooleanIO lowGoalAvailable = new BooleanCell();
	
	static {
		output.publish("highGoalOffsetYaw", highGoalOffsetYaw);
		output.publish("highGoalOffsetPitch", highGoalOffsetPitch);
		output.publish("lowGoalOffsetYaw", lowGoalOffsetYaw);
		output.publish("lowGoalOffsetPitch", lowGoalOffsetPitch);
		output.publish("highGoalAvailable", highGoalAvailable);
		output.publish("lowGoalAvailable", lowGoalAvailable);
	}
}
