package org.team1540.optometry;

import org.opencv.core.Point;

public class GoalBox {
	public Point bottomLeft, bottomRight, topLeft, topRight;
	
	public GoalBox(Point bottomLeft, Point bottomRight, Point topLeft, Point topRight) {
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		this.topLeft = topLeft;
		this.topRight = topRight;
	}
	
	/*
	 * Return the area of the quadrilateral formed with the four given points.
	 */
	public double area() {
		return 0.0;
	}
}
