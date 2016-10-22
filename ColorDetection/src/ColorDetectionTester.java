import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

public class ColorDetectionTester {

	public static void main(String[] args) {
		
		BufferedImage originalImage = null;
		
		try {
			originalImage = ImageIO.read(new File("TestImages/1280px-Flag_of_Japan.svg.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ImageProcessor imageProcessor = new ImageProcessor();
		
		BufferedImage processedImage = imageProcessor.detectColor(new int[] {0,0,0}, new int[] {255,5,255},originalImage);
		try {
			ImageIO.write(processedImage, "png", new File("TestImages/processed.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
