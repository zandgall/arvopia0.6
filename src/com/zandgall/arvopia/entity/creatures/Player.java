package com.zandgall.arvopia.entity.creatures;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.Entity;
import com.zandgall.arvopia.entity.creatures.npcs.*;
import com.zandgall.arvopia.entity.statics.StaticEntity;
import com.zandgall.arvopia.gfx.Animation;
import com.zandgall.arvopia.gfx.Assets;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.guis.ForgeGui;
import com.zandgall.arvopia.guis.Inventory;
import com.zandgall.arvopia.guis.Menu;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.items.tools.*;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.state.TitleState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.water.Water;

public class Player extends Creature {

	// Items
	// metal, wood, petals, honey, foxFur, butterflyWings, fineMetal, furCloth,
	// charcoal
	public int[] items = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	private Tool currentTool;
	public int selectedTool = 0;
	public ArrayList<Tool> tools;
	public int[] toolbar = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	public String has = "";

	// Fall Damage
	private double lastHeight;

	// Attacking
	private boolean attackReady;

	private Rectangle attack;
	private boolean attacking, primed;
	private int attackDelay, delayRange;
	private double damage;

	// Timing
	private long timer;

	//
	private long attackTimer;

	// Inventory
	public Inventory inventory;
	public ForgeGui crafting;
	public Menu menu;

	public boolean viewInventory, viewCrafting;
	public boolean viewMenu;

	// Create animations
	private Animation jump;
	private Animation still;
	private Animation crouch;
	private Animation walk;

	private Animation punch;
	private Animation stab;
	private Animation smash;

	private Animation hold;
	private BufferedImage airKick;

	private Sound swoosh;
	private Sound fire;

	private Assets player;

	private boolean jumping = false;

	int renderCount = 0;

	int widthFlip = 1;

	String appearance = "Male";

	private MouseManager mouse;
	private int mouseX, mouseY;
	@SuppressWarnings("unused")
	private boolean leftMouse, fullLeftMouse, rightMouse;

	public int lives;

	Entity closest;
	public NPC closestNPC;

	public double breath = 20;

	public Player(Handler handler, double x, double y, boolean direction, double speed, int lives) {
		super(handler, x, y, DEFAULT_CREATURE_WIDTH, 47, direction, speed, DEFAULT_ACCELERATION, (int) MAX_SPEED, false,
				false, DEFAULT_JUMP_FORCE, DEFAULT_JUMP_CARRY, Reporter.user);

		swoosh = new Sound("Sounds/Swing.wav");
		fire = new Sound("Sounds/Fire.wav");

		mouse = handler.getMouse();

		attack = new Rectangle((int) x, (int) y, 72, 54);

		health = 20;
		MAX_HEALTH = 20;

		layer = 0;

		attackReady = false;
		attacking = false;
		attackDelay = 15;
		delayRange = 15;
		damage = 1;

		timer = 0;
		attackTimer = 0;

		this.lives = lives;

		tools = new ArrayList<Tool>();

		inventory = new Inventory(handler);
		menu = new Menu(handler);
		crafting = new ForgeGui(handler);

		bounds.x = 12;
		bounds.y = 8;
		bounds.width = 10;
		bounds.height = 45;

		player = new Assets(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/Player.png"), 36, 54,
				"Player");

		// Initiate animations
		jump = new Animation(1000, new BufferedImage[] { player.get(0, 1) }, "Jump", "Player");
		still = new Animation(750, new BufferedImage[] { player.get(0, 0), player.get(1, 0) }, "Still", "Player");
		walk = new Animation(250, new BufferedImage[] { player.get(1, 1), player.get(3, 1) }, "Walk", "Player");
		crouch = new Animation(750, new BufferedImage[] { player.get(2, 0), player.get(3, 0) }, "Crouch", "Player");

		player = new Assets(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/PlayerPunch.png"), 36,
				54, "Player Punching");
		punch = new Animation(150, new BufferedImage[] { player.get(1, 0), player.get(2, 0), player.get(0, 0) },
				"Punch", "Player");
		airKick = ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/PlayerAirKick.png");

		player = new Assets(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/PlayerHolding.png"),
				36, 54, "Player Holding");
		hold = new Animation(750, new BufferedImage[] { player.get(0, 0), player.get(1, 0) }, "Hold", "Player");

		player = new Assets(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/PlayerStab.png"), 36,
				54, "Player Stabbing");
		stab = new Animation(150, new BufferedImage[] { player.get(1, 0), player.get(2, 0), player.get(3, 0) }, "Stab",
				"Player");
		player = new Assets(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/PlayerSmash.png"), 36,
				54, "Player Stabbing");
		smash = new Animation(150,
				new BufferedImage[] { player.get(0, 0), player.get(1, 0), player.get(2, 0), player.get(3, 0) }, "Smash",
				"Player");

	}

