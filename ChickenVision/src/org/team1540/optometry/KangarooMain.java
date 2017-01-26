package org.team1540.optometry;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.team1540.optometry.comm.KangarooOutput;
import org.team1540.optometry.comm.RoboRIOInput;

import ccre.cluck.Cluck;
import ccre.cluck.tcp.CluckTCPClient;

public class KangarooMain {
	
	private static boolean shouldStop = false;
	private static Mat currentFrame = null;
	private static Object videoLock = new Object();
	
	public static void requestHalt() {
		shouldStop = true;
	}
	
	/*
	 * To be run on the Kangaroo. See #visionaries in Slack for impl details.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Cluck.setupServer(4001);
		CluckTCPClient client = new CluckTCPClient("RoboRIO-1540-FRC.local", Cluck.getNode(), "robot", "kangaroo");
		client.start();
		Cluck.getNode().notifyNetworkModified();
	
		Thread videoThread = new Thread(() -> {
			VideoCapture vc = openWebcam(0);
			
			while (true) {
				synchronized (videoLock) {
					vc.read(currentFrame);
					Imgproc.cvtColor(currentFrame, currentFrame, Imgproc.COLOR_BGR2HSV);
				}
				
				try {
					Thread.sleep(1000 / 30);
				} catch (Exception e) {
				}
			}
		});
		
		videoThread.setDaemon(true);
		videoThread.start();
		
		// Wait for system to establish connection
		while (!client.isEstablished()) {
			Thread.sleep(10);		
		}
		
		KangarooOutput.setup();
		RoboRIOInput.setup();
		
		while (!shouldStop) {
//			Logger.info("Hue Minimum " + RoboRIOInput.hueMin.get());
			if (currentFrame != null) {
				synchronized (videoLock) {
					Mat imageOutputMat = Vision.grayscaleOutputMat(currentFrame);
					Vision.thresholdImage(currentFrame, imageOutputMat, 
							new Scalar(0.18888889*180, 0.90999997*256, 0.58*256),
							new Scalar(0.43333334*180, 256, 256));
					
//					ImageIO.write(Vision.matToImage(currentFrame), "jpeg", new File("/home/robotics/image2.jpg"));
//					ImageIO.write(Vision.matToImage(imageOutputMat), "jpeg", new File("/home/robotics/image.jpg"));
					
					List<GoalBox> boxes = Vision.isolateThresholdedGoals(imageOutputMat, 50);
					boxes.sort((a, b) -> -Double.compare(a.area(), b.area()));
					
					if (!boxes.isEmpty()) {
						GoalBox goal = boxes.get(0);				
						Point center = new Point(imageOutputMat.height()/2, imageOutputMat.width()/2);
						float yawOffset = (float) Vision.horizontalAngleFromCenter(goal.center(), center, RoboRIOInput.anglePerPixel.get());
						
						// only send a true over the network when it changes
						if (KangarooOutput.goalAvailable.get() == false) {
							KangarooOutput.goalAvailable.set(true);
						}
						KangarooOutput.goalOffsetYaw.set(yawOffset);
					} else {
						// only send a false over the network when it changes
						if (KangarooOutput.goalAvailable.get() == true) {
							KangarooOutput.goalAvailable.set(false);
						}
					}
				}
			}
			
			try {
				Thread.sleep(1000 / 30);
			} catch (Exception e) {
			}
		}
	}
	
	public static VideoCapture openWebcam(int index) {
		VideoCapture vc = new VideoCapture(0);
		if (vc.isOpened()) {
			currentFrame = Mat.zeros((int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT), 
					(int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH), 
					CvType.CV_8SC3);
			return vc;
		} else {
			throw new RuntimeException("Could not open camera. Try running with sudo.");
		}
	}
}
