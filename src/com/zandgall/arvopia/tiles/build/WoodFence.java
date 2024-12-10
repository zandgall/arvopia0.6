package com.zandgall.arvopia.tiles.build;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;

public class WoodFence extends Building {

	public WoodFence(Handler game, int x, int y) {
		super(ImageLoader.loadImage("/textures/Tiles/WoodFenceTileset.png"), game, x, y);
	}

	@Override
	public void tick(Handler game) {

	}

	@Override
	public void init() {

	}

	@Override
	public void reset() {

	}

	@Override
	public Color getColor() {
		return new Color(100, 50, 0);
	}

	// @Override
	// public Point getType(ArrayList<Building> types) {
	//
	// boolean left = false, right = false, top = false, bottom = false,
	// bottomsbottom = true;
	//
	// for(Building b: types) {
	// if(b.getX()==x-1&&b.getY()==y)
	// left=true;
	// else if(b.getX()==x+1&&b.getY()==y)
	// right=true;
	//
	// if(b.getY()==y-1&&b.getX()==x)
	// top=true;
	// else if(b.getY()==y+1&&b.getX()==x)
	// bottom=true;
	//
	// if(b.getY()==y+2)
	// bottomsbottom=false;
	// }
	//
	// if(!bottom)
	// bottomsbottom=false;
	//
	// Point out;
	//
	//
	//
	// if(left) {
	// if(right) {
	// if(top) {
	// if(bottom) {
	// out = new Point(1,1);
	// } else {
	// out = new Point(1,2);
	// }
	// } else {
	// out = new Point(1,0);
	// }
	// } else {
	// if(top) {
	// if(bottom) {
	// out = new Point(2,1);
	// } else {
	// out = new Point(2,3);
	// }
	// } else {
	// out = new Point(2,1);
	// }
	// }
	// } else {
	// if(right) {
	// if(top) {
	// if(bottom) {
	// out = new Point(0,1);
	// } else {
	// out = new Point(0,3);
	// }
	// } else {
	// out = new Point(0,0);
	// }
	// } else {
	// if(top) {
	// if(bottom) {
	// out = new Point(1,1);
	// } else {
	// out = new Point(1,2);
	// }
	// } else {
	// if(bottom) {
	// out = new Point(1,0);
	// } else {
	// out = new Point(1,1);
	// }
	// }
	// }
	// }
	//
	// if(bottomsbottom)
	// out.y=2;
	//
	// return out;
	//
	// }

	public Point getType(ArrayList<Building> types) {

		boolean left = false, right = false, top = false, bottom = false;

		for (Building b : types) {
			if (b.getX() == x - 1 && b.getY() == y)
				left = true;
			else if (b.getX() == x + 1 && b.getY() == y)
				right = true;

			if (b.getY() == y - 1 && b.getX() == x)
				top = true;
			else if (b.getY() == y + 1 && b.getX() == x)
				bottom = true;
		}
		
		int nx = 1, ny = 1;
		
		if(!bottom)
			ny=2;
		if(!top)
			ny=0;
		if(!left)
			nx=0;
		if(!right)
			nx=2;
		
		return new Point(nx, ny);
	}

}
