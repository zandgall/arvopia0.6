package com.zandgall.arvopia.items.tools;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.SpriteSheet;

public class Scythe extends Tool {

	BufferedImage texture;
	Animation smash;
	
	private int xOff = 3;
	
	public Scythe(Handler game) {
		super(game, "Scythe", true, 6, 15, 40);
		texture = super.getImage("Scythe", "");
		SpriteSheet s = new SpriteSheet(super.getImage("Scythe", "Smash"));
		smash = new Animation(1000, new BufferedImage[] {s.get(0, 0, 13, 36), 
				s.get(14, 0, 30, 36), 
				s.get(0, 36, 36, 13),
				s.get(0, 49, 36, 30)}, "Slice", "Scythe");
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(Graphics g, int x, int y) {
		
	}

	@Override
	public void custom1(int x, int y) {
		
	}
	
	public BufferedImage texture() {
		return texture;
	}
	
	@Override
	public BufferedImage getFrame() {
		return smash.getFrame();
	}

	@Override
	public int getYOffset() {
		return 8;
	}
	
	public int getXOffset() {
		return xOff;
	}

	@Override
	public boolean smashOrStab() {
		return true;
	}

	@Override
	public void setFrame(int frameInt) {
		smash.setFrame(frameInt);
	}

	@Override
	public tools Type() {
		return Tool.tools.SCYTHE;
	}

	@Override
	public void custom2(int i) {
		if(i<0)
			xOff = 10;
		else xOff = 3;
	}

}
