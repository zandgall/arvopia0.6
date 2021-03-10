package com.zandgall.arvopia.worlds;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.entity.*;
import com.zandgall.arvopia.entity.creatures.*;
import com.zandgall.arvopia.entity.moveableStatics.*;
import com.zandgall.arvopia.entity.statics.*;
import com.zandgall.arvopia.entity.creatures.npcs.*;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.items.Item;
import com.zandgall.arvopia.items.ItemManager;
import com.zandgall.arvopia.items.tools.Axe;
import com.zandgall.arvopia.items.tools.Sword;
import com.zandgall.arvopia.items.tools.Torch;
import com.zandgall.arvopia.items.tools.Umbrella;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.state.GameState;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.tiles.build.Building;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;
import com.zandgall.arvopia.water.WaterManager;

public class World {

	public int state = 0;

	// Lag detection

	public long entities, items, enviorn, water, tilet;
	public long entitiesr, itemsr, enviorr, waterr, tiler;

	private Enviornment enviornment;

	private Handler handler;

	private static int width;

	private static int height;
	private int spawnx, spawny;
	private static int[][] tiles;

	public int bee, butterfly, fox, stone0, stone1, stone2, flower0, flower1, flower2, youngTrees, midTrees, oldTrees,
			cloud0, cloud1, cloud2, cloud3, cloudY, cannibalTribes, minPerTribe, maxPerTribe;
	private int maxBee, maxButterfly, maxFox, maxStone, maxFlower, maxTrees, maxCannibalTribes;

	int rencount = 0;

	boolean waitingForCreature;

	// Respawn
	private Button respawn;
	private boolean dead;

	// Entities
	private EntityManager entityManager;

	private Entity center;
	private boolean Box = false;

	private boolean loading;
	public double percentDone = 0;

	private ArrayList<ArrayList<Integer>> heights;
	private ArrayList<ArrayList<Integer>> tops;

	// NPCs
	Lia Lia;

	// Items
	private ItemManager itemManager;

	// Water
	private WaterManager waterManager;

	// Save vs Load
	public boolean save = false;

	// Map
	Map map;
	
	// Background

	BufferedImage background;
	BufferedImage backgroundSnowOverlay;

	// Building

	ArrayList<Building> builtTiles;

	public World(Handler handler, String path, boolean isPath, boolean beginning, boolean generate) {
		this.handler = handler;
		percentDone = 0;

		map = new Map(handler);

		entityManager = new EntityManager(handler, new Player(handler, 100, 0, false, 2, 3));
		itemManager = new ItemManager(handler);
		waterManager = new WaterManager(handler);

		builtTiles = new ArrayList<Building>();

		center = entityManager.getPlayer();
		waitingForCreature = false;

		loading = true;

		respawn = new Button(handler, handler.getWidth() / 2 - 50, handler.getHeight() / 2 - 25, 100, 25,
				"Respawns the character", "Respawn");

		Creature.init();
		
		if(generate) {
			generateWorld(beginning);
		} else	
		loadWorld(path, isPath, beginning, false);

		Tile.set(width, height);

		enviornment.setupStars();

		highestTile();

		Lia = new Lia(handler, (width - 10) * 18,
				getLowest(Public.range(0, width * Tile.TILEWIDTH, width - 10 * Tile.TILEWIDTH)) * Tile.TILEHEIGHT);
		entityManager.addEntity(Lia, true);

		if (save) {

			entityManager.getPlayer().setX(spawnx);
			entityManager.getPlayer().setY(spawny);

			return;
		}

		if (beginning) {
			GameState gset = (GameState) (State.getState());
			gset.setLoadingPhase(3);
		}

		addShrubbery(10);

		addTrees(youngTrees, 0, 5);
		addTrees(midTrees, 6, 10);
		addTrees(oldTrees, 11, 15);

		addCloud(cloud0, 0);
		addCloud(cloud1, 1);
		addCloud(cloud2, 2);
		addCloud(cloud3, 3);
		addFox(fox);
		addBee(bee, 100000);
		addButterfly(butterfly, 100000);
		addStone(stone0, 0);
		addStone(stone1, 1);
		addStone(stone2, 2);
		addFlower(flower0, 2);
		addFlower(flower1, 1);
		addFlower(flower2, 0);
		entityManager.getPlayer().setX(spawnx);
		entityManager.getPlayer().setY(spawny);

		if (beginning) {
			GameState gset = (GameState) (State.getState());
			gset.setLoadingPhase(4);
			ren();
		}

		createBackground();
	}
	
