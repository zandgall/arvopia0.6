package com.zandgall.arvopia.tiles;

import java.awt.Color;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;

public class EmptyTile extends Tile {

	public EmptyTile(int id) {
		super(ImageLoader.loadImage("/textures/Null.png"), id);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(Handler game, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Color getColor() {
		return new Color(100, 200, 255);
	}
	
}
