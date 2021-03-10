package com.zandgall.arvopia.tiles;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.utils.Public;

public class GrassTile extends Tile {

	private static Assets grassTileset = new Assets(ImageLoader.loadImage("/textures/Tiles/DirtTileset.png"), 18, 18,
			"GrassTile");
	private static BufferedImage snowSet = ImageLoader.loadImage("/textures/Tiles/SnowyDirtOverlay.png");
	
	BufferedImage grass, snow;
	BufferedImage[] snowOverlay = new BufferedImage[6];
	
	ArrayList<Integer> offset, timeTakes, timer;
	ArrayList<Integer> xpos, ypos;
	Map<Integer, Map<Integer, Integer>> posFinder = new HashMap<Integer, Map<Integer, Integer>>();
	
	int gx, gy;
	
	public GrassTile(int id, int x, int y) {
		super(grassTileset.get(x, y), id);
		
		gx=x;
		gy=y;
		
		grass = grassTileset.get(x, y);
		snow = snowSet.getSubimage(x*18, y*18, 18, 18);
		
		offset = new ArrayList<Integer>();
		timeTakes = new ArrayList<Integer>();
		timer = new ArrayList<Integer>();
		xpos = new ArrayList<Integer>();
		ypos = new ArrayList<Integer>();
		
		snowOverlay[0]=snow;
		for(int i =1; i<6; i++) {
			snowOverlay[i]=Tran.combine(snow, snowOverlay[i-1]);
		}
	}
	
	public int gridX() {
		return gx;
	}

	public int gridY() {
		return gy;
	}
	
	public boolean isSolid() {
		if(id == 19 || id == 20 || id == 23)
			return false;
		return true;
	}

	public void init() {
		
	}
	
	Handler game;
	
	@Override
	public void tick(Handler game, int x, int y) {
		
		this.game = game;
		if(!posFinder.containsKey(x) || !posFinder.get(x).containsKey(y)) {
			
			if(posFinder.containsKey(x)) {
				if(!posFinder.get(x).containsKey(y)) {
					posFinder.get(x).put(y, xpos.size());
				}
			} else {
				posFinder.put(x, new HashMap<Integer, Integer>());
				posFinder.get(x).put(y, xpos.size());
			}
			
			xpos.add(x);
			ypos.add(y);
			offset.add((int) Public.random(0, 10));
			timeTakes.add((int) Public.random(0, 20));
			timer.add(0);
		}
		
		int off = posFinder.get(x).get(y);
		
		if(game.getWorld().getEnviornment().getTemp()-32>offset.get(off)) {
			if(timer.get(off)>=timeTakes.get(off)) {
				snowy[x][y]--;
				timer.set(off, 0);
			} else {
				timer.set(off, timer.get(off)+1);
			}
		} else {
			timer.set(off, 0);
		}
	}
	
	@Override
	public boolean tickable() {
		return true;
	}
	@Override
	public void render(Graphics g, int x, int y, int gridx, int gridy) {
		
		g.drawImage(grass, x, y, null);	
		
		if(snowy[gridx][gridy] > 0) {
			g.drawImage(snowOverlay[(int) Public.range(0, 5, snowy[gridx][gridy]-1)], x, y, null);
		}
	}
	
	public Image getImage() {
		BufferedImage out = new BufferedImage(18, 18, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics g = out.getGraphics();
		
		g.drawImage(grass, 0, 0, null);
		
		if(snowy[0][0] > 0) {
			g.drawImage(snowOverlay[(int) Public.range(0, 5, snowy[0][0]-1)], 0, 0, null);
		}
		
		return out;
	}
	
	public Image getSnowy() {
		return snowOverlay[0];
	}

	@Override
	public void reset() {
		grassTileset.reset(ImageLoader.loadImage("/textures/DirtTileset.png"), 18, 18,
				"GrassTile");
	}

	@Override
	public Color getColor() {
		if(id == 1 || id == 2 || id == 3 || id == 10 || id == 11 || id == 12 || id == 13 || id == 16)
			return Color.green;
		else return new Color(50, 25, 0);
	}
}
