package com.zandgall.arvopia.water;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.utils.Public;

public class Water {

	Handler game;
	
	public double calmness = 0.4;
	public boolean calmer = true;

	int x, y, width, height;
	int resolution;

	double[] xP, yP, mP;
	int source = 1;
	long sourceTimer = 0;

	Polygon water;

	Color color;
	
	public Water(Handler game, int x, int y, int width, int height, int resolution, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.game = game;
		
		this.resolution = resolution;
		
		this.color = color;

		source = resolution / 2 + 1;

		xP = new double[resolution + 2];
		yP = new double[resolution + 2];
		mP = new double[resolution + 2];

		xP[0] = x;
		yP[0] = y + height;
		xP[resolution + 1] = x + width;
		yP[resolution + 1] = y + height;

		for (int k = 1; k < resolution; k++) {
			yP[k] = y;
		}

		int k = 1;
		for (int j = 0; j < width; j += width / resolution) {
			xP[(int) Public.range(0, resolution, k)] = j+x;
			k++;
		}

		xP[resolution] = x + width;
		yP[resolution] = y;

		yP[source] = (double) (y + Public.random(-5, 5));

		for (int i = 0; i < mP.length; i++) {
			mP[i] = 0;
		}
		mP[source] = Public.random(-0.5, 0.5);

		int[] ix = new int[resolution + 2];
		int[] iy = new int[resolution + 2];

		for (int i = 0; i < resolution + 2; i++) {
			ix[i] = (int) xP[i];
			iy[i] = (int) yP[i];
		}

		water = new Polygon(ix, iy, resolution + 2);
	}

	public int getNewSource(int x, int y, int radius) {
		for (int i = 1; i < resolution; i++) {
			if (Public.dist(x, y, xP[i], yP[i]) < radius) {
				return i;
			}
		}

		return -1;
	}
	
	public Rectangle box() {
		return new Rectangle(x, y, width, height);
	}
	
	public void collision(int x, int y, int width, int height, double force, boolean canComeUp) {
		for (int i = x; i < x + width; i++)
			if (getNewSource(i, y, 2) != -1 && (canComeUp || y > yP[getNewSource(i, y, 2)])) {
				calmness = Math.max(calmness, force);
				if (yP[getNewSource(i, y, 10)] < this.y + height * 2) {
					yP[getNewSource(i, y, 10)] = y;
					source = (int) Public.range(1, resolution, getNewSource(i + width / 2, y, 2));
					sourceTimer = 0;
				}
			}
	}
	
	public void collision(Rectangle bounds) {
		collision(bounds.x, bounds.y, bounds.width, bounds.height, 0.5, true);
	}

	public void tick() {
		double wind = game.getWind()/10;
		if (sourceTimer > 100) {
			if (calmness > Math.max(Math.abs(wind), 0.1)) {
				calmness -= 0.005;
				if (Public.difference(calmness, Math.max(Math.abs(wind), 0.1)) < 0.2)
					calmness = Math.max(Math.abs(wind), 0.1);
			} else if (calmness < Math.max(Math.abs(wind), 0.1)) {
				calmness = Math.max(Math.abs(wind), 0.1);
			} else if (calmness == Math.max(Math.abs(wind), 0.1))
				if (wind > 0)
					source = 2;
				else
					source = resolution;
		}

		if (Public.difference(mP[source], 0) == 0.1) {
			yP[source] = y;
			mP[source] = 0;
		}

		if (yP[source] > y && mP[source] > -3) {
			mP[source] -= 0.1;
		} else if (yP[source] < y && mP[source] < 3) {
			mP[source] += 0.1;
		}

		if (Public.difference(yP[source], y) < Math.abs(mP[source]))
			yP[source] = y;
		else
			yP[source] += mP[source];

		for (int i = source + 1; i < resolution; i++) {
			if (yP[i - 1] > yP[i] && mP[i] < 3) {
				mP[i] += Math.min(calmness, Public.difference(yP[i], yP[i - 1] - 0.5));
			} else if (yP[i - 1] < yP[i] && mP[i] > -3) {
				mP[i] -= Math.min(calmness, Public.difference(yP[i], yP[i - 1] - 0.5));
			}

			yP[i] += mP[i];
			mP[i] *= 0.5;
		}

		for (int k = source - 1; k > 1; k--) {
			if (yP[k + 1] > yP[k] && mP[k] < 3) {
				mP[k] += Math.min(calmness, Public.difference(yP[k], yP[k + 1] - 0.5));
			} else if (yP[k + 1] < yP[k] && mP[k] > -3) {
				mP[k] -= Math.min(calmness, Public.difference(yP[k], yP[k + 1] - 0.5));
			}

			yP[k] += mP[k];
			mP[k] *= 0.5;
		}

		int[] ix = new int[resolution + 2];
		int[] iy = new int[resolution + 2];

		for (int i = 0; i < resolution + 2; i++) {
			ix[i] = (int) xP[i];
			iy[i] = (int) yP[i];
		}
		water = new Polygon(ix, iy, resolution + 2);

		sourceTimer++;
	}

	public void render(Graphics g) {
		
		if(!game.getGame().paused)
			water.translate((int) -game.xOffset(), (int) -game.yOffset());
		
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()-100));
		
		if(!game.getGame().paused) {
			Polygon water2 = water;
			water2.translate(0, -1);
			g.drawPolygon(water2);
		}
		
		g.setColor(color);
		g.fillPolygon(water);

	}

}
