package com.zandgall.arvopia.water;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.tiles.Tile;

public class WaterManager {
	
	ArrayList<Water> water;
	Handler game;
	
	public WaterManager(Handler game) {
		water = new ArrayList<Water>();
		this.game = game;
	}
	
	public Water newWater(int x, int y, int width, int height) {
		
		Water newater = new Water(game, x*Tile.TILEWIDTH-1, y*Tile.TILEHEIGHT+10, width*Tile.TILEWIDTH+3, height * Tile.TILEHEIGHT-9, width*4, new Color(10, 100, 200, 200)); 
		
		water.add(newater);
		
		game.log("Water "+newater + " added at ("+x+", "+y+"), with a size of ("+width+", "+ height+")");
		
		return newater;
	}
	
	public Water getClosest(int x, int y) {
		for(Water w: water)
			if(w.box().contains(x, y))
				return w;
		return null;
	}
	
	public void tickCol(EntityManager entity) {
		for(Water w: water) {
			for(Entity e: entity.getEntitiesTouching(w.box().x, w.box().y, w.box().width, w.box().height))
				w.collision((int) (e.getX()+e.getWidth()/2), (int) e.getY()+e.getbounds().y, e.getbounds().width, e.getbounds().height, 0.5, true);
		}
	}
	
	public ArrayList<Water> getWater() {
		return water;
	}

	public void setWater(ArrayList<Water> water) {
		this.water = water;
	}

	public Handler getGame() {
		return game;
	}

	public void setGame(Handler game) {
		this.game = game;
	}

	public void tick() {
		for(Water w:water)
			w.tick();
	}
	
	public void Render(Graphics g, boolean box) {
		for(Water w:water) {
			w.render(g);
			if(box)
				g.drawRect((int) (w.box().x-game.xOffset()), (int) (w.box().y-game.yOffset()), w.box().width, w.box().height);
		}
	}
	
	
	
}
