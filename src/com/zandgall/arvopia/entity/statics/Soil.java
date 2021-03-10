package com.zandgall.arvopia.entity.statics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.items.tools.Tool;
import com.zandgall.arvopia.tiles.Tile;

public class Soil extends StaticEntity {
	
	public BufferedImage texture;
	
	public Soil(Handler game, int x, int y, int id) {
		super(game, x*Tile.TILEWIDTH, y*Tile.TILEHEIGHT, 18, 18, false, 5, Tool.tools.SHOVEL);
		texture = ImageLoader.loadImage("/textures/Statics/Soil.png").getSubimage(id*18, 0, 18, 18);
	}
	
	public void tick() {
		
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(texture, (int)(x-game.xOffset()), (int)(y-game.yOffset()), null);
	}
	
}
