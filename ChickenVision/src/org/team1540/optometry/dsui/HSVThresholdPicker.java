package org.team1540.optometry.dsui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

public class HSVThresholdPicker extends JComponent {
	private static final long serialVersionUID = 1412079752207284707L;

	private float minHue = 0.f,
			      maxHue = 1.f;
	
	private float minSaturation = 0.f,
				  maxSaturation = 1.f;
	
	private float minValue = 0.f,
				  maxValue = 1.f;
	
	private int numHuePoints,
				numSaturationPoints,
				numValuePoints;
	
	private int saturationWidth;
	private int hueWidth;
	
	private int minSaturationBound = 24,
				maxSaturationBound = 100;
	
	private int minHueBound = 80,
				maxHueBound = 140;
	
	private int minValueBound = 40,
				maxValueBound = 100;
	
	private boolean minBoundsDragging = false;
	private boolean maxBoundsDragging = false;
	private boolean minSatBoundsDragging = false;
	private boolean maxSatBoundsDragging = false;
	private int prevDragX;
	private int prevDragY;
	
	public HSVThresholdPicker(int hueWidth, int saturationWidth, int valHeight) {
		super();
		
		numHuePoints = hueWidth;
		numSaturationPoints = valHeight;
		numValuePoints = valHeight;
		this.saturationWidth = saturationWidth;
		this.hueWidth = hueWidth;
		
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (new Rectangle(minHueBound-2, minValueBound-2, 8, 8).contains(e.getX(), e.getY())) {
					if (!minBoundsDragging && !maxBoundsDragging) {
						minBoundsDragging = true;
						prevDragX = e.getX();
						prevDragY = e.getY();
					}
				} else if (new Rectangle(maxHueBound-2, maxValueBound-2, 8, 8).contains(e.getX(), e.getY())) {
					if (!minBoundsDragging && !maxBoundsDragging) {
						maxBoundsDragging = true;
						prevDragX = e.getX();
						prevDragY = e.getY();
					}
				} else if (new Rectangle(hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3))-4, 
						minSaturationBound-4, 9, 9).contains(e.getX(), e.getY())) {
					if (!minSatBoundsDragging && !maxSatBoundsDragging) {
						minSatBoundsDragging = true;
						prevDragX = e.getX();
						prevDragY = e.getY();
					}
				} else if (new Rectangle(hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3))-4, 
						maxSaturationBound-4, 9, 9).contains(e.getX(), e.getY())) {
					if (!minSatBoundsDragging && !maxSatBoundsDragging) {
						maxSatBoundsDragging = true;
						prevDragX = e.getX();
						prevDragY = e.getY();
					}
				} 
				
				if (minBoundsDragging) {
					HSVThresholdPicker.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					prevDragX = Math.max(0, prevDragX);
					prevDragX = Math.min(numHuePoints, prevDragX);
					prevDragY = Math.max(0, prevDragY);
					prevDragY = Math.min(numValuePoints, prevDragY);
					
					minHueBound += e.getX() - prevDragX;
					minValueBound += e.getY() - prevDragY;
					
					minHueBound = Math.max(0, minHueBound);
					minHueBound = Math.min(numHuePoints, minHueBound);
					minValueBound = Math.max(0, minValueBound);
					minValueBound = Math.min(numValuePoints, minValueBound);
					
					prevDragX = e.getX();
					prevDragY = e.getY();
				} else if (maxBoundsDragging) {
					HSVThresholdPicker.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					prevDragX = Math.max(0, prevDragX);
					prevDragX = Math.min(numHuePoints, prevDragX);
					prevDragY = Math.max(0, prevDragY);
					prevDragY = Math.min(numValuePoints, prevDragY);
					
					maxHueBound += e.getX() - prevDragX;
					maxValueBound += e.getY() - prevDragY;
					
					maxHueBound = Math.max(0, maxHueBound);
					maxHueBound = Math.min(numHuePoints, maxHueBound);
					maxValueBound = Math.max(0, maxValueBound);
					maxValueBound = Math.min(numValuePoints, maxValueBound);
					
					prevDragX = e.getX();
					prevDragY = e.getY();
				} else if (minSatBoundsDragging) {
					HSVThresholdPicker.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					prevDragY = Math.max(0, prevDragY);
					prevDragY = Math.min(numSaturationPoints, prevDragY);
					
					minSaturationBound += e.getY() - prevDragY;
					
					minSaturationBound = Math.max(0, minSaturationBound);
					minSaturationBound = Math.min(numSaturationPoints, minSaturationBound);
					
					prevDragX = e.getX();
					prevDragY = e.getY();
				} else if (maxSatBoundsDragging) {
					HSVThresholdPicker.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					prevDragY = Math.max(0, prevDragY);
					prevDragY = Math.min(numSaturationPoints, prevDragY);
					
					maxSaturationBound += e.getY() - prevDragY;

					maxSaturationBound = Math.max(0, maxSaturationBound);
					maxSaturationBound = Math.min(numSaturationPoints, maxSaturationBound);
					
					prevDragX = e.getX();
					prevDragY = e.getY();
				}
				
				HSVThresholdPicker.this.invalidate();
				HSVThresholdPicker.this.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (new Rectangle(minHueBound-2, minValueBound-2, 8, 8).contains(e.getX(), e.getY())
				 || new Rectangle(maxHueBound-2, maxValueBound-2, 8, 8).contains(e.getX(), e.getY())
				 || new Rectangle(hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3))-4, 
							minSaturationBound-4, 9, 9).contains(e.getX(), e.getY())
				 || new Rectangle(hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3))-4, 
							maxSaturationBound-4, 9, 9).contains(e.getX(), e.getY())) {
					if (!minBoundsDragging && !maxBoundsDragging) {
						HSVThresholdPicker.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
					}
				} else {
					HSVThresholdPicker.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				minBoundsDragging = false;
				maxBoundsDragging = false;
				minSatBoundsDragging = false;
				maxSatBoundsDragging = false;
				
				HSVThresholdPicker.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
		});
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		for (int h=0; h<numHuePoints; ++h) {
			for (int v=0; v<numValuePoints; ++v) {
				float hue = minHue + (h * ((maxHue - minHue) / numHuePoints));
				float value = minValue + (v * ((maxValue - minValue) / numValuePoints));
				g2.setColor(Color.getHSBColor(hue, (getSaturationUpper() + getSaturationLower()) / 2, value));
				g2.fillRect(h, v, 1, 1);
			}
		}
		
		for (int s=0; s<numSaturationPoints; ++s) {
			float saturation = minSaturation + (s * ((maxSaturation - minSaturation) / numSaturationPoints));
			g2.setColor(Color.getHSBColor((240.f/360.f), saturation, (getValueUpper() + getValueLower()) / 2));
			g2.fillRect(numHuePoints+1, s, saturationWidth, 1);
		}
		
		g2.setColor(Color.white);
		if (minHueBound < maxHueBound) {
			if (minValueBound < maxValueBound) {
				g2.drawLine(minHueBound, minValueBound, minHueBound, maxValueBound);
				g2.drawLine(minHueBound, maxValueBound, maxHueBound, maxValueBound);
				g2.drawLine(maxHueBound, maxValueBound, maxHueBound, minValueBound);
				g2.drawLine(maxHueBound, minValueBound, minHueBound, minValueBound);
			}
		}
		
		g2.setColor(Color.black);
		g2.drawRect(minHueBound-2, minValueBound-2, 5, 5);
		g2.drawRect(maxHueBound-2, maxValueBound-2, 5, 5);
		
		g2.setColor(Color.black);
		int[] xCoordinates = { 
				hueWidth + saturationWidth + 1 + 1, 
				hueWidth + saturationWidth + 1 + 1 + (int) (5*Math.sqrt(3)), 
				hueWidth + saturationWidth + 1 + 1 + (int) (5*Math.sqrt(3)) };
		int[] yCoordinates = { 
				minSaturationBound, 
				minSaturationBound - 5, 
				minSaturationBound + 5 };
		g2.fillPolygon(xCoordinates, yCoordinates, 3);
		
		int[] xCoordinates2 = { 
				hueWidth + saturationWidth + 1 + 1, 
				hueWidth + saturationWidth + 1 + 1 + (int) (5*Math.sqrt(3)), 
				hueWidth + saturationWidth + 1 + 1 + (int) (5*Math.sqrt(3)) };
		int[] yCoordinates2 = { 
				maxSaturationBound, 
				maxSaturationBound - 5, 
				maxSaturationBound + 5 };
		g2.fillPolygon(xCoordinates2, yCoordinates2, 3);
		
		if (minSaturationBound < maxSaturationBound) {
			g2.drawLine(hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3)), minSaturationBound, 
					hueWidth + saturationWidth + 1 + 1 + (int) (2.5*Math.sqrt(3)), maxSaturationBound);
		}		
	}
	
	public float getHueUpper() {
		return minHue + (maxHueBound * ((maxHue - minHue) / numHuePoints));
	}
	
	public float getHueLower() {
		return minHue + (minHueBound * ((maxHue - minHue) / numHuePoints));
	}
	
	public float getValueUpper() {
		return minValue + (maxValueBound * ((maxValue - minValue) / numValuePoints));
	}
	
	public float getValueLower() {
		return minValue + (minValueBound * ((maxValue - minValue) / numValuePoints));
	}
	
	public float getSaturationUpper() {
		return minSaturation + (maxSaturationBound * ((maxSaturation - minSaturation) / numSaturationPoints));
	}
	
	public float getSaturationLower() {
		return minSaturation + (minSaturationBound * ((maxSaturation - minSaturation) / numSaturationPoints));
	}
}
