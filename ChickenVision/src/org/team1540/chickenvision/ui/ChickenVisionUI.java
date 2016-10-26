package org.team1540.chickenvision.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.Scalar;
import org.team1540.chickenvision.Image;

public class ChickenVisionUI extends JFrame {
	private static final long serialVersionUID = 3524003050489214180L;

	private DynamicImagePanel dynImagePanel = new DynamicImagePanel(null);
	private BufferedImage original;
	private int currentH = 0;
	private int currentS = 0;
	private int currentV = 0;
	
	public ChickenVisionUI() {
		super("Chicken Vision UI");
		
		JPanel listPane = new JPanel();
		setContentPane(listPane);
		
		setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.add(dynImagePanel);
		
		// hsv
		JSlider h = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider s = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JSlider v = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		
		h.setMajorTickSpacing(15);
		h.setMinorTickSpacing(3);
		h.setPaintTicks(true);
		h.setPaintLabels(true);
		
		s.setMajorTickSpacing(15);
		s.setMinorTickSpacing(3);
		s.setPaintTicks(true);
		s.setPaintLabels(true);
		
		v.setMajorTickSpacing(15);
		v.setMinorTickSpacing(3);
		v.setPaintTicks(true);
		v.setPaintLabels(true);
		
		h.addChangeListener(c -> {
			currentH = h.getValue();
			updateImage();
		});
		
		s.addChangeListener(c -> {
			currentS = s.getValue();
			updateImage();
		});
		
		v.addChangeListener(c -> {
			currentV = v.getValue();
			updateImage();
		});
		
		listPane.add(h);
		listPane.add(s);
		listPane.add(v);
		
		//add(listPane);
		
		setBounds(0, 0, 740, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void updateImage() {
		Image image = new Image(original);
		int radius = 100;
		image.threshold(new Scalar((currentH-radius), (currentS-radius), (currentV-radius)), 
				new Scalar((currentH+radius), (currentS+radius), (currentV+radius)));
		dynImagePanel.setImage(image.toBufferedImage());
	}
	
	public static void main(String[] args) throws IOException {
		//System.setProperty("java.library.path", "/Users/jake/Sandbox/opencv/build/lib");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		ChickenVisionUI ui = new ChickenVisionUI();
		ui.original = ImageIO.read(new File("res/hsv.jpg"));
		ui.dynImagePanel.setImage(ui.original);
		ui.updateImage();
	}
}
