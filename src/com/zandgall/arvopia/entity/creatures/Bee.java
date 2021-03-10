package com.zandgall.arvopia.entity.creatures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.water.Water;

public class Bee extends Creature {

	public long prevTime = System.currentTimeMillis(), prevMoveTime = System.currentTimeMillis();
	private long timer, moveTimer;

	public Sound buzz;

	public Bee(Handler handler, double x, double y, boolean direction, long timeMillis) {
		super(handler, x, y, 6, 6, direction, Creature.DEFAULT_SPEED, Creature.DEFAULT_ACCELERATION, 5, true, false, 0,
				0, "Bee");

		prevTime = System.currentTimeMillis();
		timer = timeMillis;

		buzz = new Sound("Sounds/BeeBuzz.wav");
		buzz.setVolume(-80, false);

		health = 1;
		MAX_HEALTH = 1;

		bounds.x = -6;
		bounds.width = 18;
		bounds.y = -6;
		bounds.height = 18;

		layer = Math.random() - 5;
		timer = 100;
		moveTimer = 0;

		buzz.Start(-1, false);
	}

	public void tick() {
		if (this.dead)
			buzz.Stop(false);

		if (System.currentTimeMillis() - prevTime == timer) {
			game.getWorld().kill(this);
		} else {

			if (System.currentTimeMillis() - prevMoveTime >= moveTimer) {
				prevMoveTime = System.currentTimeMillis();

				xVol += (float) (Math.random() * speed * 2 - 1);
				yVol += (float) (Math.random() * speed * 2 - 1);

				if (Math.abs(xVol) > MAX_SPEED) {
					xVol -= xVol / 2;
				}

				if (Math.abs(yVol) > MAX_SPEED) {
					yVol -= yVol / 2;
				}
			}

			buzz.setVolume((int) Public.range(-80, Public.Map(OptionState.fxVolume, 100, 0, 6, -40),
					Public.random(-5, 5) - (Public.dist(x, y, game.getEntityManager().getPlayer().centerX(),
							game.getEntityManager().getPlayer().centerY()) / 10)),
					false);

			yMove = yVol;
			setxMove((float) (xVol + game.getWind() / 5));

			move();
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(getFrame(), (int) (x - game.getGameCamera().getxOffset()),
				(int) (y - game.getGameCamera().getyOffset()), null);

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}
	}

	private BufferedImage getFrame() {
		if (Math.round(getxMove() - speed + 0.49) > 0) {
			if (yMove > 0) {
				return PublicAssets.bee[1];
			} else if (yMove < 0) {
				return PublicAssets.bee[3];
			} else {
				return PublicAssets.bee[0];
			}
		} else if (Math.round(Math.abs(getxMove()) - speed + 0.49) > 0) {
			if (yMove > 0) {
				return PublicAssets.bee[3];
			} else if (yMove < 0) {
				return PublicAssets.bee[1];
			} else {
				return PublicAssets.bee[0];
			}
		} else if (Math.round(Math.abs(yMove) - speed + 0.49) > 0) {
			return PublicAssets.bee[2];
		} else {
			return PublicAssets.bee[0];
		}
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

		for (Water w : game.getWorld().getWaterManager().getWater()) {
			if (w.box().intersects(bounds))
				inWater = true;
		}

		int ty = (int) ((y + yMove + bounds.y + bounds.height) / Tile.TILEHEIGHT);
		if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
				|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
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
						|| collisionWithDown((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty))
						&& y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1)) {
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
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() + 1, 0f)) {
			right = true;
		}
		tx = (int) ((x + getxMove() + bounds.x + bounds.width + 2) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() + 1, 0f)) {
			rights = true;
		}

		tx = (int) ((x + getxMove() + bounds.x) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove(), 0f)) {
			left = true;
		}
		tx = (int) ((x + getxMove() + bounds.x - 2) / Tile.TILEWIDTH);
		if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
				|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
				|| checkCollision(getxMove() - 1, 0f)) {
			lefts = true;
		}
	}

}