	public void tick() {
		crafting.tick();
		inventory.tick();
		if (viewMenu) {
			menu.tick();
		}

		getInput();

		if (!game.getGame().paused) {

			fallDamage();

			mouseX = mouse.getMouseX();
			mouseY = mouse.getMouseY();
			fullLeftMouse = mouse.fullLeft;
			leftMouse = mouse.isLeft();
			rightMouse = mouse.isRight();

			if (!mouse.isIn() && game.options().pauseOnExit.on) {
				viewMenu = true;
				game.getGame().pause();
			}

			toolHandling();
			toolSoundHandling();
			waterHandling();

			if (health <= 0)
				game.getWorld().kill(this);

			// Animation ticks
			jump.tick();
			still.tick();
			walk.tick();
			crouch.tick();

			punch.tick();
			hold.tick();
			stab.tick();
			smash.tick();

			if (currentTool != null && currentTool.smashOrStab())
				currentTool.setFrame(smash.frameInt);

			game.getWorld().outOfBounds(this);
			move();

			checkMouse();

			game.getKeyManager().typed = false;

			if (bottoms && jumping) {
				jumping = false;
			}

			attackHandling();

			timer++;
			attackTimer++;
		}
	}

	private void toolHandling() {
		for (int i = 0; i < 10; i++) {
			if (game.getKeyManager().num[i]) {
				selectedTool = i;
			}
		}

		if (selectedTool < toolbar.length && toolbar[selectedTool] < tools.size() && toolbar[selectedTool] >= 0) {
			currentTool = tools.get(toolbar[selectedTool]);
			attackDelay = currentTool.getDelay();
			delayRange = currentTool.getRange();
			damage = currentTool.getDamage();
		} else {
			currentTool = null;
			attackDelay = 15;
			delayRange = 15;
			damage = 1;
		}

		if (currentTool != null)
			currentTool.tick();

	}

	private void toolSoundHandling() {
		swoosh.tick(false);

		swoosh.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
		if (OptionState.fxVolume == 0)
			swoosh.setVolume(-80, false);

		if (getFrame() == stab.getArray()[2] || getFrame() == smash.getArray()[2]) {
			swoosh.Start(0, false);
		}

		fire.tick(false);
		fire.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
		if (OptionState.fxVolume == 0)
			fire.setVolume(-80, false);

		if (currentTool != null && currentTool.hasLight()) {
			currentTool.getLight().turnOff();
			currentTool.getLight().setX(-200);
			currentTool.getLight().setY(-200);

			if (fire.hasEnded())
				fire.Start(-1, false);
		} else
			fire.Stop(false);
	}

	private void waterHandling() {
		if (inWater)
			if (breath > 0)
				breath -= 0.02;
			else if (breath <= 0)
				health -= 0.1;
		if (!inWater && breath < 20)
			breath += 0.1;
		else if (breath >= 20)
			breath = 20;
	}

	private void fallDamage() {
		if (bottoms || inWater) {
			if (y - lastHeight > (inWater ? 360 : 180)) {
				damage((int) Math.floor((y - lastHeight) / 180));
			}

			lastHeight = y;
		} else {
			if (y < lastHeight)
				lastHeight = y;
		}
	}

	private void attackHandling() {
		// If it's attacking, and the left mouse button was let go of
		if (!attacking) {
			attack.x = (int) x;
			attack.y = (int) y;
		}

		if (!fullLeftMouse)
			attacking = false;

		if (timer >= 1000000)
			timer = 0;
		if (attackTimer >= 1000000)
			attackTimer = 0;
		if (attackTimer == attackDelay) {
			attackReady = true;
		} else if (attackTimer < attackDelay || attackTimer > attackDelay + delayRange) {
			attackReady = false;
			if (attackTimer > attackDelay + delayRange)
				attackTimer = 0;
		}

	}

