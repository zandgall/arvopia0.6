package com.zandgall.arvopia.entity.statics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.tools.Tool;
import com.zandgall.arvopia.items.tools.Tool.tools;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.water.Water;
import com.zandgall.arvopia.worlds.World;

public class Tree extends StaticEntity {

	BufferedImage[][] tree = PublicAssets.tree;

	long[] timings = { 2000, 4000, 10000, 20000, 30000, 50000, 100000, 200000, 350000, 700000, 1000000, 2000000,
			4000000, 5000000, 7000000, 10000000 }; // How long each age lasts

	double[] chanceLeaves = { 0.05, 0.1, 0.2, 0.5, 0.5, 1, 2, 4, 5, 6, 6, 2, 0, 0, 0, 0, 0, 0.01, 0.02 };

	int[] xoff = { 16, 15, 14, 14, 12, 11, 10, 9, 5, 5, 2, 2, 2, -4, -11, -17 },
			yoff = { 140, 138, 134, 128, 125, 118, 112, 108, 101, 93, 84, 79, 69, 50, 22, 9 },
			width = { 3, 4, 9, 9, 12, 13, 17, 18, 28, 29, 34, 34, 34, 43, 60, 66 },
			height = { 4, 6, 10, 16, 19, 26, 32, 36, 43, 51, 60, 68, 77, 94, 122, 135 };

	public int age = 0;

	int growthTime;
	long lastTime = 0;

	ArrayList<Leaf> leaves;

	int widthflip = 1;

	public Tree(Handler handler, double x, double y, int age) {
		super(handler, x, y, 36, 144, false, age * 2, Tool.tools.AXE);

		maxSnowy = 2;

		layer = Public.random(-10, 10);

		leaves = new ArrayList<Leaf>();

		this.age = age;

		bounds.x = xoff[age];
		bounds.y = yoff[age];
		bounds.width = width[age];
		bounds.height = height[age];

		if (Math.random() < 0.5) {
			widthflip = -1;
		}

		growthTime = (int) Public.random(-1000, 5000);

	}

	public void tick() {
		if (game.getWorld().getEnviornment().getTotalTime() - lastTime >= timings[age] + growthTime) {

			lastTime = game.getWorld().getEnviornment().getTotalTime();
			age++;
			growthTime = (int) Public.random(-5000 - (game.getWorld().getEnviornment().getHumidity() * 10), 10000);

			if (age == 16) {
				game.getWorld().kill(this);
				return;
			}

			bounds.x = xoff[age];
			bounds.y = yoff[age];
			bounds.width = width[age];
			bounds.height = height[age];

		}

		if (age < 6 && getStage() != 0) {
			game.getWorld().kill(this);
		}

		if (Public.chance(chanceLeaves[getStage()]) && x > game.xOffset() - 50
				&& x < game.xOffset() + game.getWidth() + 50 && y > game.yOffset() - 50
				&& y < game.yOffset() + game.getHeight() + 50) {
			int s = 0;
			switch (getStage()) {
			case 1:
				s = 1;
				break;
			case 3:
				s = 1;
				break;
			case 5:
				s = 2;
				break;
			case 7:
				s = 3;
				break;
			}
			if (getStage() >= 8)
				s = 4;

			game.getWorld().getEntityManager()
					.addEntity(new Leaf(game, Public.random(x - (age > 12 ? 12 : 0), x + xoff[age] + width[age]),
							Public.random(y + yoff[age], y + yoff[age] + height[age] / 3), s), false);
		}

		// for (Leaf leaf : leaves) {
		// if (leaf.getX() > game.xOffset() - 50 && leaf.getX() < game.xOffset() +
		// game.getWidth() + 50
		// && leaf.getY() > game.yOffset() - 50 && leaf.getY() < game.yOffset() +
		// game.getHeight() + 50)
		// leaf.tick();
		// }
	}

	@Override
	public void render(Graphics g) {

		double newX = 0;

		if (age > 12)
			newX = 18;

		// for (Leaf leaf : leaves) {
		// if (leaf.getX() > game.xOffset() - 12 && leaf.getX() < game.xOffset() +
		// game.getWidth() + 12
		// && leaf.getY() > game.yOffset() - 12 && leaf.getY() < game.yOffset() +
		// game.getHeight() + 12)
		// leaf.render(g);
		// }

		// if (variety)
		// g.drawImage(Tran.flip(getFrame(), widthflip, 1, xoff[age] + width[age],
		// yoff[age] + height[age]),
		// (int) (x - game.xOffset() - newX), (int) (y - game.yOffset()), null);
		// else
		g.drawImage(getFrame(), (int) (x - game.xOffset() - newX), (int) (y - game.yOffset()), null);

	}

	private BufferedImage getFrame() {
		return tree[getStage()][age];
	}

