package com.zandgall.arvopia.entity.creatures.npcs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.tools.Umbrella;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.LoaderException;

public class Lia extends NPC {
	
	BufferedImage texture;

	Assets set;
	
	Animation jump, still, walk, crouch;
	
	int widthFlip;
	
	long aitimer;
	
	boolean start = true;
	
	boolean l = false, r = false, u = false, d = false;
	
	public Lia(Handler handler, double x, double y) {
		super(handler, x, y, 1, "Lia", new String[] { "Welcome to Arvopia " + Reporter.user + "! (RCLICK)",
				"Please walk around and enjoy yourself!", "Hey, while you're at it,", "could you get me some wood?",
				"10 logs will do!", "Thanks so much!", "You see, that's how quests work", "Have fun exploring!" });

		if (LoaderException.readFile("C:\\Arvopia\\03.arv").contains(Quest.getWoodForLia.name))
			if (LoaderException.readFile("C:\\Arvopia\\04.arv").contains(Quest.getWoodForLia.name))
				resetSpeech(new String[] { "Welcome back to Arvopia " + Reporter.user + "!" });
			else
				resetSpeech(new String[] { "Welcome back to Arvopia " + Reporter.user + "!",
						"Have you gotten around to getting me that wood?", "10 logs will do!", "Thanks so much!",
						"You see, that's how quests work", "Have fun exploring!" });
		
		useSpeech(true);
		
		texture = ImageLoader.loadImage("/textures/NPCs/" + name + "/" + name + ".png");

		set = new Assets(texture, 36, 54, "Player");

		layer = 0.01;

		MAX_HEALTH = 20;
		health = 20;

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		// Initiate animations
		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Lia");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Lia");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Lia");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Lia");
	}

	@Override
	public void tick() {
		
		if(start) {
			y = (game.getWorld().getLowest(x)-4)*Tile.TILEHEIGHT;
			start = false;
		}
		
		still.tick();
		walk.tick();
		crouch.tick();
		
		if(!useSpeech) {
			
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
		
		} else {
			
			l=false;
			r=false;
			setxMove(0);
			
			if (game.getEntityManager().getPlayer().getX() > x)
				widthFlip = 1;
			else
				widthFlip = -1;
		
		}
			
		if (dead) {
			Achievement.award(Achievement.disrespectful);
			setHealth(20);
			dead = false;
			y = (game.getWorld().getLowest(x)-4)*Tile.TILEHEIGHT;
		}

		if (done) {
			done = false;
			setSpeech(speechuse + 1);
		}
		
		if (game.getEntityManager().getPlayer().getCurrentTool() != null
				&& game.getEntityManager().getPlayer().getCurrentTool().getClass() == Umbrella.class && game.getEnviornment().precipitation
				&& game.getEnviornment().getTemp() >= 32 && game.getEntityManager().getPlayer().getX() >= x - 54
				&& game.getEntityManager().getPlayer().getX() <= x + width + 54)
			Achievement.award(Achievement.shieldethfairmaiden);

		if (use.speech == "10 logs will do!") {
			Quest.begin(Quest.getWoodForLia);
			use.done = false;
			if (game.getMouse().isRight()) {
				if (game.getEntityManager().getPlayer().items[1] >= 10) {
					done = true;
					Quest.finish(Quest.getWoodForLia);
					Achievement.award(Achievement.firstquest);
					game.getEntityManager().getPlayer().items[1] -= 10;
				}
			}
		}
		if (use.speech == "END")
			useSpeech(false);
		
		
	}
	
	public void aiMove() {

		if (right && r) {
			
			int tx = (int) ((x+getxMove()+bounds.x+bounds.width)/Tile.TILEWIDTH);
			
			if (!collisionWithTile(tx, (int) (y + bounds.y - 36) / Tile.TILEHEIGHT)) {
				u = true;
			} else {
				r = false;
				l = true;
				u = false;
			}
		} else if (left && l) {
			int tx = (int) ((x+getxMove()+bounds.x)/Tile.TILEWIDTH);
			
			if (!collisionWithTile(tx, (int) (y + bounds.y - 36) / Tile.TILEHEIGHT)) {
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

	@Override
	public void render(Graphics g) {
		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);

		if (useSpeech && game.getEntityManager().getPlayer().closestNPC==this) {
			use.render(g, game);
		}
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
