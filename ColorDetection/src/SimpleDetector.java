import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.*;
import static org.opencv.imgcodecs.Imgcodecs.*;

public class SimpleDetector {
	
	private static BufferedImage originalImage;
	private static Mat originalMat;
	private static Mat processedMat;

	
	public static void main( String[] args ) {
		//load the native libraries
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		//load in the original image as a BufferedImage
		try {
			originalImage = ImageIO.read(new File("TestImages/1280px-Flag_of_Japan.svg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//construct the necessary Mats
		//must be down here otherwise the necessary libraries aren't loaded
		originalMat = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8UC3);
		processedMat = new Mat(originalImage.getHeight(), originalImage.getWidth(), CvType.CV_8UC3);
		
		System.out.println(originalImage.getWidth()+"x"+originalImage.getHeight());
		
		//convert the original image to a mat
		//byte[] data = ((DataBufferByte) originalImage.getRaster().getDataBuffer()).getData();
		//originalMat.put(0, 0, data);
		originalMat.put(0,0,((DataBufferByte) originalImage.getRaster().getDataBuffer()).getData());
		
		//create a new scalar that constitutes the lower bound in HSV color
		Scalar lowerBound = new Scalar(0,0,250);
		//create a new scalar that constitutes the upper bound in HSV color
		Scalar upperBound = new Scalar(180,1,255);
		
		//make the originalMat HSV instead of RGB
		Imgproc.cvtColor(originalMat, originalMat, Imgproc.COLOR_BGR2HSV);
		
		//using the lowerBound and upperBound, find all pixels inRange in the originalMat and put them in processedMat
		inRange(originalMat, lowerBound, upperBound, processedMat);
		
		//color the processedMat
		//Imgproc.cvtColor(processedMat, processedMat, Imgproc);
		
		imwrite("TestImages/processed.png",processedMat);
		
	}
	
}