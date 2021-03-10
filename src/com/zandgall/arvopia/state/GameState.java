package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.guis.Gui;
import com.zandgall.arvopia.guis.PlayerGui;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.FileChooser;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.worlds.World;

public class GameState extends State {
	private World world;
	
	public long worldt, ui, songs, other, worldtr;
	
//	private World subworld;

	public Gui u;

	private final String[] levels = new String[] { "/Worlds/LevelOne", "/Worlds/LevelTwo", "/Worlds/0.5Forest",
			"/Worlds/world1.txt", "/Worlds/world2.txt", "/Worlds/Staircase.txt" };

	private Sound SweetGuitar, StarsInTheNight, Playtime, Raindrops, Thunderstorm;

	private boolean songPlaying, loadingWorld;

	public boolean isSongPlaying() {
		return songPlaying;
	}

	private Button skip;

	public GameState(Handler handler) {
		super(handler);

		songPlaying = true;

		PublicAssets.init();

		SweetGuitar = new Sound("Songs/SweetGuitar.wav");
		StarsInTheNight = new Sound("Songs/StarsInTheNight.wav");
		Playtime = new Sound("Songs/Playtime.wav");
		Raindrops = new Sound("Songs/Raindrops.wav");
		Thunderstorm = new Sound("Songs/Thunderstorm.wav");
		
		SweetGuitar.setVolume(-80, false);
		StarsInTheNight.setVolume(-80, false);
		Playtime.setVolume(-80, false);
		Raindrops.setVolume(-80, false);
		Thunderstorm.setVolume(-80, false);

		world = new World(handler, "/Worlds/DefaultWorld", false, false, false);
		world.finish(false);
		handler.setWorld(world);

		u = new PlayerGui(handler);

		skip = new Button(handler, 100, 130, 80, 20, "Skips the resseting of FPS", "Skip");

	}

	public void openWorld(boolean open, int index) {

		world.reset();
		loadingWorld = true;
		ready = false;

		if (open) {

			FileChooser fileGet = new FileChooser();

			String i = fileGet.getFile("C:\\Arvopia");

			if (i.length() > 0) {
				loadWorld(i, true);
			} else {
				State.setState(getPrev());
				handler.log("Couldn't load the world specified");
			}
		} else {
			loadWorld(levels[index], false);
		}
		
//		if(world.subworld!=null)
//			subworld = new World(handler, world.subworld, false, false, true);
	}

	public void saveWorld() {
		FileChooser fileSet = new FileChooser();

		String i = fileSet.saveFile("C:\\Arvopia\\Saves");

		world.saveWorld(i + ".arv");
	}

	public void openSave() {
		FileChooser fileGet = new FileChooser();

		String i = fileGet.getFile("C:\\Arvopia\\Saves");

		if (i.length() > 0) {
			loadWorld(i, true);
		} else {
			State.setState(getPrev());
			handler.log("Couldn't load the world specified");
		}
	}

	public void loadWorld(String path, boolean tf) {
		handler.log("World: " + path + " loaded");
		world = new World(handler, path, tf, true, false);
		world.percentDone = 0;
		loadingWorld = true;
		ready = false;

		handler.getGame().stable = false;
	}

