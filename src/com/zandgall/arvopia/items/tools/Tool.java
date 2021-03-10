package com.zandgall.arvopia.items.tools;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.enviornment.Light;
import com.zandgall.arvopia.gfx.ImageLoader;

public abstract class Tool {
	
	protected Handler game;
	
	double damage;
	int delay, range;
	
	public static enum tools {
		AXE, PICK, SCYTHE, TILLER, SHOVEL, NONE;
	}
	
	
	BufferedImage thumbnail;
	
	public Tool(Handler game, BufferedImage thumbnail, boolean weapon, double damage, int delay, int range) {	
		this.game = game;
		this.damage = damage;
		this.delay = delay;
		this.range = range;
		this.thumbnail = thumbnail;
	}
	
	public int getDelay() {
		return delay;
	}

	public int getRange() {
		return range;
	}
	
	public boolean front() {
		return false;
	}

	public Tool(Handler game, String name, boolean weapon, double damage, int delay, int range) {	
		this.game = game;
		this.damage = damage;
		this.damage = damage;
		this.delay = delay;
		this.range = range;
		thumbnail = getImage(name, "Thumbnail");
	} 
	
	public BufferedImage getThumbnail() {
		return thumbnail;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public abstract tools Type();
	
	public abstract void tick();
	
	public abstract void render(Graphics g, int x, int y);
	
	public abstract void custom1(int x, int y);

	public abstract void custom2(int i);
	
	public BufferedImage texture() {
		return null;
	}
	
	public abstract BufferedImage getFrame();

	public abstract int getYOffset();
	
	public abstract int getXOffset();
	
	public boolean hasLight() {
		return false;
	}
	
	public Light getLight() {
		return null;
	}
	
	public abstract boolean smashOrStab();

	public abstract void setFrame(int frameInt);
	
	public BufferedImage getImage(String name, String variation) {
		return ImageLoader.loadImage("/textures/Inventory/Tools/"+name+"/"+name+variation+".png");
	}
	
}
