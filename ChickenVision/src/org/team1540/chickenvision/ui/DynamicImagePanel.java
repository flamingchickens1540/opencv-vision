package org.team1540.chickenvision.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DynamicImagePanel extends JPanel {
	private static final long serialVersionUID = 8606266100909502687L;

	private BufferedImage currentImage;
	
	public DynamicImagePanel(BufferedImage originalImage) {
		super();
		setImage(originalImage);
	}
	
	public DynamicImagePanel() {
		super();
		setImage(null);
	}
	
	public void setImage(BufferedImage newImage) {
		currentImage = newImage;
		if (currentImage != null) {
			setBounds(0, 0, currentImage.getWidth(), currentImage.getHeight());
		} else {
			setBounds(0, 0, 0, 0);
		}
		revalidate();
		repaint();
	}
	
	@Override
	public void paint(Graphics graphics) {
		Graphics2D g2 = (Graphics2D) graphics;
		if (currentImage != null) {
			g2.drawImage(currentImage, 0, 0, currentImage.getWidth(), currentImage.getHeight(), null);
		}
	}
}
