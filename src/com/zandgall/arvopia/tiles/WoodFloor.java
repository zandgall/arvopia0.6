package com.zandgall.arvopia.tiles;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

public class WoodFloor extends Tile {

	static Assets woodFloor = new Assets(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");
	
	public WoodFloor(int id, int x, int y) {
		super(woodFloor.get(x,y), id);
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
		woodFloor.reset(ImageLoader.loadImage("/textures/Tiles/WoodFloor.png"), 18, 18, "WoodFloor");
	}

	@Override
	public Color getColor() {
		return new Color(50, 25, 0);
	}

}
