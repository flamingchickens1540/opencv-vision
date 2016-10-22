import java.awt.image.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.*;

public class ImageProcessor {
	
	public ImageProcessor() {
		
		// set the location of the native libraries
		//System.setProperty("java.library.path", "/home/robotics/colordetection/natives");
		
		//System.out.println("java.library.path="+System.getProperty("java.library.path"));
		
		//load the native libraries
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME );
	}
	
	/**
	 * Detect a given color from a BufferedImage
	 * @param lowerBounds of color to detect in HSV format in 8UC3 (three 8 bit channels)
	 * @param upperBounds of color to detect in HSV format in 8UC3 (three 8 bit channels)
	 * @param image BufferedImage to process
	 * @return BufferedImage with white pixels as true and black as false
	 */
	public BufferedImage detectColor(int[] lowerBounds, int[] upperBounds, BufferedImage image) {
		
		//construct the necessary Mats
		Mat originalMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		Mat processedMat = originalMat.clone();
		
		//convert the original image to a mat
		originalMat.put(0,0,((DataBufferByte) image.getRaster().getDataBuffer()).getData());
		
		//create a new scalar that constitutes the lower bound in HSV color
		Scalar lowerBound = new Scalar(lowerBounds[0],lowerBounds[1],lowerBounds[2]);
		//create a new scalar that constitutes the upper bound in HSV color
		Scalar upperBound = new Scalar(upperBounds[0],upperBounds[1],upperBounds[2]);
		
		//make the originalMat HSV instead of BGR
		Imgproc.cvtColor(originalMat, originalMat, Imgproc.COLOR_BGR2HSV);
	
		//using the lowerBound and upperBound, find all pixels inRange in the originalMat and put them in processedMat
		inRange(originalMat, lowerBound, upperBound, processedMat);
		
		//create an empty image in greyscale format (same as the processed mat)
		BufferedImage proccessedImage = new BufferedImage(processedMat.width(), processedMat.height(), BufferedImage.TYPE_BYTE_GRAY);

		//get the array used to store the BufferedImage's data and put the pixels from the mat in
		byte[] data = ((DataBufferByte) proccessedImage.getRaster().getDataBuffer()).getData();
		processedMat.get(0,0,data);
		
		return proccessedImage;
	}
	
}