	private int getStage() {
		double temp = game.getWorld().getEnviornment().getTemp();
		int day = game.getWorld().getEnviornment().rohundo;

		if (day < 151 && day > 59) { // "Spring"
			if (day < 65) {
				return 12;
			} else if (day < 90) {
				return 15;
			} else if (day < 110) {
				return 16;
			} else if (day < 120) {
				return 17;
			} else {
				return 18;
			}
		} else if (Public.identifyRange(151, 243, day)) {
			return 0;
		} else {
			if (Public.identifyRange(50, 60, temp))
				return 1;
			if (Public.identifyRange(45, 50, temp))
				return 3;
			if (Public.identifyRange(40, 45, temp))
				return 5;
			if (Public.identifyRange(35, 40, temp))
				return 7;
			if (Public.identifyRange(33, 35, temp))
				return 8;
			if (Public.identifyRange(32, 33, temp))
				return 9;
			if (Public.identifyRange(31, 32, temp))
				return 10;
			if (Public.identifyRange(30, 31, temp))
				return 11;
			if (temp < 32)
				return 12 + snowy;
			return 0;
		}

	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public class Leaf extends StaticEntity {

		double rotation = 270;
		double spiralOff = Public.random(0, 1);

		double[] spiralDiff = new double[] { 5, 3, 1, 3, 5, 6, 7, 6 };

		// int[] rotationFrame = new int[] { 270, 315, 0,

		int widthFlip = 1;

		int state = 1;
		// 0 = dead, 1 = falling, 2 = spiral, 3 = carry

		int stage;
		int frame = 0;
		BufferedImage[][] frames = PublicAssets.leaves;

		long timer = 0;

		long age = 0;

		// Smart movement
		public boolean left, right, top, bottom, down;
		public boolean lefts, rights, tops, bottoms;

		double xMove = 0, yMove = 0;
		double weight = Public.random(0.1, 0.5);

		public Leaf(Handler handler, double x, double y, int stage) {
			super(handler, x, y, 12, 12, false, 1, tools.NONE);
			this.stage = stage;
		}

		@Override
		public void render(Graphics g) {
			frame = (int) Public.range(0, 6, frame);
			g.drawImage(Tran.flip(frames[stage][frame], widthFlip, 1), (int) (x - game.xOffset()),
					(int) (y - game.yOffset()), null);
		}

		boolean frame2already, backforth;

		public void tick() {
			if (x > game.xOffset() - 12 && x < game.xOffset() + game.getWidth() + 12 && y > game.yOffset() - 12
					&& y < game.yOffset() + game.getHeight() + 12) {

				checkCol();

				double wind = game.getWind();

				if (state != 0 && state != 1)
					widthFlip = (wind > 0 ? 1 : -1);

				if (state == 2) {
					if (widthFlip == 1)
						rotation += spiralOff + spiralDiff[frame];
					else
						rotation -= spiralOff + spiralDiff[frame];

					if (frame == 2)
						if (frame2already)
							state = 3;
						else
							frame2already = true;
				}

				if (state == 3) {
					if (timer > 100) {
						rotation += Public.random(-5, 5);

						if (widthFlip == 1) {
							if (Public.identifyRange(-70, 70, rotation))
								if (rotation > 90)
									rotation -= 5;
								else
									rotation += 5;
						} else {
							if (Public.identifyRange(110, 250, rotation))
								if (rotation > 250)
									rotation -= 5;
								else
									rotation += 5;
						}

						timer = 0;

						if (Public.chance(0.1)) {
							state = 2;
						}
					}
					timer++;
				}

				if (state == 0) {
					frame = 2;
					rotation = 0;
					
					if(wind>0&&!right||(wind<0&&!left))
						x+=wind/10;
					
					if (!bottom) {
						state = 3;
						timer = 200;
					}
				}

				if (!Public.over(wind, game.getWorld().getEnviornment().getMaxWind(),
						game.getWorld().getEnviornment().getMaxWind(), 0.1 + weight / 2)
						|| Public.angle(rotation)[1] < weight) {
					state = 1;
				}

				if (bottom)
					state = 0;

				if (state != 0 && state != 1) {
					if ((!right && Public.angle(rotation)[0] > 0) || (!left && Public.angle(rotation)[0] < 0))
						x += Public.angle(rotation)[0] * Math.abs(wind);
					if ((!bottom && Public.angle(rotation)[1] > 0) || (!top && Public.angle(rotation)[1] < 0))
						y += Public.angle(rotation)[1] * 5 + weight;
				} else if (state == 1) {

					widthFlip = 1;

					if (Public.over(wind, game.getWorld().getEnviornment().getMaxWind(),
							game.getWorld().getEnviornment().getMaxWind(), 0.2)) {
						state = 2;
					}

					if (!bottoms) {
						if (frame == 0 && backforth) {
							yMove = Math.random() / 5;
							xMove = Math.random() / 5;
						} else if ((frame == 1 || frame == 2) && backforth) {
							yMove = Math.random() / 10;
							xMove = Math.random() / 5;
						} else if ((frame == 3 || frame == 4) && backforth) {
							yMove = -Math.random() / 10;
							xMove = Math.random() / 10;
						} else if (frame == 3 || frame == 4) {
							yMove = Math.random() / 5;
							xMove = -Math.random() / 5;
						} else if (frame == 1 || frame == 2) {
							yMove = Math.random() / 5;
							xMove = -Math.random() / 5;
						} else if (frame == 0) {
							yMove = -Math.random() / 10;
							xMove = -Math.random() / 10;
						}
						if (xMove + game.getWind() / 10 > 0) {
							if (!right) {
								x += game.getWind() / 10 + xMove * 4;
							}
						} else if (xMove + game.getWind() / 10 < 0) {
							if (!left) {
								x += game.getWind() / 10 + xMove * 4;
							}
						}

						if (right) {
							x--;
						}
						if (left) {
							x++;
						}

						yMove += 0.001;

						if (!bottom) {
							this.y += yMove * 5 + weight;
						}

						if (timer > 15) {
							if (frame == 0)
								backforth = true;
							else if (frame >= 4)
								backforth = false;

							if (backforth)
								frame++;
							else
								frame--;

							timer = 0;
						}
						timer++;

					} else {
						state = 0;
					}
				}

				if (state != 1) {
					setFrame();
				}

			}

			age++;

			if (age > 200000)

			{
				if (state != 0)
					state = 1;
				stage = 5;
			}

		}

		private void setFrame() {
			double r = rotation;
			while (r >= 360)
				r -= 360;
			while (r < 0)
				r += 360;
			if (r >= 247.5 && r < 292.5)
				frame = 0;
			else if (r >= 292.5 && r < 337.5)
				frame = 1;
			else if (r >= 337.5 || r < 22.5)
				frame = 2;
			else if (r >= 22.5 && r < 67.5)
				frame = 3;
			else if (r >= 67.5 && r < 112.5)
				frame = 4;
			else if (r >= 112.5 && r < 157.5)
				frame = 5;
			else if (r >= 157.5 && r < 202.5)
				frame = 6;
			else if (r >= 202.5 && r < 247.5)
				frame = 7;
		}

		public boolean collisionWithTile(int x, int y) {
			return World.getTile(x, y).isSolid();
		}

		public boolean collisionTile(int x, int y) {
			return World.getTile((int) Math.floor(x / Tile.TILEWIDTH), (int) Math.floor(y / Tile.TILEHEIGHT)).isTop();
		}

		public boolean collisionWithDown(int x, int y) {
			return (World.getTile(x, y).isSolid() || World.getTile(x, y).isTop());
		}

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

			int ty = (int) ((y + bounds.y + bounds.height) / Tile.TILEHEIGHT);
			if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
					|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
					|| checkCollision(0f, 0f)) {
				bottom = true;
			} else if (collisionWithDown((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
					|| collisionWithDown((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)) {
				if (y + bounds.y + bounds.height < ty * Tile.TILEHEIGHT + 4) {

					down = true;
				}

				if (y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1) {
					bottoms = true;
					bottom = true;
				}
			}
			ty = (int) ((y + bounds.y + bounds.height + 2) / Tile.TILEHEIGHT);
			if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
					|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
					|| checkCollision(0f, 1)
					|| ((collisionWithDown((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
							|| collisionWithDown((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty))
							&& y + bounds.y + bounds.height <= ty * Tile.TILEHEIGHT + 1)) {
				bottoms = true;
			}

			ty = (int) ((y + bounds.y) / Tile.TILEHEIGHT);
			if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
					|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
					|| checkCollision(0f, 0f)) {
				top = true;
			}
			ty = (int) ((y + bounds.y - 2) / Tile.TILEHEIGHT);
			if (collisionWithTile((int) ((x + bounds.x + 2) / Tile.TILEWIDTH), ty)
					|| collisionWithTile((int) ((x + bounds.x + bounds.width - 2) / Tile.TILEWIDTH), ty)
					|| checkCollision(0f, -1)) {
				tops = true;
			}

			int tx = (int) ((x + bounds.x + bounds.width) / Tile.TILEWIDTH);
			if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
					|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
					|| checkCollision(1, 0f)) {
				right = true;
			}
			tx = (int) ((x + bounds.x + bounds.width + 2) / Tile.TILEWIDTH);
			if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
					|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
					|| checkCollision(1, 0f)) {
				rights = true;
			}

			tx = (int) ((x + bounds.x) / Tile.TILEWIDTH);
			if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
					|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
					|| checkCollision(0f, 0f)) {
				left = true;
			}
			tx = (int) ((x + bounds.x - 2) / Tile.TILEWIDTH);
			if (collisionWithTile(tx, (int) (y + bounds.y + 2) / Tile.TILEHEIGHT)
					|| collisionWithTile(tx, (int) (y + bounds.y + bounds.height - 2) / Tile.TILEHEIGHT)
					|| checkCollision(-1, 0f)) {
				lefts = true;
			}
		}

	}

}
