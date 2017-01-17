package org.team1540.optometry;

import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Point;
import org.opencv.core.Rect;

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
	
	/*
	 * Return the center of the quadrilateral.
	 */
	public Point center() {
		return new Point((bottomLeft.x+bottomRight.x+topLeft.x+topRight.x)/4.0,
						 (bottomLeft.y+bottomRight.y+topLeft.y+topRight.y)/4.0);
	}
	
	/*
	 * Return the approximate height of the quadrilateral.
	 */
	public double height() {
		return -((topRight.y + topLeft.y) - (bottomRight.y + bottomLeft.y)) / 2.0;
	}
	
	/*
	 * Return the approximate width of the quadrilateral.
	 */
	public double width() {
		return ((topRight.x + bottomRight.x) - (topLeft.x + bottomLeft.x)) / 2.0;
	}
	
	private static double determinant(double a, double b, double c, 
			double d, double e, double f, 
			double g, double h, double i) {
		return a*(e*i - f*h) - b*(d*i - f*g) + c*(d*h - e*g);
	}
	
	public static List<GoalBox> allGoalsInRectangle(Rect r, List<GoalBox> boxes) {
		return boxes.stream().filter(box -> r.contains(box.bottomLeft)
				&& r.contains(box.bottomRight)
				&& r.contains(box.topLeft)
				&& r.contains(box.topRight)).collect(Collectors.toList());
	}
}
