package com.zandgall.arvopia.tiles;

import java.awt.Color;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;

public class Bridge extends Tile{
	
	private int index;
	
	public Bridge(int index, int id) {
		super(PublicAssets.bridge[index], id);
		this.index = index;
		if(index == 2 || index == 5 || index == 7)
			TILEHEIGHT = 8;
	}

	@Override
	public void tick(Handler game, int x, int y) {
		
	}
	
	public boolean isTop() {
		if(index == 2 || index == 5 || index == 7) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void reset() {
		
	}

	@Override
	public Color getColor() {
		if(index == 2 || index == 5 || index == 7)
			return Color.orange;
		return new Color(100, 50, 10);
	}

}
