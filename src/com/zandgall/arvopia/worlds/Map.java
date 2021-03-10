package com.zandgall.arvopia.worlds;

import java.awt.Color;
import java.awt.Graphics;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;

public class Map {
	
	Handler game;
	
	public Map(Handler game) {
		this.game = game;
	}
	
	
	public void render(Graphics g) {
		int xOff = (int) (game.getWidth()/2-World.getWidth()*1.5);
		int yOff = (int) (game.getHeight()/2-World.getHeight()*1.5);
		
		for(int x = 0; x<World.getWidth(); x++) {
			for(int y = 0; y<World.getHeight(); y++) {
				g.setColor(World.getTile(x, y).getColor());
				g.fillRect(xOff+x*3, yOff+y*3, 3, 3);
			}
		}
		int px;
		int py;
		
		for(Entity e: game.getEntityManager().getEntities()) {
			if(e.mapable()) {
				px = (int) (e.getX());
				py = (int) (e.getY());
				g.setColor(e.mapColor());
				g.fillRect(xOff+(px/Tile.TILEWIDTH)*3+3, yOff+(py/Tile.TILEHEIGHT)*3, e.mapSize().x, e.mapSize().y);
			}
		}
		
		
		px = (int) (game.getEntityManager().getPlayer().getX());
		py = (int) (game.getEntityManager().getPlayer().getY());
		
		g.setColor(Color.red);
		g.fillRect(xOff+(px/Tile.TILEWIDTH)*3+3, yOff+(py/Tile.TILEHEIGHT)*3, 3, 9);
		
		g.setColor(Color.black);
		g.drawRect(xOff, yOff, World.getWidth()*3, World.getHeight()*3);
	}
}


