	private void getInput() {
		setxMove(0);
		yMove = 0;

		if (viewInventory) {
			if (game.getKeyManager().invtry || game.getKeyManager().esc) {
				viewInventory = false;
			}
		} else if (viewMenu) {
			if (game.getKeyManager().esc) {
				viewMenu = false;
				game.getGame().unPause();
			}
		} else if (viewCrafting) {
			if (game.getKeyManager().crft || game.getKeyManager().esc) {
				viewCrafting = false;
			}
		} else {

			if (game.getKeyManager().invtry) {
				viewInventory = true;
			}

			if (game.getKeyManager().crft) {
				viewCrafting = true;
			}

			if (game.getKeyManager().esc) {
				viewMenu = true;
				game.getGame().pause();
				game.getKeyManager().typed = false;
			}

			if (!game.getKeyManager().down || inWater) {
				if (xVol > 0)
					setxMove((float) (getxMove() + (speed + xVol)));
				if (xVol < 0)
					setxMove((float) (getxMove() + (-speed + xVol)));
			}

			if (game.getKeyManager().left) {
				if (!game.getKeyManager().down || inWater) {
					if (xVol < maxSpeed)
						if (xVol > 0)
							xVol -= FRICTION * 2;
					xVol -= acceleration;
				}
				widthFlip = -1;
			} else if (game.getKeyManager().right) {
				if (!game.getKeyManager().down || inWater) {
					if (xVol < maxSpeed)
						if (xVol < 0)
							xVol += FRICTION * 2;
					xVol += acceleration;
				}
				widthFlip = 1;
			} else {
				if (inWater) {
					xVol *= 0.1;
				} else if (!game.getKeyManager().down) {
					float txv = (float) xVol;
					if (xVol < 0)
						xVol += acceleration + FRICTION;
					else if (xVol > 0)
						xVol -= acceleration + FRICTION;
					if ((txv > 0 && xVol < 0) || (txv < 0 && xVol > 0)) {
						xVol = 0;
					}
				}
			}

			if (game.getKeyManager().b) {
				game.log("Marked X: " + Math.round(x) + ", Y: "
						+ Math.round(y + bounds.y + bounds.height - Tile.TILEHEIGHT) + " Tile: ("
						+ Math.round(x / Tile.TILEWIDTH) + ", "
						+ Math.round((y + bounds.y + bounds.getHeight()) / Tile.TILEHEIGHT - 1) + ")"
						+ " Tile full X Y: (" + Math.round(x / Tile.TILEWIDTH) * Tile.TILEWIDTH + ", "
						+ Math.round((y + bounds.y + bounds.getHeight()) / Tile.TILEHEIGHT - 1) * Tile.TILEHEIGHT
						+ ")");
				game.log("X and Y offset: " + game.getGameCamera().getxOffset() + ", "
						+ game.getGameCamera().getyOffset());
			}

			if (game.getKeyManager().up) {
				if (bottoms && !jumping) {
					yVol = (float) -DEFAULT_JUMP_FORCE;
					jumping = true;
				} else if (touchingWater) {

					yVol--;
					yVol = (float) Math.max(yVol, -speed);

				}
				if (yVol < 0) {
					yVol -= DEFAULT_JUMP_CARRY;
				}
			}

			if (game.getKeyManager().down) {
				if (inWater) {

					yVol++;
					yVol = (float) Math.min(yVol, speed * 2);

				} else {
					if (xVol > 0 && bottoms) {
						xVol = xVol * 0.75;
						setxMove((float) (xVol + speed));
						if (xVol < 0.001)
							xVol = 0;
					} else if (xVol > 0) {
						setxMove((float) (xVol + speed));
					}

					if (xVol < 0 && bottoms) {
						xVol = xVol * 0.75;
						setxMove((float) (xVol - speed));
						if (xVol > -0.001)
							xVol = 0;
					} else if (xVol < 0) {
						setxMove((float) (xVol - speed));
					}

					bounds.y = 37;
					bounds.height = 16;
				}
			} else {
				bounds.y = 7;
				bounds.height = 46;
			}

		}

	}

