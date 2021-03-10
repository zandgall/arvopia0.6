package com.zandgall.arvopia.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Chart {

	double[] vals;
	String[] components;
	String name;
	int x, y, width, height;
	
	Slice[] slices;
	
	public Chart(double[] vals, String[] components, Color[] colors, String name, int x, int y, int width, int height) {
		this.vals = vals;
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.components = components;
		
		slices = new Slice[vals.length];
		for(int i = 0; i<slices.length; i++) {
			slices[i]=new Slice(vals[i], components[i], colors[i]);
		}
	}
	
	public void update(double[] vals) {
		for(int i = 0; i<slices.length; i++) {
			slices[i].value = vals[i];
		}
	}
	
	public void render(Graphics g) {
		drawPie((Graphics2D) g, new Rectangle(x, y, width, height), slices);
		g.setColor(Color.black);
		g.drawString(name, x+width/2, y+height/2);
		for(int i = 0; i<slices.length; i++) {
			g.setColor(slices[i].color);
			g.fillRect(x, y+height+10+i*20, width, 15);
			g.setColor(Color.black);
			g.drawString(slices[i].name, x, y+height+23+i*20);
		}
	}

	void drawPie(Graphics2D g, Rectangle area, Slice[] slices) {
		double total = 0.0D;
		for (int i = 0; i < slices.length; i++) {
			total += slices[i].value;
		}

		double curValue = 0.0D;
		int startAngle = 0;
		for (int i = 0; i < slices.length; i++) {
			startAngle = (int) (curValue * 360 / total);
			int arcAngle = (int) (slices[i].value * 360 / total);

			g.setColor(slices[i].color);
			g.fillArc(area.x, area.y, area.width, area.height, startAngle, arcAngle);
			curValue += slices[i].value;
		}
	}

	class Slice {
		double value;
		Color color;
		String name;
		
		public Slice(double value, String name, Color color) {
			this.value = value;
			this.color = color;
			this.name = name;
		}
	}

}
