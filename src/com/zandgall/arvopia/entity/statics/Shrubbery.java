package com.zandgall.arvopia.entity.statics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.tools.Tool;
import com.zandgall.arvopia.utils.Public;

public class Shrubbery extends StaticEntity {

	public BufferedImage image;
	public BufferedImage snowi;
	public BufferedImage[] snow;
	
	public int type;
	int widthflip = 1;
	
	public Shrubbery(Handler handler, double x, double y, int type) {
		super(handler, x, y, 18, 18, false, 0, Tool.tools.SCYTHE);
		
		maxSnowy=10;
		
		if(Math.random()<0.5) {
			widthflip = -1;
		}
		
		layer = Public.random(-0.5, -1);
		
		image = PublicAssets.shrubbery[type];
		
		if(type ==0) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18);
		}
		if(type ==1) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18);
		}
		if(type ==2) {
			snowi = PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18);
		}
		
		if(type == 0||type==1||type==2) {
			snow = new BufferedImage[10];
			snow[0] = snowi;
			for(int i = 1; i<10; i++) {
				snow[i] = Tran.combine(snowi, snow[i-1]);
			}
		}
		
		this.type = type;
		
	}

	@Override
	public void render(Graphics g) {
		
		if(variety && type != 0 && type != 2)
			g.drawImage(Tran.flip(image, widthflip, 1), (int) (x-game.xOffset()), (int) (y-game.yOffset()), null);
		else g.drawImage(image, (int) (x-game.xOffset()), (int) (y-game.yOffset()), null);
		
		if(snowy>0 && (type == 0||type==1||type==2)) {
			if(variety && type != 0 && type != 2)
				g.drawImage(Tran.flip(snow[Math.min(snowy-1, 9)], widthflip, 1), (int) (x-game.xOffset()), (int) (y-game.yOffset()), null);
			else g.drawImage(snow[Math.min(snowy-1, 9)], (int) (x-game.xOffset()), (int) (y-game.yOffset()), null);
		}
	}

}
