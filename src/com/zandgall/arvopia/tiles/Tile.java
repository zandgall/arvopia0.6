package com.zandgall.arvopia.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;

public abstract class Tile {
	
	
	
	
	// TILE STATES

	public static Tile[] tiles = new Tile[256];
	public static Tile n0 = new EmptyTile(0); // null
	
	public static Tile g1 = new GrassTile(1, 0, 0); // Grass
	public static Tile g2 = new GrassTile(2, 1, 0);
	public static Tile g3 = new GrassTile(3, 2, 0);
	public static Tile g4 = new GrassTile(4, 0, 1);
	public static Tile g5 = new GrassTile(5, 1, 1);
	public static Tile g6 = new GrassTile(6, 2, 1);
	public static Tile g7 = new GrassTile(7, 0, 2);
	public static Tile g8 = new GrassTile(8, 1, 2);
	public static Tile g9 = new GrassTile(9, 2, 2);
	public static Tile g10 = new GrassTile(10, 0, 3);
	public static Tile g11 = new GrassTile(11, 1, 3);
	public static Tile g12 = new GrassTile(12, 2, 3);
	public static Tile g13 = new GrassTile(13, 3, 0);
	public static Tile g14 = new GrassTile(14, 3, 1);
	public static Tile g15 = new GrassTile(15, 3, 2);
	public static Tile g16 = new GrassTile(16, 4, 0);
	public static Tile g17 = new GrassTile(17, 3, 3);
	public static Tile g18 = new GrassTile(18, 4, 3);
	public static Tile g19 = new GrassTile(19, 4, 1);
	public static Tile g20 = new GrassTile(20, 4, 2);
	public static Tile g21 = new GrassTile(21, 5, 0);
	public static Tile g22 = new GrassTile(22, 5, 1);
	public static Tile g23 = new GrassTile(23, 5, 2);
	public static Tile g24 = new GrassTile(24, 5, 3);
	public static Tile g25 = new GrassTile(25, 6, 0);
	public static Tile g26 = new GrassTile(26, 6, 1);
	public static Tile g27 = new GrassTile(27, 6, 2);
	public static Tile g28 = new GrassTile(28, 6, 3);
	
	public static Tile b0 = new Bridge(2,29);
	public static Tile b1 = new Bridge(1,30);
	public static Tile b2 = new Bridge(0,31);
	public static Tile b3 = new Bridge(3,32);
	public static Tile b4 = new Bridge(4,33);
	public static Tile b5 = new Bridge(5,34);
	public static Tile b6 = new Bridge(6,35);
	public static Tile b7 = new Bridge(7,36);

	public static Tile f0 = new WoodFloor(37,0,0);
	public static Tile f1 = new WoodFloor(38,1,0);
	public static Tile f2 = new WoodFloor(39,2,0);
	public static Tile f3 = new WoodFloor(40,0,1);
	public static Tile f4 = new WoodFloor(41,1,1);
	public static Tile f5 = new WoodFloor(42,2,1);
	public static Tile f6 = new WoodFloor(43,0,2);
	public static Tile f7 = new WoodFloor(44,1,2);
	public static Tile f8 = new WoodFloor(45,2,2);
	public static Tile f9 = new WoodFloor(46,0,3);
	public static Tile f10 = new WoodFloor(47,1,3);
	public static Tile f11 = new WoodFloor(48,2,3);
	public static Tile f12 = new WoodFloor(49,3,0);
	public static Tile f13 = new WoodFloor(50,3,1);
	public static Tile f14 = new WoodFloor(51,3,2);
	public static Tile f15 = new WoodFloor(52,3,3);
	
	public static Tile w0 = new WoodWall(53,0,0);
	public static Tile w1 = new WoodWall(54,1,0);
	public static Tile w2 = new WoodWall(55,2,0);
	public static Tile w3 = new WoodWall(56,0,1);
	public static Tile w4 = new WoodWall(57,1,1);
	public static Tile w5 = new WoodWall(58,2,1);
	public static Tile w6 = new WoodWall(59,0,2);
	public static Tile w7 = new WoodWall(60,1,2);
	public static Tile w8 = new WoodWall(61,2,2);
	public static Tile w9 = new WoodWall(62,0,3);
	public static Tile w10 = new WoodWall(63,1,3);
	public static Tile w11 = new WoodWall(64,2,3);
	public static Tile w12 = new WoodWall(65,3,0);
	public static Tile w13 = new WoodWall(66,3,1);
	public static Tile w14 = new WoodWall(67,3,2);
	public static Tile w15 = new WoodWall(68,3,3);
	
	public static Tile w16 = new WoodBackTile(69);
	
	// TILE SUPERCLASS
	
	public static int TILEWIDTH = 18, TILEHEIGHT = 18;
	public static final int DEFAULT_WIDTH = 18, DEFAULT_HEIGHT = 18; 
	protected int x, y;
	
	protected static int[][] snowy; 

	protected final int id;
	
	private BufferedImage texture;

	public Tile(BufferedImage texture, int id) {
		this.id = id;

		this.texture = texture;
		
		tiles[id] = this;
	}

	public static void set(int width, int height) {
		snowy = new int[width][height];
	}
	
	public abstract void tick(Handler game, int x, int y);
	
	public abstract void init();
	
	public abstract void reset();

	public void render(Graphics g, int x, int y, int gridx, int gridy) {
		g.drawImage(texture, x, y, TILEWIDTH, TILEHEIGHT, null);
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
	
	public void setSnowy(int x, int y, int snowy) {
		Tile.snowy[x][y] = snowy;
	}
	
	public void updateSnowy(int x, int y, int snowy) {
		Tile.snowy[x][y]+=snowy;
	}
	
	public int snowyness(int x, int y) {
		return snowy[x][y];
	}
	
	public boolean isTop() {
		return false;
	}

	public int getId() {
		return id;
	}
	
	public abstract Color getColor();

	public Image getImage() {
		return null;
	}
	
	public Image getSnowy() {
		return null;
	}
	
}
