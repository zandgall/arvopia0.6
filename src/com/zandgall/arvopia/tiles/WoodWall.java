package com.zandgall.arvopia.tiles;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

public class WoodWall extends Tile {

	static Assets woodWall = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodWall.png"), 18, 18, "WoodWall");
	
	public WoodWall(int id, int x, int y) {
		super(woodWall.get(x,y), id);
	}

	@Override
	public void tick(Handler game, int x, int y) {
		
	}

	@Override
	public void init() {
		
	}

	public boolean isSolid() {
		return true;
	}
	
	@Override
	public void reset() {
		woodWall.reset(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");
	}

	@Override
	public Color getColor() {
		return new Color(150, 75, 0);
	}

}
