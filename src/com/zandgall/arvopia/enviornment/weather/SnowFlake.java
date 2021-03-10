package com.zandgall.arvopia.enviornment.weather;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

class SnowFlake {

	Handler game;
	
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public static double xchange = 0.2;
	public static int change = 2;
	
	double gravity = 0;
	
	int timer = 0;
	
	double x, y, size;

	double xOff = 0;
	
	public SnowFlake(Handler game, double x, double y, double size) {
		this.game = game;
		this.x = x+game.xOffset();
		this.y = y+game.yOffset();
		this.size = size;
		
		gravity += size/3;
		gravity += Public.random(-0.5, 0.5);
		
		if(gravity<0) {gravity=0.1;}
	}
	
	private boolean checkEntity(Entity e) {
		
		if(entities.contains(e))
			return false;
		
		if(e.getX()+e.getbounds().x<=x &&  e.getX()+e.getbounds().x+e.getbounds().width>=x) {
			if(e.getY()+e.getbounds().y<=y && e.getY()+e.getbounds().y+e.getbounds().height>=y) {
				return true;
			}
		}
		return false;
	}
	
	public void tick(double wind, int speed) {
		Entity closest = game.getWorld().getEntityManager().getPlayer().getClosest(x, y);
		if(checkEntity(closest)) {
			if(closest.snowy<closest.maxSnowy) { 
				game.getWorld().getEntityManager().getPlayer().getClosest(x, y).snowy++;
				entities.add(game.getWorld().getEntityManager().getPlayer().getClosest(x, y));
			}
		}
		
		if (y - size / 2 < World.getHeight()*18 - 1 && x<(World.getWidth()+10)*18+10 && x>-10 && !(World.getTile(Public.grid(x, 18, 0), Public.grid(y, 18, 0)).isTop() || World.getTile(Public.grid(x, 18, 0), Public.grid(y, 18, 0)).isSolid())) {
			if(timer - change >= 0) {
				
				double offset = Public.debugRandom(-xchange, xchange);
				
				xOff += offset;
				
				xOff = Public.range(-change, change, xOff) + wind;
				
				timer = 0;
			}

			x += (xOff/5)*speed;
			y += ((gravity + 2 - Math.abs(xOff / 5))/5)*speed;
			
			timer+=(1/5.00)*(double) speed;
		} else {
			if(Public.identifyRange(0, World.getWidth()*Tile.TILEWIDTH, x) && Public.identifyRange(0, World.getHeight()*Tile.TILEHEIGHT, y))
				World.getTile(Public.grid(x, 18, 0), Public.grid(y, 18, 0)).updateSnowy(Public.grid(x, 18, 0), Public.grid(y, 18, 0), 1);
			
			entities.clear();
			
			y = game.yOffset();
			x = Public.random(-360+game.xOffset(), 1080+game.xOffset());
			xOff = Public.random(-change, change);
		}
	}

	public void render(Graphics g) {
		 
		int x = (int) (this.x-game.xOffset());
		int y = (int) (this.y-game.yOffset());
		
		g.setColor(new Color(200, 200, 255, 14));
		g.fillOval((int) (x-size/2)-3, (int) (y-size/2)-3, (int) size+6, (int) size+6);
		g.setColor(new Color(200, 200, 255, 14));
		g.fillOval((int) (x-size/2)-6, (int) (y-size/2)-6, (int) size+12, (int) size+12);
			
		 g.setColor(new Color(200, 200, 255, 100));
		 g.drawOval((int) (x-size/2), (int) (y-size/2), (int) size, (int) size);
		 g.setColor(new Color(200, 200, 255, 200));
		 g.fillOval((int) (x-size/2), (int) (y-size/2), (int) size, (int) size);
		 
	 }
}