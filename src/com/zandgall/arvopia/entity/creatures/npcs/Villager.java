package com.zandgall.arvopia.entity.creatures.npcs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;

public class Villager extends NPC {

	int type;
	BufferedImage texture;
	Assets set;
	Animation jump, still, walk, crouch;

	int widthFlip = 1;

	long aitimer;

	boolean l = false, r = false, u = false, d = false;
	
	double bx, by;
	
	public Villager(Handler handler, double x, double y, int type) {
		super(handler, x, y, 1, "Villager " + type, new String[] { "null" });
		this.type = type;

		bx=x;
		by=y;
		
		
		useSpeech(false);

		texture = ImageLoader.loadImage("/textures/NPCs/Villagers/Varient" + type + ".png");

		set = new Assets(texture, 36, 54, "Player");

		layer = Public.debugRandom(-0.01, 0.01);

		MAX_HEALTH = 20;
		health = 20;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		// Initiate animations
		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Villager");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Villager");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Villager");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Villager");
	}

	@Override
	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();

		if (l)
			widthFlip = -1;
		if (r)
			widthFlip = 1;

		aiMove();

		if (l)
			setxMove(-1);
		else if (r)
			setxMove(1);
		else
			setxMove(0);

		move();
		
		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			x=bx;
			y=by;
		}
		
		
		
	}

	public void aiMove() {

		if (right && r) {
			
			int tx = (int) ((x+getxMove()+bounds.x+bounds.width)/Tile.TILEWIDTH);
			
			if (!collisionWithTile(tx, (int) (y + bounds.y - 18) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if (left && l) {
			int tx = (int) ((x+getxMove()+bounds.x)/Tile.TILEWIDTH);
			
			if (!collisionWithTile(tx, (int) (y + bounds.y - 18) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = true;
				l = false;
				u = false;
			}
		} else {
			u = false;
		}
		
		if (u) {
			if (bottoms && yVol > -DEFAULT_JUMP_FORCE + (GRAVITY * 2)) {
				if (!d && yVol > -DEFAULT_JUMP_FORCE / 2) {
					yVol -= DEFAULT_JUMP_FORCE;
				}
			} else if (yVol < 0) {
				yVol -= DEFAULT_JUMP_CARRY;
			}
		}
		
		if (aitimer >= 100) {
			if (Math.random() < 0.15) {
				l = true;
				r = false;
			} else if (Math.random() < 0.3) {
				r = true;
				l = false;
			} else {
				r = false;
				l = false;
			}
			aitimer = 0;
		}
		
		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT)+6;
		if (!(collisionWithDown((int) ((x + bounds.x - 20) / Tile.TILEWIDTH), ty)
				|| collisionWithDown((int) ((x + bounds.x + bounds.width - 20) / Tile.TILEWIDTH), ty)) && y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {
			r = true;
			l = false;
		} else if (!(collisionWithDown((int) ((x + bounds.x + 24) / Tile.TILEWIDTH), ty)
				|| collisionWithDown((int) ((x + bounds.x + bounds.width - 24) / Tile.TILEWIDTH), ty)) && y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {
			r = false;
			l = true;
		}

		aitimer++;
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);
	}

	public BufferedImage getFrame() {

		if (d)
			return crouch.getFrame();

		if (l || r)
			return walk.getFrame();
		if (bottoms)
			return still.getFrame();
		else
			return jump.getFrame();
	}
	
	public boolean mapable() {
		return true;
	}
	
	public Color mapColor() {
		return Color.yellow;
	}
	
	public Point mapSize() {
		return new Point(3, 9);
	}
	
}
