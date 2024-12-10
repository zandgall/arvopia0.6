package com.zandgall.arvopia.entity.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.water.Water;

public class NPCs extends Creature {

	int widthFlip = -1;

	BufferedImage texture;

	Assets set;

	Animation jump, still, walk, crouch;

	boolean jumping;

	Speech[] speech;
	Speech use;
	public int speechuse;
	boolean useSpeech = false;

	long timer = 0;
	public boolean done = false;

	public NPCs(Handler handler, double x, double y, double speed, String name, String[] speeches) {
		super(handler, x, y, DEFAULT_CREATURE_WIDTH, 47, false, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false,
				false, DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, name);

		speech = new Speech[speeches.length];
		for (int i = 0; i < speeches.length; i++) {
			speech[i] = new Speech(x, y - 25, speeches[i]);
		}
		use = speech[0];

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
		jump = new Animation(1000, new BufferedImage[] { set.get(0, 1) }, "Jump", "Player");
		still = new Animation(750, new BufferedImage[] { set.get(0, 0), set.get(1, 0) }, "Still", "Player");
		walk = new Animation(250, new BufferedImage[] { set.get(1, 1), set.get(3, 1) }, "Walk", "Player");
		crouch = new Animation(750, new BufferedImage[] { set.get(2, 0), set.get(3, 0) }, "Crouch", "Player");

	}

	@Override
	public void tick() {
		still.tick();
		walk.tick();
		crouch.tick();
		
		
		if (useSpeech && use.done) {
			if (game.getMouse().isRight()) {
				done = true;
			}
		}
		
		if (useSpeech) {
			use.tick();
		}

		double prex = x;
		double prey = y;

		move();

		if (game.getEntityManager().getPlayer().getX() > x)
			widthFlip = 1;
		else
			widthFlip = -1;

		if (prey != y || prex != x)
			for (int i = 0; i < speech.length; i++) {
				boolean b = (use == speech[i]);
				speech[i] = new Speech(x, y - 25, speech[i].speech);
				if (b)
					setSpeech(i);
			}
	}

	public void useSpeech(boolean tf) {
		useSpeech = tf;
	}

	public Speech getSpeech() {
		return use;
	}
	
	public void setSpeech(int i) {
		if(i>=speech.length)
			use = new Speech(x, y-25, "END");
		else use = speech[i];
		speechuse = i;
	}

	@Override
	public void checkCol() {

		down = false;
		left = false;
		right = false;
		top = false;
		bottom = false;
		lefts = false;
		rights = false;
		tops = false;
		bottoms = false;
		inWater = false;
		touchingWater = false;

		for (Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().contains(getCollision(0, 0))) {
				inWater = true;
			}
			if (w.box().intersects(getCollision(0, 0)))
				touchingWater = true;
		}

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithTile((int) ((x + bounds.x + bounds.width - 3) / Tile.TILEWIDTH), ty)
				|| checkCollision(0f, yMove)) {
			bottom = true;
		} else if (collisionWithDown((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithDown((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)) {
			if (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {

				down = true;
			}

			if (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1 && yMove >= 0) {
				bottoms = true;
				bottom = true;
			}
		}
		ty = (int) ((y + yMove + bounds.y + bounds.height + 2) / Tile.TILEHEIGHT);
		if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
				|| checkCollision(0f, yMove + 1)
				|| ((collisionWithDown((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
						|| collisionWithDown((int) ((x + bounds.x + bounds.width + 2) / Tile.TILEWIDTH), ty))
						&& y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1 && !jumping)) {
			bottoms = true;
		}

		ty = (int) ((y + yMove + bounds.y) / Tile.TILEHEIGHT);
		if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
				|| checkCollision(0f, yMove)) {
			top = true;
		}
		ty = (int) ((y + yMove + bounds.y - 2) / Tile.TILEHEIGHT);
		if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
				|| checkCollision(0f, yMove - 1)) {
			tops = true;
		}

		int tx = (int) ((x + getxMove() + bounds.x + bounds.width) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() + 1, 0f)) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() + 1, 0f)) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove(), 0f)) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height / 4) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height * 0.75) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() - 1, 0f)) {
			lefts = true;
		}
	}

	@Override
	public void render(Graphics g) {

		g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) (x - game.xOffset()), (int) (y - game.yOffset()), null);

		if (useSpeech) {
			use.render(g, game);
		}

	}

	public BufferedImage getFrame() {
		if (bottoms)
			return still.getFrame();
		else
			return jump.getFrame();
	}

	public class Movement {

		public Movement() {

		}

	}

	public class Speech {

		public String speech;

		public String use = "";

		double i = 0;

		double x, y;

		public boolean done = false;

		public Speech(double x, double y, String speech) {
			this.speech = speech;
			this.x = x;
			this.y = y;
		}

		public void tick() {
			i += 0.3;
			use = speech.substring(0, Math.min((int) i, speech.length()));

			if (Math.min((int) i, speech.length()) == speech.length() && speech!="END") {
				done = true;
			}
		}

		public void render(Graphics g, Handler game) {

			double nx = x * 1.1111;
			double ny = y * 1.1111;

			g.setColor(Color.white);
			g.fillRoundRect((int) (nx - game.xOffset() * 1.1111 - use.length() * 3 - 10),
					(int) (ny - game.yOffset() * 1.1111 - 10), use.length() * 6 + 20, 20, 20, 20);
			g.setColor(Color.black);
			g.drawRoundRect((int) (nx - game.xOffset() * 1.1111 - use.length() * 3 - 10),
					(int) (ny - game.yOffset() * 1.1111 - 10), use.length() * 6 + 20, 20, 20, 20);
			g.drawString(use, (int) (nx - game.xOffset() * 1.1111 - use.length() * 3),
					(int) (ny - game.yOffset() * 1.1111) + 5);
		}

	}

}
