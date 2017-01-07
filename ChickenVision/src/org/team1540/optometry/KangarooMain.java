package org.team1540.optometry;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.team1540.optometry.dsui.WebcamPanel;

public class KangarooMain {
	/*
	 * To be run on the Kangaroo. See #visionaries in Slack for impl details.
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture camera;
		while ((camera = connectToCamera(0)) == null) {
			System.err.println("There was an error connecting to the camera; waiting 1000ms and retrying.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 2560/2);
		camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 1600/2);
		
		double width = camera.get(Videoio.CV_CAP_PROP_FRAME_WIDTH);
		double height = camera.get(Videoio.CV_CAP_PROP_FRAME_HEIGHT);
		System.out.println("Dimensions " + width + ", " + height);
		
		Mat imageMat = new Mat();
		
		JFrame frame = new JFrame();
		WebcamPanel panel = new WebcamPanel();
		frame.add(panel);
		
		for (;;) {
			camera.read(imageMat);
			panel.setImage(Vision.matToImage(imageMat));
		}
	}
	
	public static VideoCapture connectToCamera(int index) {
		VideoCapture camera = new VideoCapture(0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
//		if (camera.isOpened()) {
			return camera;
//		} else {
//			return null;
//		}
	}
}
