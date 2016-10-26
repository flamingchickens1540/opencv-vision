package org.team1540.chickenvision;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Image {
	private Mat image;
	private Mat oldImage;

	public Image(BufferedImage image) {
		image = toBufferedImageOfType(image, BufferedImage.TYPE_3BYTE_BGR);
		this.image = bufferedImageToMat(image);
		this.oldImage = bufferedImageToMat(image);
		//Imgproc.cvtColor(this.image, this.image, Imgproc.COLOR_BGR2HSV);
	}

	public Image(BufferedImage image, int newWidth, int newHeight, int hints) {
		this.image = bufferedImageToMat((BufferedImage) image.getScaledInstance(newWidth, newHeight, hints));
		this.oldImage = bufferedImageToMat((BufferedImage) image.getScaledInstance(newWidth, newHeight, hints));
		//Imgproc.cvtColor(this.image, this.image, Imgproc.COLOR_BGR2HSV);
	}

	public void threshold(Scalar low, Scalar high) {
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
		Core.inRange(image, low, high, image);
		Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2BGR);
		Core.multiply(image, new Scalar(1.0/256.0, 1.0/256.0, 1.0/256.0), image);
		Core.multiply(image, oldImage, image);
	}

	public BufferedImage toBufferedImage() {
		// Imgproc.cvtColor(image, image, Imgproc.COLOR_HSV2BGR);
		BufferedImage out;
		byte[] data = new byte[(int) (image.width() * image.height() * image.elemSize())];
		int type;
		image.get(0, 0, data);

		if (image.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}

		out = new BufferedImage(image.width(), image.height(), type);
		out.getRaster().setDataElements(0, 0, image.width(), image.height(), data);
		return out;
	}

	public static Mat bufferedImageToMat(BufferedImage image) {
		image = toBufferedImageOfType(image, BufferedImage.TYPE_3BYTE_BGR);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}

	public static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
		if (original == null) {
			throw new IllegalArgumentException("original == null");
		}

		if (original.getType() == type) {
			return original;
		}

		BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);
		Graphics2D g = image.createGraphics();
		try {
			g.setComposite(AlphaComposite.Src);
			g.drawImage(original, 0, 0, null);
		} finally {
			g.dispose();
		}

		return image;
	}
}
