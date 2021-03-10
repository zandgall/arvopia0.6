package com.zandgall.arvopia.entity.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.statics.Shrubbery;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Bridge;
import com.zandgall.arvopia.tiles.GrassTile;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.worlds.World;

public abstract class Creature extends Entity {

	public static final int DEFAULT_HEALTH = 10, DEFAULT_CREATURE_WIDTH = 18, DEFAULT_CREATURE_HEIGHT = 18;

	public static double DEFAULT_SPEED = 1, DEFAULT_ACCELERATION = 0.01, GRAVITY = 0.1, MAX_SPEED = 2,
			DEFAULT_JUMP_FORCE = 2, DEFAULT_JUMP_CARRY = 0.05, FRICTION = 0.05;

	public double health;
	protected boolean direction;
	protected double speed, orSpeed;
	protected double acceleration;
	protected String name;

	public String getName() {
		return name;
	}

	protected int maxSpeed;

	private float xMove;

	protected float yMove;
	double xVol;

	protected float yVol;

	boolean flies;

	public int MAX_HEALTH;

	// Sounds
	protected static Sound[] grassWalk, woodWalk, snowWalk;
	protected long walkTimer;

	// Smart movement
	public boolean left, right, top, bottom, down;
	public boolean lefts, rights, tops, bottoms;
	public boolean inWater, touchingWater;

	// Pre-loaded
	static Animation foxWalk, foxSit, foxStill, butterflyForward, butterflyBackward;

	public Creature(Handler handler, double x, double y, int width, int height, boolean direction, double speed,
			double acceleration, int maxSpeed, boolean flies, boolean solid, double jumpForce, double jumpCarry,
			String name) {
		super(handler, x, y, width, height, solid, true, false, false);
		this.direction = direction;
		this.speed = speed;
		orSpeed = speed;
		this.acceleration = acceleration;
		this.maxSpeed = maxSpeed;
		this.name = name;

		this.flies = flies;

		setxMove(0);
		yMove = 0;
		xVol = 0;
		yVol = 0;
	}

	public abstract void tick();

	public Tile getTile(int x, int y) {
		return World.getTile(x, y);
	}

	public static Tile Tile(int x, int y) {
		return World.getTile(x, y);
	}

	public boolean collisionWithTile(int x, int y) {
		return World.getTile(x, y).isSolid();
	}

	public static boolean collisionTile(int x, int y) {
		return World.getTile((int) Math.floor(x / Tile.TILEWIDTH), (int) Math.floor(y / Tile.TILEHEIGHT)).isTop();
	}

	public boolean collisionWithDown(int x, int y) {
		return (World.getTile(x, y).isSolid() || World.getTile(x, y).isTop());
	}

	public void regen() {
		if ((int) (health + 0.005) > (int) health)
			speed = orSpeed;
		if (health < MAX_HEALTH)
			health += 0.005;
		if (health > MAX_HEALTH)
			health = MAX_HEALTH;

	}

