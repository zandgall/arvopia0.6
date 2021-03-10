package com.zandgall.arvopia.entity.statics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.tools.Tool;
import com.zandgall.arvopia.utils.Public;

public class House extends StaticEntity{
	
	public int type;
	private int orType;
	int widthflip = 1;
	public boolean isStone = false;
	
	public House(Handler handler, double x, double y,int type) {
		super(handler, x, y, 108, 162, false, type*5, Tool.tools.PICK);
		this.type = type;
		
		if(Math.random()<0.5) {
			widthflip = -1;
		}
		
		if(type == 0) {
			layer = Public.random(8, 9);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if(type == 1) {
			layer = Public.random(8, 9);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		} else if(type == 2) {
			layer = Public.random(8, 9);
			bounds.x = 0;
			bounds.y = 0;
			bounds.width = 126;
			bounds.height = 108;
		}
	}

	public int getType() {
		return type;
	}
	
	public int getOrType() {
		return orType;
	}

	public void tick() {
		
	}
	
	@Override
	public void render(Graphics g) {
		if(variety)
			if(type==2)
				g.drawImage(Tran.flip(ImageLoader.loadImage("/textures/FrizzysHouse.png"), widthflip, 1), (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
			else g.drawImage(Tran.flip(ImageLoader.loadImage("/textures/"+(isStone ? "Stone" : "")+"House"+(type!=0 ? "2":"")+".png"), widthflip, 1), (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
		else if(type==2) g.drawImage(ImageLoader.loadImage("/textures/FrizzysHouse.png"), (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
		else g.drawImage(ImageLoader.loadImage("/textures/"+(isStone ? "Stone" : "")+"House"+(type!=0 ? "2":"")+".png"), (int) (x - game.getGameCamera().getxOffset()), (int) (y - game.getGameCamera().getyOffset()), null);
	}
	
	public boolean mapable() {
		return true;
	}
	
	public Color mapColor() {
		return new Color(150, 100, 0);
	}
	
	public Point mapSize() {
		return new Point(18, 18);
	}
	
}
