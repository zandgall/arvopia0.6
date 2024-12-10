package com.zandgall.arvopia.items.tools;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;

public class Sword extends Tool{
	
	private BufferedImage texture, stab;
	
	public Sword(Handler game) {
		super(game, "Sword", false, 3, 15, 15);
		
		texture = PublicAssets.sword;
		stab = PublicAssets.swordStab;
	}

	@Override
	public void tick() {
	}

	public void custom1(int x, int y) {
	}
	
	public BufferedImage texture() {
		return texture;
	}

	@Override
	public BufferedImage getFrame() {
		return stab;
	}

	@Override
	public void render(Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public int getYOffset() {
		return 8;
	}
	
	public int getXOffset() {
		return texture.getWidth()/2;
	}

	@Override
	public boolean smashOrStab() {
		return false;
	}

	@Override
	public void setFrame(int frameInt) {
		
	}

	@Override
	public tools Type() {
		return tools.NONE;
	}

	@Override
	public void custom2(int i) {
		
	}

	
}