	public void move() {
		
		if(inWater) {
			Creature.FRICTION=0.0001;
			Creature.GRAVITY=0.001;
			Creature.DEFAULT_JUMP_FORCE = 1;
		} else {
			Creature.FRICTION=0.05;
			Creature.GRAVITY=0.1;
			Creature.DEFAULT_JUMP_FORCE = 2;
		}
		
		if (bottoms && walkTimer >= 10 && Math.round(Math.abs(getxMove()) - speed + 0.49) > 0) {
			walkTimer = 0;
			int i = (int) Public.debugRandom(0, 1);
			if (World.getTile(Public.grid(centerX(), 18, 0), Public.grid(y + bounds.y + bounds.height + 1, 18, 0))
					.isTop()
					|| World.getTile(Public.grid(centerX(), 18, 0),
							Public.grid(y + bounds.y + bounds.height + 1, 18, 0)).isSolid())
				if (getShrub(0, 0) != this) {
					if (getShrub(0, 0).snowy > 3) {
						snowWalk[i].setVolume(
								(int) Public.range(-80, Public.Map(OptionState.fxVolume, 100, 0, 6, -40),
										Public.random(-5, 5) - (Public.dist(x, y,
												game.getGameCamera().getxOffset() + game.getWidth() / 2,
												game.getGameCamera().getyOffset() + game.getHeight() / 2) / 10)),
								false);
						if(OptionState.fxVolume==0)
							snowWalk[i].setVolume(-80, false);
						snowWalk[i].Start(0, false);
					} else {
						grassWalk[i].setVolume(
								(int) Public.range(-80, Public.Map(OptionState.fxVolume, 100, 0, 6, -40),
										Public.random(-5, 5) - (Public.dist(x, y,
												game.getGameCamera().getxOffset() + game.getWidth() / 2,
												game.getGameCamera().getyOffset() + game.getHeight() / 2) / 10)),
								false);
						if(OptionState.fxVolume==0)
							grassWalk[i].setVolume(-80, false);
						grassWalk[i].Start(0, false);
					}
				} else if (World
						.getTile(Public.grid(centerX(), 18, 0), Public.grid(y + bounds.y + bounds.height + 1, 18, 0))
						.getClass() == Bridge.class) {
					woodWalk[i]
							.setVolume(
									(int) Public.range(-80, Public.Map(OptionState.fxVolume, 100, 0, 6, -40),
											Public.random(-5, 5) - (Public.dist(x, y,
													game.getGameCamera().getxOffset() + game.getWidth() / 2,
													game.getGameCamera().getyOffset() + game.getHeight() / 2) / 10)),
									false);
					if(OptionState.fxVolume==0)
						woodWalk[i].setVolume(-80, false);
					woodWalk[i].Start(0, false);
				}
		}
		walkTimer++;

		for (Sound i : grassWalk)
			i.tick(false);
		for (Sound g : woodWalk)
			g.tick(false);
		for (Sound s : snowWalk)
			s.tick(false);

		checkCol();

		if (flies || inWater) {
			if(!flies) {
				yVol*=1-FRICTION*50;
				yMove = (float) Math.floor((yVol));
				if(xMove<0)
					xMove*=1-FRICTION;
				else xMove*=1-FRICTION/100;
			}
			moveX();
			flY();
		} else {
			moveY();
			moveX();
		}
	}

