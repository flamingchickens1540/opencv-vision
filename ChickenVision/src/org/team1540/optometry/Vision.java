package org.team1540.optometry;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Vision {
	private static double GOAL_CERTAINTY_THRESHOLD = 0.5;
	
	public static Mat imageToMat(BufferedImage image) {
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		//Imgproc.cvtColor(mat, mat, Imgproc.COLOR_HSV2RGB);
		return mat;
	}
	
	public static Mat grayscaleOutputMat(Mat image) {
		return new Mat(image.width() / 3, image.height(), CvType.CV_8SC1);
	}

	public static BufferedImage matToImage(Mat image) {
		BufferedImage out;
		byte[] data = new byte[(int) (image.width() * image.height() * image.elemSize())];
		int type;
		image.get(0, 0, data);

		if (image.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}

		out = new BufferedImage(image.width(), image.height(), type);
		out.getRaster().setDataElements(0, 0, image.width(), image.height(), data);
		return out;
	}
	
	/*
	 * Given an image matrix and some HSV bounds, return a new matrix with a 1 in every location
	 * where the HSV of the pixel is within the bounds.
	 */
	public static void thresholdImage(Mat input, Mat output, Scalar lowBounds, Scalar highBounds) {
		Core.inRange(input, lowBounds, highBounds, output);
	}
	
	/*
	 * Calculate the left-bottom-most point in the matrix with a value of 1.
	 */
	public static Point leftBottomMost(Mat input) {
		int bestRow = 0;
		int bestCol = Integer.MAX_VALUE;
		
		for (int i=0; i<input.rows(); ++i) {
			for (int j=0; j<input.cols(); ++j) {
				if (input.get(i, j)[0] > 0.9 && (-bestRow + bestCol) > (i+j)) {
					bestRow = i;
					bestCol = j;
				}
			}
		}
		
		return new Point(bestRow, bestCol);
	}
	
	/*
	 * Calculate the right-bottom-most point in the matrix with a value of 1.
	 */
	public static Point rightBottomMost(Mat input) {
		int bestRow = 0;
		int bestCol = 0;
		
		for (int i=0; i<input.rows(); ++i) {
			for (int j=0; j<input.cols(); ++j) {
				if (input.get(i, j)[0] > 0.9 && (-bestRow - bestCol) > (i+j)) {
					bestRow = i;
					bestCol = j;
				}
			}
		}
		
		return new Point(bestRow, bestCol);
	}
	
	/*
	 * Calculate the left-top-most point in the matrix with a value of 1.
	 */
	public static Point leftTopMost(Mat input) {
		int bestRow = input.rows();
		int bestCol = input.cols();
		
		for (int i=0; i<input.rows()-1; ++i) {
			for (int j=0; j<input.cols()-1; ++j) {
				if (input.get(i, j)[0] > 0.9 && (bestRow + bestCol) > (i+j)) {
					bestRow = i;
					bestCol = j;
				}
			}
		}
		
		return new Point(bestRow, bestCol);
	}
	
	/*
	 * Calculate the right-top-most point in the matrix with a value of 1.
	 */
	public static Point rightTopMost(Mat input) {
		int bestRow = 0;
		int bestCol = 0;
		
		for (int i=0; i<input.rows(); ++i) {
			for (int j=0; j<input.cols(); ++j) {
				if (input.get(i, j)[0] > 0.9 && (bestRow - bestCol) > (i+j)) {
					bestRow = i;
					bestCol = j;
				}
			}
		}
		
		return new Point(bestRow, bestCol);
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
		List<GoalBox> list = new ArrayList<>();
		
		Mat marked = Mat.zeros(input.rows(), input.cols(), CvType.CV_8U);
		
		Queue<Point> points = new LinkedList<>();
		for (int i=0; i<input.rows(); ++i) {
			for (int j=0; j<input.cols(); ++j) {
				if (marked.get(i, j)[0] < 0.5) {
					if (input.get(i, j)[0] > 0.5) {
						int numPoints = 1;
						
						Point bottomLeft = new Point(i, j);
						Point bottomRight = new Point(i, j);
						Point topLeft = new Point(i, j);
						Point topRight = new Point(i, j);
						
						points.add(new Point(i, j));
						marked.put(i, j, 1);
						while (!points.isEmpty()) {
							numPoints++;
							Point p = points.remove();
							int p_x = (int)p.x;
							int p_y = (int)p.y;
							if (p_x-1 >= 0 && input.get(p_x-1, p_y)[0] > 0.5 && marked.get(p_x-1, p_y)[0] < 0.5) {
								Point check = new Point(p_x-1, p_y);
								points.add(check);
								marked.put(p_x-1, p_y, 1);
								if (bottomLeft.x - bottomLeft.y < check.x - check.y) {
									bottomLeft = check;
								}
								
								if (bottomRight.x + bottomRight.y < check.x + check.y) {
									bottomRight = check;
								}
								
								if (topLeft.x + topLeft.y > check.x + check.y) {
									topLeft = check;
								}
								
								if (topRight.x - topRight.y > check.x - check.y) {
									topRight = check;
								}
							}
							
							if (p_x+1 >= 0 && input.get(p_x+1, p_y)[0] > 0.5 && marked.get(p_x+1, p_y)[0] < 0.5) {
								Point check = new Point(p_x+1, p_y);
								points.add(check);
								marked.put(p_x+1, p_y, 1);
								if (bottomLeft.x - bottomLeft.y < check.x - check.y) {
									bottomLeft = check;
								}
								
								if (bottomRight.x + bottomRight.y < check.x + check.y) {
									bottomRight = check;
								}
								
								if (topLeft.x + topLeft.y > check.x + check.y) {
									topLeft = check;
								}
								
								if (topRight.x - topRight.y > check.x - check.y) {
									topRight = check;
								}
							}
							
							if (p_y-1 >= 0 && input.get(p_x, p_y-1)[0] > 0.5 && marked.get(p_x, p_y-1)[0] < 0.5) {
								Point check = new Point(p_x, p_y-1);
								points.add(check);
								marked.put(p_x, p_y-1, 1);
								if (bottomLeft.x - bottomLeft.y < check.x - check.y) {
									bottomLeft = check;
								}
								
								if (bottomRight.x + bottomRight.y < check.x + check.y) {
									bottomRight = check;
								}
								
								if (topLeft.x + topLeft.y > check.x + check.y) {
									topLeft = check;
								}
								
								if (topRight.x - topRight.y > check.x - check.y) {
									topRight = check;
								}
							}
							
							if (p_y+1 >= 0 && input.get(p_x, p_y+1)[0] > 0.5 && marked.get(p_x, p_y+1)[0] < 0.5) {
								Point check = new Point(p_x, p_y+1);
								points.add(check);
								marked.put(p_x, p_y+1, 1);
								if (bottomLeft.x - bottomLeft.y < check.x - check.y) {
									bottomLeft = check;
								}
								
								if (bottomRight.x + bottomRight.y < check.x + check.y) {
									bottomRight = check;
								}
								
								if (topLeft.x + topLeft.y > check.x + check.y) {
									topLeft = check;
								}
								
								if (topRight.x - topRight.y > check.x - check.y) {
									topRight = check;
								}
							}
						}
						
						if (numPoints >= minPixelSize) {
							list.add(new GoalBox(new Point(bottomLeft.y, bottomLeft.x),
									new Point(bottomRight.y, bottomRight.x),
									new Point(topLeft.y, topLeft.x),
									new Point(topRight.y, topRight.x)));
						}
					}
				}
			}
		}
		
		return list;
	}
	
	/*
	 * Calculate the horizontal angle away in degrees from the center given degreesPerPixel.
	 */
	public static double horizontalAngleFromCenter(Point point, Point center, double pixelsPerDegree) {
		return (point.x - center.x) * pixelsPerDegree;
	}
	
	public static double verticalAngleFromCenter(Point point, Point center, double pixelsPerDegree) {
		return (point.y - center.y) * pixelsPerDegree;
	}
}