	private void checkMouse() {

		boolean first = (punch.getFrame() == punch.getArray()[2] || stab.getFrame() == stab.getArray()[2]
				|| smash.getFrame() == smash.getArray()[3]);

		if (getClosest(mouseX + game.xOffset(), mouseY + game.yOffset()) != null
				&& closest != getClosest(mouseX + game.xOffset(), mouseY + game.yOffset())) {
			closest = getClosest(mouseX + game.xOffset(), mouseY + game.yOffset());
		}

		if (getClosestNPC(mouseX + game.xOffset(), mouseY + game.yOffset()) != null
				&& getClosestNPC(mouseX + game.xOffset(), mouseY + game.yOffset()) != closestNPC) {
			closestNPC = getClosestNPC(mouseX + game.xOffset(), mouseY + game.yOffset());
		}

		if (fullLeftMouse && !viewInventory) {

			double cx = mouseX + game.xOffset();
			double cy = mouseY + game.yOffset();

			if (closest != null) {
				boolean a = (cx > closest.getX() - closest.getWidth() / 2
						&& cx < closest.getX() + closest.getbounds().x + closest.getbounds().width * 1.5);
				boolean b = (cy > closest.getY() - closest.getHeight() / 2
						&& cy < closest.getY() + closest.getbounds().y + closest.getbounds().height * 1.5);

				if (a && b && !closest.creature) {

					if (closest.staticEntity) {

						StaticEntity e = (StaticEntity) closest;

						if (e.health <= 0) {

							game.getWorld().kill(closest);

							closest = null;

						} else {

							if (currentTool != null && e.weakness == currentTool.Type())
								e.health -= 5;
							else
								e.health--;

						}

					} else {

						game.log("Entity:" + closest.staticEntity);

						game.getWorld().kill(closest);

						closest = null;

					}
				}
			}
		}
		if (fullLeftMouse && attackReady) {
			punch.setFrame(0);
			attacking = true;
			// Attack
			if (widthFlip == 1) { // Set bounds in correct direction
				attack.x = (int) (x);
			} else {
				attack.x = (int) (x - 72 + bounds.x + bounds.width);
			}
			attack.y = (int) y;
			// Check if it hits anything

			ArrayList<Creature> c = getInRadius(x + bounds.getCenterX(), y + bounds.getCenterY(), 100);

			for (Creature e : c) {
				if (attackReady && first) {
					attackReady = false;
					Creature d = (Creature) e;
					d.damage(damage);
					game.log("Damaged: " + d.health);
					if (d.dead)
						if (d.getClass() == Fox.class) {
							items[4] += (int) Public.random(1, 3);
						} else if (d.getClass() == Butterfly.class) {
							items[5] += 2;
						} else if (d.getClass() == Bee.class) {
							items[3]++;
						}
				}
			}
		} else if (fullLeftMouse && !attacking && !attackReady) {
			primed = true;
			attacking = false;
			punch.setFrame(0);
			stab.setFrame(0);
			smash.setFrame(0);
		} else {
			primed = false;
		}
	}