	@Override
	public void tick() {
		if (!loadingWorld) {
			long pre = System.nanoTime();
			world.tick();
			world.spawing();
			worldt = System.nanoTime()-pre;
		}
		if (!ready) {
			world.finish(true);
			handler.getGame().forceRender();
		}
		
		long pre = System.nanoTime();
		u.tick();
		ui=System.nanoTime()-pre;
		
		pre = System.nanoTime();
		
		if(handler.getVolume()!=0) {
			songPlaying = !State.songEnded();
			
			switching();
			
			if (!songPlaying) {
				if (world.getEnviornment().getHours() > 9 && world.getEnviornment().getHours() < 19) {
					
					if(world.getEnviornment().precipitation) {
						if(world.getEnviornment().getTemp()>=32) {
							if(world.getEnviornment().stormy) {
								Thunderstorm.setVolume((int) -80, true);
								setSong(Thunderstorm);
							} else {
								Raindrops.setVolume((int) -80, true);
								setSong(Raindrops);
							}
						} else {
							Raindrops.setVolume((int) -80, true);
							setSong(Raindrops);
						}
					} else if (Math.random() < 0.5 && world.getEntityManager().getPlayer().health >= world.getEntityManager()
							.getPlayer().MAX_HEALTH) {
						Playtime.setVolume((int) -80, true);
						setSong(Playtime);
					} else {
						SweetGuitar.setVolume((int) -80, true);
						setSong(SweetGuitar);
					}
				} else {
					if(world.getEnviornment().precipitation) {
						if(world.getEnviornment().getTemp()>=32) {
							if(world.getEnviornment().stormy) {
								Thunderstorm.setVolume((int) -80, true);
								setSong(Thunderstorm);
							} else {
								Raindrops.setVolume((int) -80, true);
								setSong(Raindrops);
							}
						} else {
							Raindrops.setVolume((int) -80, true);
							setSong(Raindrops);
						}
					} else {
						StarsInTheNight.setVolume((int) -80, true);
						setSong(StarsInTheNight);
					}
				}
				
				handler.setVolume();
			}
		}
		
		songs = System.nanoTime()-pre;
		pre = System.nanoTime();
		
		if(world.getEntityManager().getPlayer().tools.size()==1) {
			Achievement.award(Achievement.firsttool);
		}
		
		String h = world.getEntityManager().getPlayer().has;
		
		if(h.contains("-1")&&h.contains("-2")&&h.contains("-3")&&h.contains("-4")&&h.contains("-5"))
			Achievement.award(Achievement.alltools);
		
		if(!LoaderException.readFile("C:\\Arvopia\\01.arv", false).contains("Hoarder"))
			for(int i: world.getEntityManager().getPlayer().items)
				if(i>=100) 
					Achievement.award(Achievement.hoarder);
		
		other = System.nanoTime()-pre;
		
	}
	
	public void switching() {
		if(getSong()==null)
			return;
		if(getSong()==Thunderstorm) {
			if(!world.getEnviornment().stormy || !world.getEnviornment().precipitation)
				player.fadeOut();
		} else if(getSong()==Raindrops) {
			if(!world.getEnviornment().precipitation)
				player.fadeOut();
		} else if(getSong()==SweetGuitar) {
			if(world.getEnviornment().stormy || world.getEnviornment().precipitation)
				player.fadeOut();
		} else if(getSong()==Playtime) {
			if(world.getEnviornment().stormy || world.getEnviornment().precipitation)
				player.fadeOut();
		} else if(getSong()==StarsInTheNight) {
			if(world.getEnviornment().stormy || world.getEnviornment().precipitation ||	(world.getEnviornment().getHours() > 9 && world.getEnviornment().getHours() < 19)) {
				player.fadeOut();
			}
		}
		
		if(player.getSong().hasEnded())
			player.stop(true);
		
		
	}
	

	boolean preWorked = false, ready = false;
	public boolean resettingFps = true;

	int loadingPhase = 0;

	public void setLoadingPhase(int loadingPhase) {
		this.loadingPhase = loadingPhase;
	}

	@Override
	public void render(Graphics g, Graphics2D g2d) {
		if (ready) {
			long pre = System.nanoTime();
			world.render(g, handler.getGame().get2D());
			g2d.setTransform(handler.getGame().getDefaultTransform());
			if(!(world.getEntityManager().getPlayer().viewInventory || world.getEntityManager().getPlayer().viewCrafting))
				u.render(g);
			worldtr = System.nanoTime()-pre;
		}

		if (!ready) {
			if(loadingPhase == -1) {
				handler.getGame().setTps(60);
	
				handler.setWorld(world);
				resettingFps = false;
				loadingWorld = false;
				ready = true;
			} else {
				
				g.setColor(Color.black);
				g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
				g.setColor(Color.white);
				g.setFont(new Font("Arial", Font.PLAIN, 20));
				switch (loadingPhase) {
				case 1:
					g.drawString("Getting variables...", 100, 100);
					break;
				case 2:
					g.drawString("Loading tiles...", 100, 100);
					break;
				case 3:
					g.drawString("Loading custom entities...", 100, 100);
					break;
				case 4:
					g.drawString("Making background...", 100, 100);
					break;
				default:
					g.drawString("Loading world...", 100, 100);
					break;
				}
			}
		}

	}

	@Override
	public void init() {

	}

}
