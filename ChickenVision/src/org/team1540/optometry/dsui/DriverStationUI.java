package org.team1540.optometry.dsui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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

import miscellaneous.Webcam;

public class DriverStationUI {
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
		
		Vision.thresholdImage(imageMat, imageOutputMat, new Scalar(picker.getHueLower()*180, 
				picker.getSaturationLower()*256, 
				picker.getValueLower()*256), new Scalar(picker.getHueUpper()*180, 
						picker.getSaturationUpper()*256, 
						picker.getValueUpper()*256));
		BufferedImage imageOutput = Vision.matToImage(imageOutputMat);
		
		BufferedImage convertedImg = new BufferedImage(imageOutput.getWidth(), imageOutput.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	    convertedImg.getGraphics().drawImage(imageOutput, 0, 0, null);
		
	    List<GoalBox> goalBoxes = Vision.isolateThresholdedGoals(imageOutputMat, 30);
	    goalBoxes.sort((a, b) -> a.area() < b.area() ? 1 : -1);
	    	    
	    int boxRadius = 3;
		Graphics2D g2d = (Graphics2D) convertedImg.getGraphics();
		for (GoalBox box : goalBoxes) {
			g2d.setColor(Color.YELLOW);
			g2d.drawLine((int)box.bottomLeft.x, (int)box.bottomLeft.y, (int)box.bottomRight.x, (int)box.bottomRight.y);
			g2d.drawLine((int)box.bottomRight.x, (int)box.bottomRight.y, (int)box.topRight.x, (int)box.topRight.y);
			g2d.drawLine((int)box.topRight.x, (int)box.topRight.y, (int)box.topLeft.x, (int)box.topLeft.y);
			g2d.drawLine((int)box.topLeft.x, (int)box.topLeft.y, (int)box.bottomLeft.x, (int)box.bottomLeft.y);
			g2d.setColor(Color.MAGENTA);
			g2d.fillRect((int)box.bottomLeft.x-boxRadius, (int)box.bottomLeft.y-boxRadius, boxRadius*2+1, boxRadius*2+1);
			g2d.setColor(Color.RED);
			g2d.fillRect((int)box.bottomRight.x-boxRadius, (int)box.bottomRight.y-boxRadius, boxRadius*2+1, boxRadius*2+1);
			g2d.setColor(Color.GREEN);
			g2d.fillRect((int)box.topLeft.x-boxRadius, (int)box.topLeft.y-boxRadius, boxRadius*2+1, boxRadius*2+1);
			g2d.setColor(Color.CYAN);
			g2d.fillRect((int)box.topRight.x-boxRadius, (int)box.topRight.y-boxRadius, boxRadius*2+1, boxRadius*2+1);
		}
		
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
		
//		BufferedImage image = ImageIO.read(new File("/Users/jake/Downloads/2017VisionExample/Vision Images/LED Peg/1ftH7ftD0Angle0Brightness.jpg"));
//		BufferedImage image = ImageIO.read(new File("/Users/jake/Desktop/1.jpg"));
//		BufferedImage image = ImageIO.read(new File("/Users/jake/Downloads/HSLS255.jpg"));
		
		
		Webcam webcam = new Webcam("webcam", "10.15.40.68", true, 0);
//		webcam.start();
//		final Webcam webcam = Webcam.getWebcams().get(1);
//		webcam.setViewSize(WebcamResolution.VGA.getSize());
//		webcam.open();
		
//		frame.addWindowListener(new WindowListener() {
//
//			@Override
//			public void windowOpened(WindowEvent e) {				
//			}
//
//			@Override
//			public void windowClosing(WindowEvent e) {
//				webcam.close();
//				Runtime.getRuntime().halt(0);
//			}
//
//			@Override
//			public void windowClosed(WindowEvent e) {
//			}
//
//			@Override
//			public void windowIconified(WindowEvent e) {				
//			}
//
//			@Override
//			public void windowDeiconified(WindowEvent e) {				
//			}
//
//			@Override
//			public void windowActivated(WindowEvent e) {				
//			}
//
//			@Override
//			public void windowDeactivated(WindowEvent e) {				
//			}
//		});
//				
//		while (!webcam.isOpen()) Thread.sleep(100);;
//		
//		while (webcam.isOpen()) {
		
		
		Thread thing = new Thread(() -> {
			while (true) {
				System.out.println("Hue L " + picker.getHueLower() + " | "
								 + "Hue U " + picker.getHueUpper() + " | "
								 + "Sat L " + picker.getSaturationLower() + " | "
								 + "Sat U " + picker.getSaturationUpper() + " | "
								 + "Val L " + picker.getValueLower() + " | "
								 + "Val L " + picker.getValueUpper());
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
			}
		});
		thing.setDaemon(true);
		thing.start();
		
		while (webcam.isAlive()) {
			BufferedImage possibleImage = webcam.getImage();
			if (possibleImage != null) {
				BufferedImage smallerImage = toBufferedImage(thresholdImage(possibleImage).getScaledInstance((int) (640.f/1.5), (int) (480.f/1.5), Image.SCALE_FAST));
				webcamPanel.setImage(smallerImage);
			}
			Thread.sleep(100);
		}
	}
}