	public void moveX() {

		checkCol();

		if (getxMove() > 0) { // Right
			int tx = (int) ((x) + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH;
			if (!right) {
				x += inWater ? xMove / 2 : xMove;
				x = Math.floor(x);
			} else {
				if (checkCollision(getxMove(), 0f)) {
					x = tx * Tile.TILEWIDTH - bounds.x - bounds.width + getEntity(getxMove(), 0f).getbounds().x;
				} else if (right) {
					x = tx * Tile.TILEWIDTH - bounds.x - bounds.width - 1;
				}
				xVol = 0;
			}

		} else if (getxMove() < 0) { // Left
			int tx = (int) ((x) + getxMove() + bounds.x) / Tile.TILEWIDTH;
			if (!left) {
				x += inWater ? xMove / 2 : xMove;
				x = Math.floor(x);
			} else {
				if (checkCollision(getxMove(), 0f)) {
					x = tx * Tile.TILEWIDTH - bounds.x + getEntity(getxMove(), 0f).getbounds().x
							+ getEntity(getxMove(), 0f).getbounds().width;
				} else if (left) {
					x = tx * Tile.TILEWIDTH + Tile.TILEWIDTH - bounds.x - 1;
				}
				xVol = 0;
			}
		}
	}

	public void moveY() {
		yMove = (float) Math.floor((yVol));

		checkCol();

		int tyv = (int) ((y) + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
		if (!bottom) {
			yVol += GRAVITY;
		} else {
			if (checkCollision(0f, yMove)) {
				y = tyv * Tile.TILEHEIGHT - bounds.y - bounds.height + getEntity(0f, yMove).getbounds().y;
			} else if (down) {
				y = tyv * Tile.TILEHEIGHT - bounds.y - bounds.height;
			} else if (bottom) {
				y = tyv * Tile.TILEHEIGHT - bounds.y - bounds.height - 1;
			}
			yVol = 0;
		}

		if (!bottom) {
			y += yMove;
		} else {
			if (checkCollision(0f, yMove)) {
				y = tyv * Tile.TILEHEIGHT - bounds.y - bounds.height + getEntity(0f, yMove).getbounds().y;
			} else if (bottom) {
				y = tyv * Tile.TILEHEIGHT - bounds.y - bounds.height - 1;
			}
			yVol = 0;
		}

		if (yMove < 0) { // Up

			int ty = (int) ((y) + yMove + bounds.y) / Tile.TILEHEIGHT;
			if (!top) {
				y += yMove;
			} else {
				if (checkCollision(0f, yMove)) {
					y = ty * Tile.TILEHEIGHT - bounds.y + getEntity(0f, yMove).getbounds().y
							+ getEntity(0f, yMove).getbounds().height;
				} else if (top) {
					y = ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT - bounds.y;
				}
				yVol = (float) DEFAULT_JUMP_FORCE;
			}
		}

	}

	public void flY() {

		checkCol();

		if (yMove > 0) { // Down

			int ty = (int) ((y) + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT;
			if (!bottom) {
				y += inWater ? yMove / 2 : yMove;
			} else {
				y = ty * Tile.TILEHEIGHT - bounds.y - bounds.height - 1;
				yVol = 0;
			}

		} else if (yMove < 0) { // Up
			int ty = (int) ((y) + yMove + bounds.y) / Tile.TILEHEIGHT;
			if (!top) {
				y += inWater ? yMove / 2 : yMove;
			} else {
				y = ty * Tile.TILEHEIGHT + Tile.TILEHEIGHT - bounds.y;
				yVol = 0;
			}
		}
	}

	public abstract void checkCol();

	public void reset() {
	}

	public void damage(double d) {
		speed = orSpeed * 1.5;
		health -= d;
		if (health < 1) {
			health = 0;
			game.getWorld().kill(this);
		} else {
			game.log("Damaged: " + this.health + " left for " + this);
		}
	}

	public void setHealth(int health) {
		if (health > MAX_HEALTH) {
			this.health = MAX_HEALTH;
		} else {
			this.health = health;
		}
	}

	public void addHealth(int amount) {
		if (health + amount > MAX_HEALTH) {
			health = MAX_HEALTH;
		} else {
			health += amount;
		}
	}

	public static void init() {
		foxWalk = new Animation(250, new BufferedImage[] { PublicAssets.fox[0], PublicAssets.fox[1] }, "Walk", "Fox");
		foxSit = new Animation(250, new BufferedImage[] { PublicAssets.fox[2], PublicAssets.fox[3] }, "Sit", "Fox");
		foxStill = new Animation(250, new BufferedImage[] { PublicAssets.fox[6], PublicAssets.fox[7] }, "Still", "Fox");
		butterflyForward = new Animation(100, new BufferedImage[] { PublicAssets.butterfly[0],
				PublicAssets.butterfly[1], PublicAssets.butterfly[2], PublicAssets.butterfly[3] }, "Forward",
				"Butterfly");
		butterflyBackward = new Animation(100, new BufferedImage[] { PublicAssets.butterfly[4],
				PublicAssets.butterfly[5], PublicAssets.butterfly[6], PublicAssets.butterfly[7] }, "Backward",
				"Butterfly");

		grassWalk = new Sound[] { new Sound("Sounds/GrassWalk1.wav"), new Sound("Sounds/GrassWalk2.wav") };
		snowWalk = new Sound[] { new Sound("Sounds/SnowWalk1.wav"), new Sound("Sounds/SnowWalk2.wav") };
		woodWalk = new Sound[] { new Sound("Sounds/WoodWalk1.wav"), new Sound("Sounds/WoodWalk2.wav") };
	}

	public void showHealthBar(Graphics g) {
		int Y = (int) (y + bounds.y - game.getGameCamera().getyOffset());

		int width = Math.min(Math.max(5 * MAX_HEALTH, 20), 50);

		int X = (int) (x + bounds.x - game.getGameCamera().getxOffset() - width / 2 + 5);

		g.setColor(Color.red);
		g.fillRect(X, Y - 30, (int) (width), 10);
		g.setColor(Color.green);
		g.fillRect(X, Y - 30, (int) (width * (int) health) / MAX_HEALTH, 10);
		g.setColor(Color.black);
		g.drawRect(X, Y - 30, width, 10);
		g.drawString("" + (int) health, X + 2, Y - 20);
		g.drawString(name, X + 2, Y - 10);
	}
	
	public boolean tileOffEmp(int xoff, int yoff, int width, int height) {
		boolean is = true;
		
		for(int h=xoff; h<width; h++) {
			for(int v=yoff; v<height;v++) {
				int tx = (int) ((x+bounds.x+bounds.width/2)/Tile.TILEWIDTH)+h;
				int ty = (int) ((y+bounds.y+bounds.height)/Tile.TILEHEIGHT)+v;
				
				if(collisionWithDown(tx, ty)) {
					is = false;
				}
			}
		}
		
		return is;
	}

	public float getxMove() {
		return xMove;
	}

	public void setxMove(float xMove) {
		this.xMove = xMove;
	}
}