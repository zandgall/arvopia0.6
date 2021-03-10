package com.zandgall.arvopia.tiles;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;

public class WoodBackTile extends Tile {
	
	public WoodBackTile(int id) {
		super(ImageLoader.loadImage("/textures/Tiles/WoodBackTile.png"), id);
	}

	@Override
	public void tick(Handler game, int x, int y) {
		
	}

	@Override
	public void init() {
		
	}

	public boolean isSolid() {
		return false;
	}
	
	@Override
	public void reset() {
	}

	@Override
	public Color getColor() {
		return new Color(255, 220, 150);
	}

}