	public void generateWorld(boolean beginning) {
		GameState gset;
		if (beginning) {
			gset = (GameState) (State.getState());
			gset.setLoadingPhase(1);
			State.setState(gset);
		}
		ren();

		width = (int) Public.random(520, 644);
		height = (int) Public.random(50, 100);

		stone0 = (int) Public.random(width/20, width/20);
		stone1 = (int) Public.random(width/20, width/20);
		stone2 = (int) Public.random(width/20, width/20);
		flower0 = (int) Public.random(width/20, width/20);
		flower1 = (int) Public.random(width/20, width/20);
		flower2 = (int) Public.random(width/20, width/20);
		youngTrees = (int) Public.random(width/20, width/20);
		midTrees = (int) Public.random(width/20, width/20);
		oldTrees = (int) Public.random(width/15, width/10);

		bee = (int) Public.random(width/50, width/20);
		butterfly = (int) Public.random(width/50, width/20);
		fox = (int) Public.random(width/40, width/10);
		cannibalTribes = (int) Public.random(width/100, width/50);
		minPerTribe = (int) Public.random(1, 4);
		maxPerTribe = (int) Public.random(4, 6);

		maxStone = (int) Public.random(width, width*2);
		maxFlower =  (int) Public.random(width, width*2);
		maxTrees = (int) Public.random(width, width*2);

		maxBee = (int) Public.random(width/20, width/10);
		maxButterfly = (int) Public.random(width/20, width/10);
		maxFox = (int) Public.random(width/10, width/5);
		maxCannibalTribes = (int) Public.random(width/50, width/40);

		cloud0 = (int) Public.random(width/50, width/20);
		cloud1 = (int) Public.random(width/50, width/20);
		cloud2 = (int) Public.random(width/50, width/20);
		cloud3 = (int) Public.random(width/50, width/20);
		cloudY = (int) Public.random(height/4, height/2);

		if (beginning) {
			gset = (GameState) (State.getState());
			gset.setLoadingPhase(2);
			State.setState(gset);
		}
		ren();
		
		int bw = (width * 18 + handler.getWidth());
		int bh = (height * 18 + handler.getHeight());
		double y = cloudY;
		double c = Public.debugRandom(-1, 1);

		tiles = new int[bw / 18][bh / 18];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = 0;
			}
		}

		
		 	int biome=1;
		
		for (int i = 0; i < bw / 18; i++) {

			tiles[i][(int) Math.max(0, y)] = 2;

			for (int j = (int) y + 1; j < bh / 18; j++) {

				tiles[i][(int) Math.max(0, j)] = 5;

			}

			
			if(biome==5) {
				//Mountainous 
				if(Math.abs(c)>=3) c/=2; if(y>=cloudY+10) { c--; }
				 if(y<=cloudY-10) { c++; } c += Public.debugRandom(-0.75, 0.75);
			}

			if(biome==4) {
				//Big Hills and Plains 
				if(Math.abs(c)>=3) c/=2; if(y>=cloudY+10) { c/=2; }
				if(y<=cloudY-10) { c/=2; } c += Public.debugRandom(-0.6, 0.6);
			}

			if(biome==3) {
				 //Plains and Hills 
				if(Math.abs(c)>=2) c/=2; if(y>=cloudY+7) { c/=2; c-=0.1; }
				if(y<=cloudY-7) { c/=2; c+=0.1; } c += Public.debugRandom(-0.5, 0.5);
			} 
			

			if(biome==2) {
				//Hills 
				if(Math.abs(c)>=2) c/=2; if(y>=cloudY+4 && c>0) { c/=2; c-=0.1; }
				 if(y<=cloudY-7 && c<0) { c/=2; c+=0.1; } c += Public.debugRandom(-0.5, 0.5);
			}
			
			if(biome==1) {
				// Plains
				if (Math.abs(c) >= 1.5)
					c = 1;
				if (y >= cloudY + 10 && c > 0) {
					c /= 2;
					c -= 0.2;
				}
				if (y <= cloudY - 4 && c < 0) {
					c /= 2;
					c += 0.2;
				}
	
				c = Public.range(-1.5, 1.5, c);
				c += Public.debugRandom(-0.2, 0.2);
			}
			
			y += c;
			
			if(Public.chance(1)) {
				biome = (int) Public.random(1,5);
				System.out.println("Biome: " + biome);
			}
		}

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j]=backgroundFormat(tiles, i, j);
			}
		}
		
		highestTile();
		
		spawnx=180;
		spawny=getHighest(180);

		enviornment = new Enviornment(handler, Public.random(1, 5), Public.random(1, 5),
				Public.random(50, 100));
	}
	
	public void createBackground() {

		int bw = (width * 18 + handler.getWidth());
		int bh = (height * 18 + handler.getHeight());

		background = new BufferedImage(bw, bh, BufferedImage.TYPE_4BYTE_ABGR);
		backgroundSnowOverlay = new BufferedImage(bw, bh, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics bg = background.getGraphics();
		Graphics sg = backgroundSnowOverlay.getGraphics();

		double y = cloudY;
		double c = Public.debugRandom(-1, 1);

		int[][] tiles = new int[bw / 9][bh / 9];

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j] = 0;
			}
		}

		for (int i = 0; i < bw / 9; i++) {

			tiles[i][(int) Math.max(0, y)] = 2;

			for (int j = (int) y + 1; j < bh / 9; j++) {

				tiles[i][(int) Math.max(0, j)] = 5;

			}

			/*
			 * //Mountainous if(Math.abs(c)>=3) c/=2; if(y>=cloudY+10) { c--; }
			 * if(y<=cloudY-10) { c++; } c += Public.debugRandom(-0.75, 0.75);
			 */

			/*
			 * //Big Hills and Plains if(Math.abs(c)>=3) c/=2; if(y>=cloudY+10) { c/=2; }
			 * if(y<=cloudY-10) { c/=2; } c += Public.debugRandom(-0.6, 0.6);
			 */

			/*
			 * //Plains and Hills if(Math.abs(c)>=2) c/=2; if(y>=cloudY+7) { c/=2; c-=0.1; }
			 * if(y<=cloudY-7) { c/=2; c+=0.1; } c += Public.debugRandom(-0.5, 0.5);
			 */

			
			  //Hills 
			if(Math.abs(c)>=2) c/=2; if(y>=cloudY+4 && c>0) { c/=2; c-=0.1; }
			 if(y<=cloudY-7 && c<0) { c/=2; c+=0.1; } c += Public.debugRandom(-0.5, 0.5);
			 

			/*// Plains
			if (Math.abs(c) >= 1.5)
				c = 1;
			if (y >= cloudY + 10 && c > 0) {
				c /= 2;
				c -= 0.2;
			}
			if (y <= cloudY - 4 && c < 0) {
				c /= 2;
				c += 0.2;
			}

			c = Public.range(-1.5, 1.5, c);
			c += Public.debugRandom(-0.2, 0.2);*/

			y += c;

		}

		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				tiles[i][j]=backgroundFormat(tiles, i, j);
			}
		}
		
		int bf = 0, bs = 0;
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				if(tiles[i][j]==0&&tiles[i][j+1]!=0) {
					
					if(Public.chance(maxStone/2) && bs<maxStone) {
						bg.drawImage(PublicAssets.stone[(int) Public.random(0, 2)], i*15, j*15, 15, 15, null);
						bs++;
					}
					
					if(Public.chance(maxFlower/2) && bs<maxFlower) {
						bg.drawImage(PublicAssets.flower[(int) Public.random(0, 8)], i*15, j*15, PublicAssets.flower[(int) Public.random(0, 2)].getWidth()-3, PublicAssets.flower[(int) Public.random(0, 2)].getHeight()-3, null);
						bf++;
					}
					
					if(tiles[i][j+1]==1) {
						bg.drawImage(PublicAssets.shrubbery[0], i*15, j*15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(0, 0, 18, 18), i*15, j*15, null);
					}
					else if(tiles[i][j+1]==2) {
						bg.drawImage(PublicAssets.shrubbery[1], i*15, j*15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(18, 0, 18, 18), i*15, j*15, null);
					}
					else if(tiles[i][j+1]==3) {
						bg.drawImage(PublicAssets.shrubbery[2], i*15, j*15, 15, 15, null);
						sg.drawImage(PublicAssets.snowyGrassEntity.getSubimage(36, 0, 18, 18), i*15, j*15, null);
					}
					
					
					if(Public.chance(maxStone/2) && bs<maxStone) {
						bg.drawImage(PublicAssets.stone[(int) Public.random(0, 2)], i*15, j*15, 15, 15, null);
						bs++;
					}
					
					if(Public.chance(maxFlower/2) && bs<maxFlower) {
						bg.drawImage(PublicAssets.flower[(int) Public.random(0, 8)], i*15, j*15, PublicAssets.flower[(int) Public.random(0, 2)].getWidth()-3, PublicAssets.flower[(int) Public.random(0, 2)].getHeight()-3, null);
						bf++;
					}
					
					
				}
				
				bg.drawImage(Tile.tiles[tiles[i][j]].getImage(), i * 15, j * 15, 15, 15, null);
				sg.drawImage(Tile.tiles[tiles[i][j]].getSnowy(), i * 15, j * 15, 15, 15, null);
				
			}
		}
		

		bg.dispose();
	}

	private int backgroundFormat(int[][] tiles, int i, int j) {
		if(i==0||j==0||i==tiles.length-1||j==tiles[tiles.length-1].length-1)
			return tiles[i][j];
		if(tiles[i][j]==0)
			return 0;
		
		// Top
		if (tiles[i][j - 1] == 0) {
			// Stand Alone
			if (tiles[i - 1][j] == 0 && tiles[i + 1][j] == 0)
				return 13;
			// Right Corner
			if (tiles[i + 1][j] == 0)
				return 3;
			// Left Corner
			if (tiles[i - 1][j] == 0)
				return 1;
			
			// Middle
			if (tiles[i - 1][j] != 0 && tiles[i + 1][j] != 0)
				return 2;
		}

		// Indent
			// Right Corner
			if (tiles[i - 1][j-1] == 0 && tiles[i-1][j]!=0 && (j==0 || tiles[i][j-1]!=0))
				return 18;
			// Left Corner
			if (tiles[i + 1][j-1] == 0 && tiles[i+1][j]!=0 && tiles[i][j-1]!=0)
				return 17;
			
		// Sides
			//Right
			if(tiles[i+1][j]==0 && tiles[i][j-1]!=0)
				return 6;
			//Left
			if(tiles[i-1][j]==0 && tiles[i][j-1]!=0)
				return 4;
			
		return 5;
	}

	public void finish(boolean beginning) {
		handler.logWorld("Finished loading world");
		state = 10;

		if (beginning) {
			GameState gset = (GameState) (State.getState());
			gset.setLoadingPhase(-1);
		}
	}

	public void reset() {
		entityManager.getEntities().clear();
		tiles = new int[][] { {} };
		dead = true;
		center = null;
		handler = null;
	}

	public void tick() {

		if (handler.getKeyManager().b) {
			if (Box) {
				Box = false;
			} else {
				Box = true;
			}
		}

		if (dead) {
			respawn.tick();
		}

		long pre = System.nanoTime();
		entityManager.tick();
		entities = System.nanoTime() - pre;
		pre = System.nanoTime();
		itemManager.tick();
		items = System.nanoTime() - pre;
		pre = System.nanoTime();
		enviornment.tick();
		enviorn = System.nanoTime() - pre;
		pre = System.nanoTime();
		waterManager.tick();
		waterManager.tickCol(entityManager);
		water = System.nanoTime() - pre;
		pre = System.nanoTime();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (getTile(x, y).tickable())
					getTile(x, y).tick(handler, x, y);
			}
		}
		tilet = System.nanoTime() - pre;

		if (respawn.on) {
			if (entityManager.getPlayer() != null) {
				entityManager.getPlayer().kill();
			}
			entityManager.setPlayer(new Player(handler, 100, 0, false, 2, 3));
			entityManager.getPlayer().setHealth(entityManager.getPlayer().MAX_HEALTH);
			entityManager.getPlayer().setX(spawnx);
			entityManager.getPlayer().setY(spawny);
			respawn.tick();
			center = entityManager.getPlayer();
			dead = false;
		}

		if (waitingForCreature) {
			for (Entity e2 : entityManager.getEntities()) {
				if (e2.creature) {
					center = e2;
					handler.logWorld("Centered on: " + e2.getClass());
					waitingForCreature = false;
					return;
				}
			}
		}

		if (loading) {
			loading = false;
		}
	}

	public void spawing() {
		if (enviornment.getTemp() > 32 && enviornment.getHumidity() > 2 && flower0 + flower1 + flower2 < maxFlower
				&& Math.random() < 0.001 + Public.Map(enviornment.getHumidity(), 5, 0, 0.009, 0)) {
			if (Math.random() < 1 / 3) {
				addFlower(2, 0);
				flower0 += 2;
			} else if (Math.random() < 0.5) {
				addFlower(2, 1);
				flower1 += 2;
			} else {
				addFlower(2, 2);
				flower2 += 2;
			}
		}

		if (enviornment.getTemp() > 32 && enviornment.getHumidity() > 2 && youngTrees + midTrees + oldTrees < maxTrees
				&& Math.random() < 0.01 + Public.Map(enviornment.getHumidity(), 5, 0, 0.0049, 0)) {

			youngTrees++;

			addTrees(1, 0, 0);

		}

		if (stone0 + stone1 + stone2 < maxStone && Math.random() < 0.01) {
			if (Math.random() < 1 / 3) {
				addStone(2, 0);
				stone0 += 2;
			} else if (Math.random() < 0.5) {
				addStone(2, 1);
				stone1 += 2;
			} else {
				addStone(1, 2);
				stone2++;
			}
		}

		if (fox < maxFox && Math.random() < 0.01) {
			addFox(2);
			fox += 2;
		}

		if (bee < maxBee && Math.random() < 0.01) {
			addBee(1, 10000);
			bee++;
		}

		if (butterfly < maxButterfly && Math.random() < 0.01) {
			addButterfly(1, 10000);
			butterfly++;
		}

		if (cannibalTribes < maxCannibalTribes && Math.random() < 0.0025) {
			addCannibalTribe((int) Public.random(minPerTribe, maxPerTribe), (int) Public.random(5, width - 5));
			cannibalTribes++;
		}
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void render(Graphics g, Graphics2D g2d) {

		g2d.translate(-(Game.scale - 1) * handler.getWidth() / 2, -(Game.scale - 1) * handler.getHeight() / 2);
		g2d.scale(Game.scale, Game.scale);

		handler.getGameCamera().centerOnEntity(center);
		long pre = System.nanoTime();
		enviornment.renderSunMoon(g);
		enviornment.renderStars(g);
		enviorr = System.nanoTime() - pre;

		g.drawImage(background, (int) -(handler.xOffset() / 2), (int) -(handler.yOffset() / 2), null);
		
		for(int i = 0; i<Tile.g1.snowyness(0, 0); i++) {
			g.drawImage(backgroundSnowOverlay, (int) -(handler.xOffset	() / 2), (int) -(handler.yOffset() / 2), null);
		}
		
		if (rencount == 0)
			resetGraphics();

		pre = System.nanoTime();
		int xStart = (int) (Math.max(handler.getGameCamera().getxOffset() / (Tile.TILEWIDTH), 0));
		int xEnd = (int) (Math.min(width,
				(handler.getGameCamera().getxOffset() + handler.getWidth()) / Tile.TILEWIDTH + 1));
		int yStart = (int) (Math.max(handler.getGameCamera().getyOffset() / (Tile.TILEHEIGHT), 0));
		int yEnd = (int) (Math.min(height,
				(handler.getGameCamera().getyOffset() + handler.getHeight()) / Tile.TILEHEIGHT + 1));

		for (Building b : builtTiles)
			b.render(g, builtTiles);

		for (int y = yStart; y < yEnd; y++) {
			for (int x = xStart; x < xEnd; x++) {
				getTile(x, y).render(g, (int) (x * Tile.TILEWIDTH - (handler.getGameCamera().getxOffset())),
						(int) (y * Tile.TILEHEIGHT - handler.getGameCamera().getyOffset()), x, y);
			}
		}
		tiler = System.nanoTime() - pre;

		pre = System.nanoTime();
		entityManager.render(g, Box);
		entitiesr = System.nanoTime() - pre;
		pre = System.nanoTime();
		itemManager.render(g, Box);
		itemsr = System.nanoTime() - pre;
		pre = System.nanoTime();
		waterManager.Render(g, Box);
		waterr = System.nanoTime() - pre;

		if (dead) {
			respawn.render(g);
		}

		rencount++;

		g2d.setTransform(handler.getGame().getDefaultTransform());

		pre = System.nanoTime();
		enviornment.render(g, g2d);
		enviorr += System.nanoTime() - pre;

		entityManager.getPlayer().renScreens(g);

		if (handler.getKeyManager().m)
			map.render(g);

	}

	public WaterManager getWaterManager() {
		return waterManager;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}

	public void setItemManager(ItemManager itemManager) {
		this.itemManager = itemManager;
	}

	public void addBuilt(Building b) {
		builtTiles.add(b);
	}

	private void resetGraphics() {
		for (int i = entityManager.getEntities().size() - 1; i > 0; i--) {
			Entity e = entityManager.getEntities().get(i);
			e.reset();
		}

	}

	public static Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return Tile.n0;

		Tile t = Tile.tiles[tiles[x][y]];
		if (t == null)
			return Tile.n0;

		return t;
	}

	private void ren() {
		handler.getGame().forceRender();
	}

	private void loadWorld(String path, boolean isPath, boolean beginning, boolean isSub) {

		GameState gset = null;

		if (beginning) {
			gset = (GameState) (State.getState());
			gset.setLoadingPhase(0);
			State.setState(gset);
		}

		ren();

		String file = null;
		if (isPath) {
			file = LoaderException.readFile(path);
		} else {
			try {
				file = LoaderException.streamToString(LoaderException.loadResource(path), path.length());
			} catch (IOException e) {
				e.printStackTrace();
				handler.logWorldSilent("Error loading world: " + e.getMessage());
				path = "/Worlds/DefaultWorld";
				try {
					file = LoaderException.streamToString(LoaderException.loadResource(path), path.length());
				} catch (IOException e1) {
					e1.printStackTrace();
					handler.logWorldSilent("Error loading backup world: " + e.getMessage());
					file = null;
				}
			}
		}

		String[] tokens = file.split("\\s+");

		if (tokens[0].contains("Save")) {
			save = true;
			loadSave(path);
		} else {

			if (beginning) {
				gset = (GameState) (State.getState());
				gset.setLoadingPhase(1);
				State.setState(gset);
			}
			ren();

			width = Utils.parseInt(tokens[0]);
			height = Utils.parseInt(tokens[1]);

			spawnx = Utils.parseInt(tokens[2]) * 18;
			spawny = Utils.parseInt(tokens[3]) * 18;

			stone0 = Utils.parseInt(tokens[4]);
			stone1 = Utils.parseInt(tokens[5]);
			stone2 = Utils.parseInt(tokens[6]);
			flower0 = Utils.parseInt(tokens[7]);
			flower1 = Utils.parseInt(tokens[8]);
			flower2 = Utils.parseInt(tokens[9]);
			youngTrees = Utils.parseInt(tokens[10]);
			midTrees = Utils.parseInt(tokens[11]);
			oldTrees = Utils.parseInt(tokens[12]);

			bee = Utils.parseInt(tokens[13]);
			butterfly = Utils.parseInt(tokens[14]);
			fox = Utils.parseInt(tokens[15]);
			cannibalTribes = Utils.parseInt(tokens[16]);
			minPerTribe = Utils.parseInt(tokens[17]);
			maxPerTribe = Utils.parseInt(tokens[18]);

			maxStone = Utils.parseInt(tokens[19]);
			maxFlower = Utils.parseInt(tokens[20]);
			maxTrees = Utils.parseInt(tokens[21]);

			maxBee = Utils.parseInt(tokens[22]);
			maxButterfly = Utils.parseInt(tokens[23]);
			maxFox = Utils.parseInt(tokens[24]);
			maxCannibalTribes = Utils.parseInt(tokens[25]);

			cloud0 = Utils.parseInt(tokens[29]);
			cloud1 = Utils.parseInt(tokens[30]);
			cloud2 = Utils.parseInt(tokens[31]);
			cloud3 = Utils.parseInt(tokens[32]);
			cloudY = Utils.parseInt(tokens[33]);

			if (beginning) {
				gset = (GameState) (State.getState());
				gset.setLoadingPhase(2);
				State.setState(gset);
			}
			ren();

			tiles = new int[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					tiles[x][y] = Utils.parseInt(tokens[(x + y * width) + 34]);
				}
			}
			
			highestTile();

			enviornment = new Enviornment(handler, Utils.parseDouble(tokens[26]), Utils.parseDouble(tokens[27]),
					Utils.parseDouble(tokens[28]));

			if (tokens.length <= width * height + 34) {
				state = 0;
				return;
			} else if (tokens.length > width * height + 34) {

				handler.logWorld("Adding custom entities... " + (tokens.length > width * height + 34));

				int contInt = width * height + 34;

				int size = Utils.parseInt(tokens[contInt]);

				if (beginning) {
					gset = (GameState) (State.getState());
					gset.setLoadingPhase(3);
					State.setState(gset);
				}
				ren();

				for (int i = 0; i < size; i++) {

					if (contInt >= tokens.length)
						break;

					String s = tokens[contInt + 1];

					if (s.contains("Tree")) {
						entityManager.addEntity(
								new Tree(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4])),
								false);

						contInt += 4;
					} else if (s.contains("Flower")) {
						entityManager.addEntity(new Flower(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4]),
								Utils.parseDouble(tokens[contInt + 5])), false);

						contInt += 5;
					} else if (s.contains("Stone")) {
						entityManager.addEntity(
								new Stone(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4])),
								false);

						contInt += 4;
					} else if (s.contains("Cannibal")) {
						entityManager.addEntity(
								new Cannibal(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseDouble(tokens[contInt + 4]),
										Utils.parseInt(tokens[contInt + 5]), Utils.parseBoolean(tokens[contInt + 6])),
								false);

						contInt += 6;
					} else if (s.contains("Shrubbery")) {
						entityManager.addEntity(
								new Shrubbery(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4])),
								false);

						contInt += 4;
					} else if (s.contains("Butterfly")) {
						entityManager.addEntity(new Butterfly(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3]), false, 100000), false);

						contInt += 3;
					} else if (s.contains("Bee")) {
						entityManager.addEntity(new Bee(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3]), false, 100000), false);

						contInt += 3;
					} else if (s.contains("Fox")) {
						entityManager.addEntity(new Fox(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3])), false);

						contInt += 3;
					} else if (s.contains("Cloud")) {
						entityManager.addEntity(new Cloud(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4]),
								Utils.parseDouble(tokens[contInt + 5])), false);

						contInt += 5;
					} else if (s.contains("Water")) {
						waterManager.newWater(Utils.parseInt(tokens[contInt + 2]), Utils.parseInt(tokens[contInt + 3]),
								Utils.parseInt(tokens[contInt + 4]), Utils.parseInt(tokens[contInt + 5]));

						contInt += 5;
					} else if (s.contains("House")) {
						entityManager.addEntity(
								new House(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4])),
								false);

						contInt += 4;
					} else if (s.contains("Villager")) {
						entityManager.addEntity(
								new Villager(handler, Utils.parseDouble(tokens[contInt + 2]),
										Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4])),
								false);

						contInt += 4;
					} else if (s.contains("NPC")) {
						HashMap<String, String> quests = new HashMap<String, String>();

						String[] speeches = tokens[contInt + 6].replaceAll("`", " ").split("~");

						String[] speechesvquests = tokens[contInt + 7].replaceAll("`", " ").split("~");
						String[] questmanage = tokens[contInt + 8].replaceAll("`", " ").split("~");

						for (int q = 0; q < questmanage.length; q++) {
							if (questmanage.length > 0)
								quests.put(speechesvquests[q], questmanage[q]);
						}

						entityManager.addEntity(new Template(handler, Utils.parseDouble(tokens[contInt + 2]),
								Utils.parseDouble(tokens[contInt + 3]), Utils.parseInt(tokens[contInt + 4]),
								tokens[contInt + 5].replaceAll("~", " "), speeches, quests), true);

						contInt += 8;
					} else {
						contInt++;
					}
				}
			}
		}

	}

	public void loadSave(String path) {
		String file = LoaderException.readFile(path);

		String[] tokens = file.split("\\s+");

		width = Utils.parseInt(tokens[1]);
		height = Utils.parseInt(tokens[2]);

		spawnx = (int) Utils.parseDouble(tokens[3]);
		spawny = (int) Utils.parseDouble(tokens[4]);

		stone0 = Utils.parseInt(tokens[5]);
		stone1 = Utils.parseInt(tokens[6]);
		stone2 = Utils.parseInt(tokens[7]);
		flower0 = Utils.parseInt(tokens[8]);
		flower1 = Utils.parseInt(tokens[9]);
		flower2 = Utils.parseInt(tokens[10]);
		youngTrees = Utils.parseInt(tokens[11]);
		midTrees = Utils.parseInt(tokens[12]);
		oldTrees = Utils.parseInt(tokens[13]);

		bee = Utils.parseInt(tokens[14]);
		butterfly = Utils.parseInt(tokens[15]);
		fox = Utils.parseInt(tokens[16]);
		cannibalTribes = Utils.parseInt(tokens[17]);
		minPerTribe = Utils.parseInt(tokens[18]);
		maxPerTribe = Utils.parseInt(tokens[19]);

		maxStone = Utils.parseInt(tokens[20]);
		maxFlower = Utils.parseInt(tokens[21]);
		maxTrees = Utils.parseInt(tokens[22]);

		maxBee = Utils.parseInt(tokens[23]);
		maxButterfly = Utils.parseInt(tokens[24]);
		maxFox = Utils.parseInt(tokens[25]);
		maxCannibalTribes = Utils.parseInt(tokens[26]);

		cloud0 = Utils.parseInt(tokens[30]);
		cloud1 = Utils.parseInt(tokens[31]);
		cloud2 = Utils.parseInt(tokens[32]);
		cloud3 = Utils.parseInt(tokens[33]);
		cloudY = Utils.parseInt(tokens[34]);

		tiles = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x][y] = Utils.parseInt(tokens[(x + y * width) + 35]);
			}
		}

		highestTile();

		enviornment = new Enviornment(handler, Utils.parseDouble(tokens[27]), Utils.parseDouble(tokens[28]),
				Utils.parseDouble(tokens[29]));

		int offset = width * height + 35, off = offset;

		handler.logWorld(tokens[off] + " Entities saved to load");

		for (off = offset; off < tokens.length - 7;) {

			handler.logWorld("Currently loading a " + tokens[off + 1]);

			handler.logWorld("Offset number " + off + " Out of " + (tokens.length - 7));

			if (tokens[off + 1].contains("Bee")) {

				entityManager.addEntity(
						new Bee(handler, Utils.parseDouble(tokens[off + 2]), Utils.parseDouble(tokens[off + 3]),
								Utils.parseBoolean(tokens[off + 4]), Utils.parseLong(tokens[off + 5])),
						false);

				off += 5;
			}

			if (tokens[off + 1].contains("Butterfly")) {

				entityManager.addEntity(
						new Butterfly(handler, Utils.parseDouble(tokens[off + 2]), Utils.parseDouble(tokens[off + 3]),
								Utils.parseBoolean(tokens[off + 4]), Utils.parseLong(tokens[off + 5])),
						false);

				off += 5;
			}

			if (tokens[off + 1].contains("Cannibal")) {

				entityManager.addEntity(new Cannibal(handler, Utils.parseDouble(tokens[off + 2]),
						Utils.parseDouble(tokens[off + 3]), Utils.parseDouble(tokens[off + 4]),
						Utils.parseInt(tokens[off + 5]), Utils.parseBoolean(tokens[off + 6])), false);

				off += 6;
			}

			if (tokens[off + 1].contains("Fox")) {

				entityManager.addEntity(
						new Fox(handler, Utils.parseDouble(tokens[off + 2]), Utils.parseDouble(tokens[off + 3])),
						false);

				off += 3;
			}

			if (tokens[off + 1].contains("Cloud")) {

				entityManager.addEntity(
						new Cloud(handler, Utils.parseDouble(tokens[off + 2]), Utils.parseDouble(tokens[off + 3]),
								Utils.parseInt(tokens[off + 4]), Utils.parseDouble(tokens[off + 5])),
						false);

				off += 5;
			}

			if (tokens[off + 1].contains("Flower")) {

				entityManager.addEntity(
						new Flower(handler, Utils.parseDouble(tokens[off + 2]), Utils.parseDouble(tokens[off + 3]),
								Utils.parseInt(tokens[off + 4]), Utils.parseDouble(tokens[off + 5])),
						false);

				off += 5;
			}

			if (tokens[off + 1].contains("Shrubbery")) {

				entityManager.addEntity(new Shrubbery(handler, Utils.parseDouble(tokens[off + 2]),
						Utils.parseDouble(tokens[off + 3]), Utils.parseInt(tokens[off + 4])), false);

				off += 4;
			}

			if (tokens[off + 1].contains("Stone")) {

				entityManager.addEntity(new Stone(handler, Utils.parseDouble(tokens[off + 2]),
						Utils.parseDouble(tokens[off + 3]), Utils.parseInt(tokens[off + 4])), false);

				off += 4;
			}

			if (tokens[off + 1].contains("Tree")) {

				entityManager.addEntity(new Tree(handler, Utils.parseDouble(tokens[off + 2]),
						Utils.parseDouble(tokens[off + 3]), Utils.parseInt(tokens[off + 4])), false);

				off += 4;
			} else {
				off++;
			}

		}

		for (int i = width * height + 35; i < tokens.length; i++)
			if (tokens[i].contains("Continue")) {
				offset = i;
				continue;
			}

		Player p = entityManager.getPlayer();

		p.items[0] = Utils.parseInt(tokens[offset + 1]);
		p.items[1] = Utils.parseInt(tokens[offset + 2]);
		p.items[2] = Utils.parseInt(tokens[offset + 3]);
		p.items[3] = Utils.parseInt(tokens[offset + 4]);
		p.items[4] = Utils.parseInt(tokens[offset + 5]);
		p.items[5] = Utils.parseInt(tokens[offset + 6]);

		int newOffset = offset + 7;
		offset += Utils.parseInt(tokens[newOffset]);

		for (int i = 0; i < Utils.parseInt(tokens[newOffset]); i++) {
			String n = tokens[i + newOffset + 1];

			if (n == "Sword")
				p.tools.add(new Sword(handler));
			if (n == "Axe")
				p.tools.add(new Axe(handler));
			if (n == "Torch")
				p.tools.add(new Torch(handler));
		}

		Enviornment e = enviornment;

		e.setTime(Utils.parseLong(tokens[offset]));
		e.rohundo = Utils.parseInt(tokens[offset + 1]);
		e.collevti = Utils.parseInt(tokens[offset + 2]);
		e.lapse = Utils.parseInt(tokens[offset + 3]);

	}

	public void saveWorld(String path) {
		String content = "Save " + System.lineSeparator() + width + " ";
		content += height + System.lineSeparator();

		content += entityManager.getPlayer().getX() + " " + entityManager.getPlayer().getY() + System.lineSeparator();

		// ADD LINE SEPERATORS AND SPACES
		content += stone0 + " ";
		content += stone1 + " ";
		content += stone2 + " ";
		content += flower0 + " ";
		content += flower1 + " ";
		content += flower2 + " ";
		content += youngTrees + " ";
		content += midTrees + " ";
		content += oldTrees + System.lineSeparator();

		content += bee + " ";
		content += butterfly + " ";
		content += fox + " ";
		content += cannibalTribes + " ";
		content += minPerTribe + " ";
		content += maxPerTribe + System.lineSeparator();

		content += maxStone + " ";
		content += maxFlower + " ";
		content += maxTrees + System.lineSeparator();

		content += maxBee + " ";
		content += maxButterfly + " ";
		content += maxFox + " ";
		content += maxCannibalTribes + System.lineSeparator();

		content += enviornment.getWind() + " ";
		content += enviornment.getWindChange() + " ";
		content += enviornment.getWindSwing() + System.lineSeparator();

		content += cloud0 + " ";
		content += cloud1 + " ";
		content += cloud2 + " ";
		content += cloud3 + System.lineSeparator();
		content += cloudY;

		for (int y = 0; y < height; y++) {
			content += System.lineSeparator();
			for (int x = 0; x < width; x++) {
				content += getTile(x, y).getId() + " ";
			}
		}
		content += System.lineSeparator();

		content += entityManager.saveString();

		content += System.lineSeparator();

		content += "Continue";

		content += System.lineSeparator();

		Player p = entityManager.getPlayer();

		for (int i : p.items) {
			content += " " + i;
		}
		content += System.lineSeparator();

		content += p.tools.size();
		content += System.lineSeparator();

		for (int i = 0; i < p.tools.size(); i++) {
			content += p.tools.get(i).getClass();
			content += System.lineSeparator();
		}

		Enviornment e = enviornment;

		content += e.getTime() + " " + e.rohundo + " " + "1" + " " + e.lapse;

		Utils.fileWriter(content, path);
	}

	public Enviornment getEnviornment() {
		return enviornment;
	}

	public int getSpawnx() {
		return spawnx;
	}

	public int getSpawny() {
		return spawny;
	}

	public void outOfBounds(Entity e) {
		if (e.getY() > (height + 10) * Tile.TILEHEIGHT || e.getX() > (width + 10) * Tile.TILEWIDTH
				|| e.getX() < (-10 * Tile.TILEWIDTH) || e.getY() < (-15 * Tile.TILEHEIGHT)) {
			if (e.getClass() == Player.class) {
				Player p = (Player) e;
				if (p.lives == 0) {
					kill(e);
				} else {
					p.lives--;
					p.setX(spawnx);
					p.setY(spawny);
					p.setHealth(p.MAX_HEALTH);
					handler.logWorld("Player lives: " + p.lives);
				}
			} else if (e.getClass() != Cloud.class && (e.getClass() == Flower.class || e.getClass() == Stone.class)) {
				e.kill();
			} else if (e.getClass() != Cloud.class) {
				kill(e);
			}
		}
	}

	public void kill(Entity e) {
		if (e.getClass() != Stone.class && e.getClass() != Player.class) {
			e.dead = true;
			e.kill();
		}
		handler.logWorld("Killed: " + e);

		if (e.getClass() == Flower.class) {
			Flower f = (Flower) e;

			if (f.getType() == 0) {
				flower0--;
				for (int i = 0; i < (int) Math.ceil(Math.random() * 3); i++) {
					handler.getWorld().getItemManager()
							.addItem(Item.whitePetal.createNew((int) f.getX(), (int) f.getY()));
				}
			} else if (f.getType() == 1) {
				flower1--;
				for (int i = 0; i < (int) Math.ceil(Math.random() * 3); i++) {
					handler.getWorld().getItemManager()
							.addItem(Item.pinkPetal.createNew((int) f.getX(), (int) f.getY()));
				}
			} else {
				flower2--;
				for (int i = 0; i < (int) Math.ceil(Math.random() * 3); i++) {
					handler.getWorld().getItemManager()
							.addItem(Item.bluePetal.createNew((int) f.getX(), (int) f.getY()));
				}
			}

		} else if (e.getClass() == Stone.class) {
			Stone s = (Stone) e;
			int i = 1;
			if (s.getType() == 0) {
				if (s.getOrType() == 0) {
					stone0--;
				} else if (s.getOrType() == 1) {
					stone1--;
				} else {
					stone2--;
				}
				s.kill();
				i = 1;
			} else if (s.getType() == 1) {
				s.setType(s.getType() - 1);
				i = 2;
			} else {
				s.setType(s.getType() - 1);
				i = 3;
			}

			if (!loading) {
				for (int b = 0; b < i; b++) {
					itemManager.addItem(Item.metal.createNew((int) (s.getX()), (int) (s.getY() - 36)));
				}
			}

		} else if (e.getClass() == Soil.class) {
			for (int V = 0; V < Public.random(1, 3); V++)
				itemManager.addItem(Item.dirt.createNew((int) Public.random(e.getX() - 18, e.getX() + 18),
						(int) e.getY() + Tile.TILEHEIGHT * 2));
		} else if (e.getClass() == Butterfly.class) {
			butterfly--;
		} else if (e.getClass() == Bee.class) {
			bee--;
		} else if (e.getClass() == Fox.class) {
			fox--;
		} else if (e.getClass() == Tree.class) {

			youngTrees--;

			Tree t = (Tree) e;

			for (int b = 0; b < (t.getAge() / 2 - 3) * 2; b++) {
				itemManager.addItem(Item.wood.createNew((int) (e.getX() + e.getbounds().x),
						(int) (e.getY() + e.getbounds().y - 36)));
			}
		}
		if (e.getClass() == Player.class) {
			Player p = (Player) e;
			if (p.lives == 0) {
				e.kill();
				e.dead = true;
				dead = true;
			} else {
				p.lives--;
				p.setX(spawnx);
				p.setY(spawny);
				p.setHealth(p.MAX_HEALTH);
				handler.logWorld("Player lives: " + p.lives);
			}
		}

		if (e.getClass() == center.getClass() && e.dead) {
			for (Entity e2 : entityManager.getEntities()) {
				if (e2.creature) {
					center = e2;
					handler.logWorld("Centered on: " + e2);
					return;
				}
			}
			if (entityManager.getEntities().size() > 0) {
				center = entityManager.getEntities().get(0);
				handler.logWorld("Centered on: " + center);
				waitingForCreature = true;
			} else {
				handler.logWorld("No more entities to center on");
				waitingForCreature = true;
			}
		}
	}

	public static int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		World.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		World.height = height;
	}

	public void highestTile() {
		handler.logWorld("Getting highest tiles...");
		heights = new ArrayList<ArrayList<Integer>>();
		tops = new ArrayList<ArrayList<Integer>>();
		for (int x = 0; x < width + 2; x++) {
			handler.logWorldSilent("Tile chosen: " + x);
			heights.add(new ArrayList<Integer>());
			tops.add(new ArrayList<Integer>());
			for (int y = 0; y < height + 1; y++) {
				handler.logWorldSilent("Checking tile: (" + x + ", " + y + ")");

				if (getTile(x, y).isTop() || getTile(x, y).isSolid())
					tops.get(x).add(y);

				int v = getTile(x, y).getId();
				if (v == 1 || v == 2 || v == 3 || v == 10 || v == 11 || v == 12 || v == 13 || v == 16) {
					handler.logWorldSilent("Tile (" + x + ", " + y + ") is solid");
					heights.get(x).add(y);

					if (!save)
						if (v == 1 || v == 10 || v == 13 || v == 16)
							entityManager.addEntity(
									new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) * Tile.TILEHEIGHT, 0), false);
						else if (v == 2 || v == 11)
							entityManager.addEntity(
									new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) * Tile.TILEHEIGHT, 1), false);
						else if (v == 3 || v == 12)
							entityManager.addEntity(
									new Shrubbery(handler, x * Tile.TILEWIDTH, (y - 1) * Tile.TILEHEIGHT, 2), false);

					heights.add(new ArrayList<Integer>());
				}

				// if (v == 7 || v == 8 || v == 9 || v == 10 || v == 11 | v == 12 || v == 15 ||
				// v == 16)
				// if (v == 8 || v == 11)
				// entityManager.addEntity(new Soil(handler, x, (y + 1), 1),
				// false);
				// else if (v == 7 || v == 10 || v == 15 || v == 16)
				// entityManager.addEntity(new Soil(handler, x, (y + 1), 0),
				// false);
				// else if (v == 9 || v == 12)
				// entityManager.addEntity(new Soil(handler, x, (y + 1), 2),
				// false);
			}

			if (heights.get(x).size() <= 0) {
				handler.logWorldSilent("404: No tile found");
				heights.get(x).add(-Tile.TILEHEIGHT * 2);

				if (tops.get(x).size() <= 0) {
					tops.get(x).add(-Tile.TILEHEIGHT * 2);
				}
			}
		}
	}

	public int getHighest(double x) {

		int nx = Public.grid(x, Tile.TILEWIDTH, 0);

		if (heights.get(nx).size() > 0)
			return heights.get(nx).get(0);
		else
			return -1;
	}

	public int getLowest(double x) {

		int nx = Public.grid(x, Tile.TILEWIDTH, 0);

		if (heights.get(nx).size() > 0)
			return heights.get(nx).get(heights.get(nx).size() - 1);
		else
			return -1;
	}

	public int getHigher(double x, double y, int threshold) {

		int nx = Public.grid(x, Tile.TILEWIDTH, 0);

		int dist = threshold;
		int out = -1;

		for (int oy = 0; oy < tops.get(nx).size(); oy++) {
			if (Public.difference(tops.get(nx).get(oy) * Tile.TILEHEIGHT, y) < dist) {
				dist = (int) Public.difference(tops.get(nx).get(oy) * Tile.TILEHEIGHT, y);
				out = tops.get(nx).get(oy);
			}
		}

		return out;
	}

	public boolean checkCollision(int x, int y) {
		x = (int) x / Tile.TILEWIDTH;
		y = (int) y / Tile.TILEHEIGHT;
		if (getTile(x, y).isSolid()) {
			return true;
		} else {
			return false;
		}
	}

	private void addFlower(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.TILEWIDTH + 1);
			double l = Public.random(-2, 0);
			handler.logWorld("Layer = " + l);
			entityManager.addEntity(new Flower(handler, x,
					(heights.get(x / Tile.TILEWIDTH)
							.get((int) Public.random(0, heights.get(x / Tile.TILEWIDTH).size() - 1)) - 1)
							* Tile.TILEHEIGHT,
					type, l), true);
		}
	}

	private void addStone(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1) * Tile.TILEWIDTH;
			entityManager.addEntity(new Stone(handler, x,
					(heights.get(x / Tile.TILEWIDTH)
							.get((int) Public.random(0, heights.get(x / Tile.TILEWIDTH).size() - 1)) - 1)
							* Tile.TILEHEIGHT,
					type), true);
		}
	}

	private void addTrees(int amount, int agemin, int agemax) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width * Tile.TILEWIDTH + 1);
			entityManager.addEntity(new Tree(handler, x - 18,
					(heights.get(x / Tile.TILEWIDTH)
							.get((int) Public.random(0, heights.get(x / Tile.TILEWIDTH).size() - 1)) - 8)
							* Tile.TILEHEIGHT,
					(int) Public.random(agemin, agemax)), true);
		}
	}

	private void addBee(int amount, long timer) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1);
			int y = (int) (Math.random() * height + 1);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1);
				y = (int) (Math.random() * height + 1);
			}

			entityManager.addEntity(new Bee(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, false, timer), true);
		}
	}

	private void addButterfly(int amount, long timer) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1);
			int y = (int) (Math.random() * height + 1);

			while (getTile(x, y).isSolid()) {
				x = (int) (Math.random() * width + 1);
				y = (int) (Math.random() * height + 1);
			}

			entityManager.addEntity(new Butterfly(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, false, timer),
					true);
		}
	}

	private void addFox(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) (Math.random() * width + 1);

			while (getTile(x, 0).isSolid()) {
				x = (int) (Math.random() * width + 1);
			}

			entityManager.addEntity(new Fox(handler, x * Tile.TILEWIDTH, 0), true);
		}
	}

	public void addConBee(int x, int y, long timer) {
		if (bee < maxBee) {
			entityManager.addEntity(new Bee(handler, x, y, false, timer), true);
		} else {
			handler.logWorld("Too many bees!");
		}
	}

	public void addConButterfly(int x, int y, long timer) {
		if (butterfly < maxButterfly) {
			entityManager.addEntity(new Butterfly(handler, x, y, false, timer), true);
		} else {
			handler.logWorld("Too many butterflies!");
		}
	}

	public void addCloud(int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int y = (int) (Math.random() * -handler.getHeight() + cloudY * Tile.TILEHEIGHT);
			double x = Math.random() * (Tile.TILEWIDTH * (width + 8)) - (Tile.TILEWIDTH * 4);
			entityManager.addEntity(new Cloud(handler, x, y, type, Math.random() / 2), true);
		}
	}

	private void addCannibalTribe(int amount, int groupX) {
		entityManager.addEntity(new Cannibal(handler, groupX,
				heights.get(groupX / Tile.TILEWIDTH)
						.get((int) Public.random(0, heights.get(groupX / Tile.TILEWIDTH).size() - 1)) - 2,
				Public.random(0.1, 0.6), 1, true), true);
		for (int i = 0; i < amount - 1; i++) {
			int x = (int) Public.random(groupX - 2, groupX + 2);
			int y = heights.get(x / Tile.TILEWIDTH)
					.get((int) Public.random(0, heights.get(x / Tile.TILEWIDTH).size() - 1)) - 2;

			entityManager.addEntity(
					new Cannibal(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, Public.random(0.51, 0.8), 1, false),
					true);
		}
	}

	private void addShrubbery(int amount) {
		for (int i = 0; i < amount; i++) {
			int x = (int) Public.random(0, width);
			int y = heights.get(x).get((int) Public.random(0, heights.get(x).size() - 1)) - 1;

			entityManager.addEntity(
					new Shrubbery(handler, x * Tile.TILEWIDTH, y * Tile.TILEHEIGHT, (int) Public.random(3, 4)), true);
		}
	}

}