	public void render(Graphics g) {

		if (currentTool != null && currentTool.front())
			g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) Public.xO(x), (int) Public.yO(y), null);

		if (currentTool != null) {

			currentTool.custom2(widthFlip);

			if (getFrame() != stab.getFrame() && getFrame() != smash.getFrame()) {
				g.drawImage(Tran.flip(currentTool.texture(), widthFlip, 1),
						(int) (x - game.getGameCamera().getxOffset()) + (getToolxoffset()),
						(int) (y - game.getGameCamera().getyOffset()) + getToolyoffset(), null);
				currentTool.custom1((int) Public.xO(x + getToolxoffset() + currentTool.texture().getWidth()),
						(int) Public.yO(y));
			} else {
				currentTool.custom1((int) Public.xO(x + getToolxoffset() + currentTool.getFrame().getWidth()),
						(int) Public.yO(y));
			}
		}

		if (currentTool == null || !currentTool.front())
			g.drawImage(Tran.flip(getFrame(), widthFlip, 1), (int) Public.xO(x), (int) Public.yO(y), null);

		if (currentTool != null)
			if (getFrame() == stab.getFrame() || getFrame() == smash.getFrame())
				g.drawImage(Tran.flip(currentTool.getFrame(), widthFlip, 1), (int) Public.xO(x) + (getToolxoffset()),
						(int) Public.yO(y) + getToolyoffset(), null);

		if (closest != null) {
			if (closest.creature) {
				Creature c = (Creature) closest;
				c.showHealthBar(g);
			} else {
				closest.showBox(g);
			}
		}

		if (health < MAX_HEALTH) {
			showHealthBar(g);
		}

		renderCount++;
	}

	public int getToolxoffset() {
		if (getFrame() == airKick || getFrame() == jump.getFrame())
			if (widthFlip == 1)
				return 33 - currentTool.getXOffset();
			else
				return 3 - currentTool.getXOffset();
		if (getFrame() == crouch.getFrame())
			if (widthFlip == 1)
				return 28 - currentTool.getXOffset();
			else
				return 8 - currentTool.getXOffset();
		if (getFrame() == walk.getFrame())
			if (walk.getFrame() == walk.getArray()[0])
				if (widthFlip == 1)
					return 33 - currentTool.getXOffset();
				else
					return 3 - currentTool.getXOffset();
			else if (walk.getFrame() == walk.getArray()[1])
				if (widthFlip == 1)
					return 26 - currentTool.getXOffset();
				else
					return 10;
			else if (widthFlip == 1)
				return 25 - currentTool.getXOffset();
			else
				return 14 - currentTool.getXOffset();
		if (getFrame() == hold.getFrame())
			if (widthFlip == 1)
				return 33 - currentTool.getXOffset();
			else
				return 3 - currentTool.getXOffset();
		if (getFrame() == stab.getFrame())
			if (stab.getFrame() == stab.getArray()[0])
				if (widthFlip == 1)
					return 16;
				else
					return 20 - currentTool.getFrame().getWidth();
			else if (stab.getFrame() == stab.getArray()[1])
				if (widthFlip == 1)
					return 13;
				else
					return 23 - currentTool.getFrame().getWidth();
			else if (widthFlip == 1)
				return 33;
			else
				return 3 - currentTool.getFrame().getWidth();
		if (getFrame() == smash.getFrame())
			if (smash.getFrame() == smash.getArray()[0])
				if (widthFlip == 1)
					return 15 - currentTool.getXOffset();
				else
					return 32 - currentTool.getXOffset() - currentTool.getFrame().getWidth();
			else if (smash.getFrame() == smash.getArray()[1])
				if (widthFlip == 1)
					return 28;
				else
					return 8 - currentTool.getFrame().getWidth();
			else if (smash.getFrame() == smash.getArray()[2])
				if (widthFlip == 1)
					return 32;
				else
					return 4 - currentTool.getFrame().getWidth();
			else if (widthFlip == 1)
				return 12;
			else
				return 24 - currentTool.getFrame().getWidth();
		return 0;
	}

	public int getToolyoffset() {
		if (getFrame() == airKick || getFrame() == jump.getFrame())
			return 40 - currentTool.texture().getHeight();

		if (getFrame() == crouch.getFrame())
			return 50 - currentTool.texture().getHeight();

		if (getFrame() == walk.getFrame())
			if (walk.getFrame() == walk.getArray()[0])
				return 40 - currentTool.texture().getHeight();

			else if (walk.getFrame() == walk.getArray()[1])
				return 42 - currentTool.texture().getHeight();

			else
				return 25 - currentTool.texture().getHeight();

		if (getFrame() == hold.getFrame())
			if (hold.getFrame() == hold.getArray()[0])
				return 40 - currentTool.texture().getHeight();
			else
				return 41 - currentTool.texture().getHeight();

		if (getFrame() == stab.getFrame())
			if (stab.getFrame() == stab.getArray()[0])
				return 40 - currentTool.getYOffset();

			else if (stab.getFrame() == stab.getArray()[1])
				return 42 - currentTool.getYOffset();
			else
				return 33 - currentTool.getYOffset();

		if (getFrame() == smash.getFrame())
			if (smash.getFrame() == smash.getArray()[0])
				return 8 - currentTool.getFrame().getHeight();
			else if (smash.getFrame() == smash.getArray()[1])
				return 13 - currentTool.getFrame().getHeight();
			else if (smash.getFrame() == smash.getArray()[2])
				return 35 - currentTool.getYOffset();
			else
				return 37;

		return 33 - currentTool.texture().getHeight() / 2;
	}

	public void renScreens(Graphics g) {

		game.getGame().get2D().setTransform(game.getGame().getDefaultTransform());

		if (viewInventory) {
			inventory.render(g);
		}
		if (viewCrafting) {
			crafting.render(g);
		}
		if (viewMenu) {
			menu.render(g);
		}
	}

	private BufferedImage getFrame() {

		checkCol();

		if (game.getKeyManager().down) {
			return crouch.getFrame();
		} else if (!bottoms || jumping) {
			if (attacking && punch.getFrame() == punch.getArray()[2] && attackReady) {
				return airKick;
			}
			return jump.getFrame();
		} else if (Math.round(Math.abs(getxMove()) - speed + 0.49) > 0) {
			return walk.getFrame();
		} else if (attacking || primed) {
			if (currentTool != null)
				if (currentTool.smashOrStab()) {
					return smash.getFrame();
				} else {
					return stab.getFrame();
				}
			return punch.getFrame();
		} else {
			if (currentTool != null)
				return hold.getFrame();

			if (tops)
				return crouch.getFrame();
			return still.getFrame();
		}

	}

	public void reset() {
		player.reset(ImageLoader.loadImage("/textures/Player/" + TitleState.appearance + "/Player.png"), 36, 54,
				"Player");
	}

	public int[] getItems() {
		return items;
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

	public Tool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(Tool currentTool) {
		this.currentTool = currentTool;
	}

}
