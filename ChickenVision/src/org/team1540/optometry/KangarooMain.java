package org.team1540.optometry;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.team1540.optometry.comm.KangarooOutput;

import com.github.sarxos.webcam.Webcam;

public class KangarooMain {
	
	private static boolean shouldStop = false;
	
	public static AtomicReference<BufferedImage> currentFrame = new AtomicReference<>();
	
	public static void requestHalt() {
		shouldStop = true;
	}
	
	/*
	 * To be run on the Kangaroo. See #visionaries in Slack for impl details.
	 */
	public static void main(String[] args) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Thread webcamThread = new Thread(() -> {
			for (;;) {
				Webcam webcam;
				while ((webcam = openWebcam(0)) == null) {
					int TIMEOUT = 1000;
					System.err.println("Error opening webcam, retrying in "+TIMEOUT+"ms");
					try {
						Thread.sleep(TIMEOUT);
					} catch (Exception e) {
					}
				}
				
				while (webcam.isOpen()) {
					try {
						currentFrame.set(webcam.getImage());
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
				currentFrame.set(null);
			}
		});
		
		webcamThread.setDaemon(true);
		webcamThread.start();
		
		while (!shouldStop) {
			BufferedImage frame = currentFrame.get();
			if (frame != null) {
				Mat frameMat = Vision.imageToMat(frame);
				List<GoalBox> boxes = Vision.isolateThresholdedGoals(frameMat, 50);
				boxes.sort((a, b) -> -Double.compare(a.area(), b.area()));
				if (boxes.size() < 0) {
					GoalBox target = boxes.get(0);
					Point targetCenter = target.center();
					Point cameraCenter = new Point(frame.getWidth()/2, frame.getHeight()/2);
					
					double pitchOffset = Vision.verticalAngleFromCenter(targetCenter, cameraCenter, 1.0);
					double yawOffset = Vision.horizontalAngleFromCenter(targetCenter, cameraCenter, 1.0);
					
					KangarooOutput.highGoalAvailable.set(true);
					KangarooOutput.highGoalOffsetPitch.set((float) pitchOffset);
					KangarooOutput.highGoalOffsetYaw.set((float) yawOffset);
				}
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	public static Webcam openWebcam(int index) {
		Webcam webcam = Webcam.getWebcams().get(index);
		if (webcam.open()) {
			return webcam;
		} else {
			return null;
		}
	}
}
