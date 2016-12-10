package org.team1540.optometry;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class Vision {
	private static double GOAL_CERTAINTY_THRESHOLD = 0.5;
	
	/*
	 * Given an image matrix and some HSV bounds, return a new matrix with a 1 in every location
	 * where the HSV of the pixel is within the bounds.
	 */
	public static void thresholdImage(Mat input, Mat output, Scalar lowBounds, Scalar highBounds) {
		
	}
	
	/*
	 * Calculate the left-bottom-most point in the matrix with a value of 1.
	 */
	public Point leftBottomMost(Mat input) {
		return null;
	}
	
	/*
	 * Calculate the right-bottom-most point in the matrix with a value of 1.
	 */
	public Point rightBottomMost(Mat input) {
		return null;
	}
	
	/*
	 * Calculate the left-top-most point in the matrix with a value of 1.
	 */
	public Point leftTopMost(Mat input) {
		return null;
	}
	
	/*
	 * Calculate the right-top-most point in the matrix with a value of 1.
	 */
	public Point rightTopMost(Mat input) {
		return null;
	}
	
	/*
	 * Returns a double between 0 and 1 that represents how certain you are that the given Mat
	 * is a thresholded picture of a goal.
	 * 
	 * Algorithm:
	 *  Pick the left-bottom-most, right-bottom-most, left-top-most, right-top-most points. Draw
	 *  what you think a goal should look like given those vertices and compare the theoretical
	 *  goal to the actual one. Return the comparison result.
	 */
	public static double goalCertainty(Mat input, Point leftBottomMost, Point leftTopMost, Point rightBottomMost, Point rightTopMost) {
		// accepting all matrices is actually a reasonable implementation for now
		return 1.0f;
	}
	
	/*
	 * Given the result of thresholdImage (a matrix of values either 0 or 1), identify every solid
	 * image (a set of thresholded pixels that are all adjacent.) This is a solid image iff the number
	 * of pixels in the set is greater than or equal to minPixelSize.
	 * 
	 * For all solid images within the larger matrix, pass them to goalCertainty. If this returns a
	 * number greater than GOAL_CERTAINTY_THRESHOLD, calculate the four points: left-top-most, 
	 * right-top-most, left-bottom-most, right-bottom-most.
	 * 
	 * Return a list of GoalBoxes.
	 */
	public static List<GoalBox> isolateThresholdedGoals(Mat input, int minPixelSize) {
		return null;
	}
	
	/*
	 * Calculate the horizontal angle away in degrees from the center given degreesPerPixel.
	 */
	public static double horizontalAngleFromCenter(Point point, Point center, double pixelsPerDegree) {
		return 0.0;
	}
}
