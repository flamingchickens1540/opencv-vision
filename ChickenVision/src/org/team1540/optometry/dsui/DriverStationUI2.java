package org.team1540.optometry.dsui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.team1540.optometry.GoalBox;
import org.team1540.optometry.Vision;

public class DriverStationUI2 {
	private static HSVThresholdPicker picker = new HSVThresholdPicker(360, 20, 100);
	private static WebcamPanel webcamPanel = new WebcamPanel();
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	private static BufferedImage thresholdImage(BufferedImage image) {	
		Mat imageMat = Vision.imageToMat(image);
		Mat imageOutputMat = Vision.grayscaleOutputMat(imageMat);
		
		Vision.thresholdImage(imageMat, imageOutputMat, new Scalar(picker.getHueLower()*256, 
				picker.getSaturationLower()*256, 
				picker.getValueLower()*256), new Scalar(picker.getHueUpper()*256, 
						picker.getSaturationUpper()*256, 
						picker.getValueUpper()*256));
		BufferedImage imageOutput = Vision.matToImage(imageOutputMat);
		
		BufferedImage convertedImg = new BufferedImage(imageOutput.getWidth(), imageOutput.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	    convertedImg.getGraphics().drawImage(imageOutput, 0, 0, null);
		
//	    List<GoalBox> goalBoxes = Vision.isolateThresholdedGoals(imageOutputMat, 100);
//	    goalBoxes.sort((a, b) -> a.area() < b.area() ? 1 : -1);
//	    
//		Graphics2D g2d = (Graphics2D) convertedImg.getGraphics();
//		for (GoalBox box : goalBoxes) {
//			g2d.setColor(Color.MAGENTA);
//			g2d.fillRect((int)box.bottomLeft.x-1, (int)box.bottomLeft.y-1, 2, 2);
//			g2d.setColor(Color.RED);
//			g2d.fillRect((int)box.bottomRight.x-1, (int)box.bottomRight.y-1, 2, 2);
//			g2d.setColor(Color.GREEN);
//			g2d.fillRect((int)box.topLeft.x-1, (int)box.topLeft.y-1, 2, 2);
//			g2d.setColor(Color.BLUE);
//			g2d.fillRect((int)box.topRight.x-1, (int)box.topRight.y-1, 2, 2);
//			break;
//		}
		
		return convertedImg;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		JFrame frame = new JFrame("ChickenVision Tuner");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JPanel listPane = new JPanel();
		frame.setContentPane(listPane);
		frame.setLayout(new BoxLayout(listPane, BoxLayout.X_AXIS));
		
		listPane.add(webcamPanel);
		listPane.add(Box.createRigidArea(new Dimension(0, 20)));
		listPane.add(picker);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(875, 463);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		BufferedImage image = ImageIO.read(new File("/Users/jake/Downloads/2017VisionExample/Vision Images/LED Peg/1ftH5ftD2Angle0Brightness.jpg"));
		BufferedImage smallerImage = toBufferedImage(image.getScaledInstance((int) (640.f/1.5), (int) (480.f/1.5), Image.SCALE_FAST));
		
		for (;;) {
			webcamPanel.setImage(thresholdImage(smallerImage));
			Thread.sleep(100);			
		}
	}
}
