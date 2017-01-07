package org.team1540.optometry.dsui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.team1540.optometry.GoalBox;
import org.team1540.optometry.Vision;

import miscellaneous.Webcam;

/*
 * JFrame class to control the UI for the driver station.
 */
public class DriverStationUI {
	private static JFrame frame = new JFrame("Driver Station UI");
	private static int hueLow = 0;
	private static int satLow = 0;
	private static int valLow = 0;
	private static int hueHigh = 0;
	private static int satHigh = 0;
	private static int valHigh = 0;
	
	private static BufferedImage thresholdImage(BufferedImage image) {	
		Mat imageMat = Vision.imageToMat(image);
		Mat imageOutputMat = Vision.grayscaleOutputMat(imageMat);
		
		Vision.thresholdImage(imageMat, imageOutputMat, new Scalar(hueLow, satLow, valLow), new Scalar(hueHigh, satHigh, valHigh));
		BufferedImage imageOutput = Vision.matToImage(imageOutputMat);
		
		BufferedImage convertedImg = new BufferedImage(imageOutput.getWidth(), imageOutput.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	    convertedImg.getGraphics().drawImage(imageOutput, 0, 0, null);
		
	    List<GoalBox> goalBoxes = Vision.isolateThresholdedGoals(imageOutputMat, 100);
	    goalBoxes.sort((a, b) -> a.area() < b.area() ? 1 : -1);
	    
		Graphics2D g2d = (Graphics2D) convertedImg.getGraphics();
		for (GoalBox box : goalBoxes) {
			g2d.setColor(Color.MAGENTA);
			g2d.fillRect((int)box.bottomLeft.x-1, (int)box.bottomLeft.y-1, 2, 2);
			g2d.setColor(Color.RED);
			g2d.fillRect((int)box.bottomRight.x-1, (int)box.bottomRight.y-1, 2, 2);
			g2d.setColor(Color.GREEN);
			g2d.fillRect((int)box.topLeft.x-1, (int)box.topLeft.y-1, 2, 2);
			g2d.setColor(Color.BLUE);
			g2d.fillRect((int)box.topRight.x-1, (int)box.topRight.y-1, 2, 2);
			break;
		}
		
		return convertedImg;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		WebcamPanel webcamPanel = new WebcamPanel();
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JPanel listPane = new JPanel();
		frame.setContentPane(listPane);
		
		frame.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.add(webcamPanel);
		
		// hsv
		JSlider hLow = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider sLow = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider vLow = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		
		JSlider hHigh = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider sHigh = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider vHigh = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		
		hLow.setMajorTickSpacing(15);
		hLow.setMinorTickSpacing(3);
		hLow.setPaintTicks(true);
		hLow.setPaintLabels(true);
		
		sLow.setMajorTickSpacing(15);
		sLow.setMinorTickSpacing(3);
		sLow.setPaintTicks(true);
		sLow.setPaintLabels(true);
		
		vLow.setMajorTickSpacing(15);
		vLow.setMinorTickSpacing(3);
		vLow.setPaintTicks(true);
		vLow.setPaintLabels(true);
		
		hHigh.setMajorTickSpacing(15);
		hHigh.setMinorTickSpacing(3);
		hHigh.setPaintTicks(true);
		hHigh.setPaintLabels(true);
		
		sHigh.setMajorTickSpacing(15);
		sHigh.setMinorTickSpacing(3);
		sHigh.setPaintTicks(true);
		sHigh.setPaintLabels(true);
		
		vHigh.setMajorTickSpacing(15);
		vHigh.setMinorTickSpacing(3);
		vHigh.setPaintTicks(true);
		vHigh.setPaintLabels(true);
		
		listPane.add(hLow);
		listPane.add(sLow);
		listPane.add(vLow);
		listPane.add(hHigh);
		listPane.add(sHigh);
		listPane.add(vHigh);
		
		hLow.addChangeListener(c -> hueLow = hLow.getValue());
		sLow.addChangeListener(c -> satLow = sLow.getValue());
		vLow.addChangeListener(c -> valLow = vLow.getValue());
		hHigh.addChangeListener(c -> hueHigh = hHigh.getValue());
		sHigh.addChangeListener(c -> satHigh = sHigh.getValue());
		vHigh.addChangeListener(c -> valHigh = vHigh.getValue());
		
		hLow.setValue(0);
		sLow.setValue(140);
		vLow.setValue(210);
		hHigh.setValue(33);
		sHigh.setValue(255);
		vHigh.setValue(255);
		
		frame.setSize(600, 800);
		frame.setVisible(true);
		
//		Webcam webcam = new Webcam("Camera 1", "10.15.40.13", true, 0);
//		for (;;) {
//			BufferedImage webcamImage = webcam.getImage();
//			if (webcamImage != null) {
//				webcamPanel.setImage(thresholdImage(webcamImage));
//			}
//		}
		
		BufferedImage image = ImageIO.read(new File("/Users/jake/Desktop/1.jpg"));
		for (;;) {
			webcamPanel.setImage(thresholdImage(image));
			Thread.sleep(10);
		}
	}
}
