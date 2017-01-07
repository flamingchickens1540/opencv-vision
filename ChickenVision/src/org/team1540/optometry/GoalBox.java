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
		return 0.5*(Math.abs(determinant(bottomLeft.x, bottomLeft.y, 1,
								bottomRight.x, bottomRight.y, 1,
								topLeft.x, topLeft.y, 1))
				  + Math.abs(determinant(bottomLeft.x, bottomLeft.y, 1,
								topRight.x, topRight.y, 1,
								topLeft.x, topLeft.y, 1)));
	}
	
	private double determinant(double a, double b, double c, 
			double d, double e, double f, 
			double g, double h, double i) {
		return a*(e*i - f*h) - b*(d*i - f*g) + c*(d*h - e*g);
	}
}
