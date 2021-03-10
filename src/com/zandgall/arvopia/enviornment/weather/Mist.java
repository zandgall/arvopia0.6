package com.zandgall.arvopia.enviornment.weather;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

public class Mist {
	
	private Handler game;
	
	
	public int amount;
	
	public ArrayList<MistChunk> mist;
	
	public Mist(Handler game, int maxMist) {
		this.game = game;
		
		mist = new ArrayList<MistChunk>();
		
		for(int i=0;i<maxMist;i++) {
			mist.add(new MistChunk(Public.random(0, World.getWidth()*Tile.TILEWIDTH), Public.random(0, World.getHeight()*Tile.TILEHEIGHT)));
		}
	}
	
	public void tick() {
		amount = (int) (-1*(0.000005*Math.pow(game.getEnviornment().getTime()-25000, 2)-mist.size()));
//		game.log(""+amount);
		amount = (int) Public.range(0, mist.size(), amount);
		for(int i = 0; i<amount; i++)
			mist.get(i).tick(game);
	}
	
	public void render(Graphics g) {
		for(int i = 0; i<amount; i++)
			mist.get(i).render(g);
	}
	
	
	public class MistChunk {
		
		int id;
		
		double x, y, mom;
		int width, height;
		
		BufferedImage texture;
		
		public MistChunk(double x, double y) {
			id = (int) Public.random(1, 4);
			
			
			this.x = x;
			this.y = y;
			
			width = (int) Public.random(144, 576);
			height = (int) Public.random(72, 288);
			
			texture = ImageLoader.loadImage("/textures/MistChunkTest"+id+".png");
		}
		
		public void tick(Handler game) {
			mom+=Public.debugRandom(-0.5, 0.5);
			if(mom<-2) {
				mom+=0.5;
			} else if(mom>2) {
				mom-=0.5;
			}
			
			x+=mom+game.getWind();
			
			
			if(x>World.getWidth()*Tile.TILEWIDTH)
				x=0;
			else if(x<0) {
				x=World.getWidth()*Tile.TILEWIDTH;
			}
		}
		
		public void render(Graphics g) {
			g.drawImage(texture, (int) (x-game.xOffset()), (int) (y-game.yOffset()), width, height, null);
		}
		
	}
	
}
