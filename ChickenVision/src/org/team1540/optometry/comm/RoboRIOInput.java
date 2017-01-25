package org.team1540.optometry.comm;

import ccre.channel.FloatInput;

public class RoboRIOInput {
	private static final CommInput input = new CommInput("robot");
	
	public static FloatInput hueMin;
	public static FloatInput saturationMin;
	public static FloatInput valueMin;
	public static FloatInput hueMax;
	public static FloatInput saturationMax;
	public static FloatInput valueMax;
	public static FloatInput anglePerPixel;
	
	public static void setup() {
		hueMin = input.subscribeFI("hueMin");
		saturationMin = input.subscribeFI("saturationMin");
		valueMin = input.subscribeFI("valueMin");
		hueMax = input.subscribeFI("hueMax");
		saturationMax = input.subscribeFI("saturationMax");
		valueMax = input.subscribeFI("valueMax");
		anglePerPixel = input.subscribeFI("anglePerPixel");
	}
}
