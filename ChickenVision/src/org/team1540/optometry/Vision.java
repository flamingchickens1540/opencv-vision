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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Vision {
	private static double GOAL_CERTAINTY_THRESHOLD = 0.5;
	
	public static Mat imageToMat(BufferedImage bgrImage) {
		Mat mat = new Mat(bgrImage.getHeight(), bgrImage.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bgrImage.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		return mat;
	}
	
	public static Mat grayscaleOutputMat(Mat image) {
		return new Mat(image.width() / 3, image.height(), CvType.CV_8SC1);
	}

	public static BufferedImage matToImage(Mat hsvMat) {
		int type;
		if (hsvMat.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else {
			type = BufferedImage.TYPE_3BYTE_BGR;
			Imgproc.cvtColor(hsvMat, hsvMat, Imgproc.COLOR_HSV2BGR);
		}

		byte[] data = new byte[(int) (hsvMat.width() * hsvMat.height() * hsvMat.elemSize())];
		hsvMat.get(0, 0, data);
		
		BufferedImage out = new BufferedImage(hsvMat.width(), hsvMat.height(), type);
		out.getRaster().setDataElements(0, 0, hsvMat.width(), hsvMat.height(), data);
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
		List<GoalBox> stageOne = new ArrayList<>();
		
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
							
							if (p_x+1 < input.height() && input.get(p_x+1, p_y)[0] > 0.5 && marked.get(p_x+1, p_y)[0] < 0.5) {
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
							
							if (p_y+1 < input.width() && input.get(p_x, p_y+1)[0] > 0.5 && marked.get(p_x, p_y+1)[0] < 0.5) {
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
							stageOne.add(new GoalBox(new Point(bottomLeft.y, bottomLeft.x),
									new Point(bottomRight.y, bottomRight.x),
									new Point(topLeft.y, topLeft.x),
									new Point(topRight.y, topRight.x)));
						}
					}
				}
			}
		}
		
		List<GoalBox> stageTwo = new ArrayList<>();
		
		// determine which of the blobs look like a 2017 FRC gear goal
		// complexity: n^2
		
		// let them be about 50% off actual dimensions; might have to be decreased
		double dimensionsSimilarity = 2.3;
		
		// check a rectangle twice the size (four times area)
		double rectMultiplier = 2.0;
		
		for (int i=0; i<stageOne.size(); ++i) {
			GoalBox b = stageOne.get(i);
			
			// generate box to look at
			double possibleUnitSizeA = b.width() / 2.0; // 2 inches wide
			double possibleUnitSizeB = b.height() / 5.0; // 5 inches tall
						
			// check that they are approximately within the right 
			if (Math.abs(1.0 - (possibleUnitSizeA / possibleUnitSizeB)) > dimensionsSimilarity) {
				continue;
			}
			
			double unitSize = (possibleUnitSizeA + possibleUnitSizeB) / 2.0;
			
			Point bCenter = b.center();
			Point centerToCheck = new Point(bCenter.x + unitSize*8.25, bCenter.y);
			
			double[] rectPoints = {centerToCheck.x - rectMultiplier*unitSize*(2.0/2), 
					centerToCheck.y - rectMultiplier*unitSize*(5.0/2),
					centerToCheck.x + rectMultiplier*unitSize*(2.0/2), 
					centerToCheck.y + rectMultiplier*unitSize*(5.0/2),};
			Rect toCheckA = new Rect(new Point(rectPoints[0], rectPoints[1]),
									 new Point(rectPoints[2], rectPoints[3]));
			
			stageTwo.add(b);
			stageTwo.add(new GoalBox(new Point(rectPoints[0], rectPoints[1]),
					new Point(rectPoints[0], rectPoints[3]),
					new Point(rectPoints[2], rectPoints[1]),
					new Point(rectPoints[2], rectPoints[3])));
						
			List<GoalBox> possibleMatches = GoalBox.allGoalsInRectangle(toCheckA, stageOne);
			possibleMatches.sort((m, n) -> Double.compare(m.area(), n.area()));
			
//			stageTwo.addAll(possibleMatches);
			
			if (possibleMatches.size() == 0) {
				continue;
			}
			
			GoalBox otherGoal;
			if (possibleMatches.size() == 1) {
				otherGoal = possibleMatches.get(0);
			} else {
				GoalBox gbA = possibleMatches.get(0);
				GoalBox gbB = possibleMatches.get(1);
				
				if (gbA.center().y < gbB.center().y) {
					otherGoal = new GoalBox(gbB.bottomLeft, gbB.bottomRight, gbA.topLeft, gbA.topRight);
				} else {
					otherGoal = new GoalBox(gbA.bottomLeft, gbA.bottomRight, gbB.topLeft, gbB.topRight);
				}
			}
			
			if (b.center().x < otherGoal.center().x) {
				stageTwo.add(new GoalBox(b.bottomLeft, otherGoal.bottomRight, b.topLeft, otherGoal.topRight));
			} else {
				stageTwo.add(new GoalBox(otherGoal.bottomLeft, b.bottomRight, otherGoal.topLeft, b.topRight));
			}
		}
		
		return stageTwo;
	}
	
	/*
	 * Calculate the horizontal angle away in degrees from the center given anglePerPixel.
	 */
	public static double horizontalAngleFromCenter(Point point, Point center, double anglePerPixel) {
		return (point.x - center.x) * anglePerPixel;
	}
	
	public static double verticalAngleFromCenter(Point point, Point center, double anglePerPixel) {
		return (point.y - center.y) * anglePerPixel;
	}
}
