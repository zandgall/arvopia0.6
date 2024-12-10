package com.zandgall.arvopia.tiles.build;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;

public abstract class Building {
	public static int TILEWIDTH = 18, TILEHEIGHT = 18;
	public static final int DEFAULT_WIDTH = 18, DEFAULT_HEIGHT = 18; 
	protected int x, y;
	
	protected Handler game;
	
	protected static int[][] snowy; 
	
	private BufferedImage texture;

	public Building(BufferedImage texture, Handler game, int x, int y) {
		this.texture = texture;
		
		this.game = game;
		
		this.x = x;
		this.y = y;
	}

	public static void set(int width, int height) {
		snowy = new int[width][height];
	}
	
	public abstract void tick(Handler game);
	
	public abstract void init();
	
	public abstract void reset();

	public void render(Graphics g, ArrayList<Building> local) {
		Point p = getType(local);
		p.x*=18;
		p.y*=18;
		g.drawImage(texture.getSubimage(p.x, p.y, TILEWIDTH-1, TILEHEIGHT-1), (int) (x*TILEWIDTH-game.xOffset()), (int) (y*TILEHEIGHT-game.yOffset()), TILEWIDTH, TILEHEIGHT, null);
	}

	public boolean isSolid() {
		return false;
	}
	
	public boolean varietable() {
		return false;
	}
	
	public boolean tickable() {
		return false;
	}
	
	public int snowyness(int x, int y) {
		return snowy[x][y];
	}
	
	public boolean isTop() {
		return false;
	}
	
	public abstract Color getColor();
	
	public abstract Point getType(ArrayList<Building> types);

	public int getX() {
		return x;
	}
	
	public int getY() {		
		return y;
	}
	
}
