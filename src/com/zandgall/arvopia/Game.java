package com.zandgall.arvopia;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.zandgall.arvopia.display.Display;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.input.*;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.quests.Cutscene;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.state.*;
import com.zandgall.arvopia.utils.Chart;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

public class Game implements Runnable { // Runnable = Thread

	public boolean paused;

	public void pause() {
		paused = true;
	}

	public void unPause() {
		paused = false;
	}

	public static double scale;

	// Debug timers

	public boolean ticking;
	public boolean rendering;

	long tickTicks, renders, misc;

	public Chart gamelag, overall, world;

	// Work display
	private Display display; // Display class
	private int width = 0, height = 0; // Game size

	public int fps = 60, tps = 60, ticks = 0, fullTicks, renTicks;
	private double timePerTick = 1000000000 / tps, renderPerTick = 1000000000 / fps;

	public Display getDisplay() {
		return display;
	}

	public void setTps(int tps) {
		this.tps = tps;
		timePerTick = 1000000000 / tps;
	}

	public void setFps(int fps) {
		this.fps = fps;
		renderPerTick = 1000000000 / fps;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String title; // Game name
	public boolean resizable;

	// Input
	private KeyManager keyManager;
	private MouseManager mouse;

	// Camera
	private GameCamera gameCamera;

	// Handler
	public Handler handler;

	// Thread stuff
	private static Thread thread; // Game loop

	// Log
	public static Log log;
	String main = "C:\\Arvopia\\logs\\main.txt";

	private static boolean running = false; // Game loop boolean

	// Graphics
	private BufferStrategy bf;
	private Graphics g;
	private Graphics2D g2d;
	private boolean renOnce = false;

	AffineTransform af;

	public State optionState, gameState, menuState, worldState, changelogState, instructionsState, reportingState,
			achievementsState;

	public AffineTransform getDefaultTransform() {
		return af;
	}

	public AchievementManager achievementsDisplay;
	public QuestManager questsDisplay;

	public Recorder recorder;

	// Initialate the Game class
	public Game(String title, int width, int height, boolean resizable, Log log) {

		Reporter reporter = new Reporter();

		scale = 1;

		Game.log = log;

		achievementsDisplay = new AchievementManager();
		questsDisplay = new QuestManager();

		this.width = width; // Set width
		this.height = height; // Set height
		this.title = title; // Set title
		this.resizable = resizable; // Allows resizing of canvas
		keyManager = new KeyManager();
		mouse = new MouseManager(handler);

		recorder = new Recorder(handler);

		overall = new Chart(new double[] { 0, 0 }, new String[] { "Tick", "Render" },
				new Color[] { Color.red, Color.blue }, "Overall", 10, 100, 100, 100);
		gamelag = new Chart(new double[] { 0, 0, 0, 0 }, new String[] { "World", "UI", "Songs", "Anything else" },
				new Color[] { Color.blue, Color.orange, Color.pink, Color.green }, "In-Game", 120, 100, 100, 100);
		world = new Chart(new double[] { 0, 0, 0, 0, 0 },
				new String[] { "Entities", "Items", "Enviornment", "Water", "Tiles" },
				new Color[] { Color.blue, Color.green, Color.red, Color.cyan, Color.orange }, "World", 230, 100, 100,
				100);

	}

	public void setDimension(int width, int height) {
		this.width = width;
		this.height = height;
		display.getFrame().setSize(width, height);
		display.getCanvas().setSize(width, height);
	}

	private void init() {
		display = new Display(title, width, height); // Initialize display adding width, height, and title
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouse);
		display.getFrame().addMouseMotionListener(mouse);
		display.getFrame().setResizable(resizable);
		display.getCanvas().addMouseListener(mouse);
		display.getCanvas().addMouseMotionListener(mouse);
		display.getCanvas().addMouseWheelListener(mouse);

		handler = new Handler(this);
		handler.init();
		gameCamera = new GameCamera(handler, 0, 0);

		Achievement.init(handler, achievementsDisplay);
		Quest.init(handler, questsDisplay);
		Cutscene.init(handler);

		optionState = new OptionState(handler);
		gameState = new GameState(handler);
		menuState = new TitleState(handler);
		worldState = new WorldLoaderState(handler);
		changelogState = new Changelog(handler);
		instructionsState = new Instructions(handler);
		reportingState = new ReportingState(handler);
		achievementsState = new AchievementsState(handler);

		handler.log("Will open? : " + LoaderException.readFile("C:\\Arvopia\\DontShowThisAgain"));

		if (Utils.parseBoolean(LoaderException.readFile("C:\\Arvopia\\DontShowThisAgain"))) {
			State.setState(menuState);
			handler.log("True");
		} else
			State.setState(instructionsState);

		State.getState().init();

		Public.init(handler);

		log.log("Successfully initiated " + title + "'s Game loop");
	}

	private void tick() { // Update vars posistions and objects

		achievementsDisplay.tick();
		questsDisplay.tick();

		keyManager.tick();

		if (keyManager.b) {
			if (!Cutscene.run(Cutscene.practice)) {
				if (State.getState() != null && !paused) {
					State.getState().tick();
					State.getState().tickMusic();
				} else if (paused) {
					handler.getWorld().getEntityManager().getPlayer().tick();
				}
			}
		} else if (State.getState() != null && !paused) {
			State.getState().tick();
			State.getState().tickMusic();
		} else if (paused) {
			handler.getWorld().getEntityManager().getPlayer().tick();
		}

		mouse.tick();
	}

	public void forceRender() {
		render();
	}

	private void render() { // Draws to screen

		bf = display.getCanvas().getBufferStrategy();
		if (bf == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}

		g = bf.getDrawGraphics();
		g2d = (Graphics2D) g;

		if (!renOnce) {
			af = g2d.getTransform();
			renOnce = true;
		}

		// Clear screen
		g.clearRect(0, 0, width, height);
		/// Start draw
		if (State.getState() != null) {
			State.getState().render(g, g2d);
		}

		achievementsDisplay.Render(g);
		questsDisplay.Render(g);

		g2d.transform(af);

		if (keyManager.f1) {
			mouse.visualize(g);
			overall.render(g);
			gamelag.render(g);
			world.render(g);
		}

		// End draw

		bf.show();
		g.dispose();
		g2d.dispose();
	}

	public int prepreTicks, preTicks, preRenTicks;
	public boolean stable;

	public void run() { // Game loop

		init(); // Initiates graphics

		double delta = 0, renderDelta = 0;
		long now;
		long nowSec;
		long lastTime = System.nanoTime();
		long timer = 0;

		long prevCheck = System.currentTimeMillis() / 1000;

		while (isRunning()) { // official loop
			now = System.nanoTime();
			nowSec = Math.round(System.currentTimeMillis() / 1000);
			delta += (now - lastTime) / timePerTick;
			renderDelta += (now - lastTime) / renderPerTick;
			timer += now - lastTime;
			lastTime = now;

			if (delta >= 1) {
				long pre = System.nanoTime();
				tick();
				ticks++;
				delta = 0;
				tickTicks = System.nanoTime() - pre;
			}

			if (renderDelta >= 1) {
				long pre = System.nanoTime();
				rendering = true;
				render();
				rendering = false;
				renTicks++;
				renderDelta = 0;
				renders = System.nanoTime() - pre;
			}

			if (timer >= 1000000000) {
				fullTicks = ticks;
				handler.saveFps(fullTicks);

				if (keyManager.f1) {
					GameState g = (GameState) (gameState);
					gamelag.update(new double[] { g.worldt + g.worldtr, g.ui, g.songs, g.other });
					overall.update(new double[] { tickTicks, renders });
					world.update(new double[] { handler.getWorld().entities + handler.getWorld().entitiesr,
							handler.getWorld().items + handler.getWorld().itemsr,
							handler.getWorld().enviorn + handler.getWorld().enviorr,
							handler.getWorld().water + handler.getWorld().waterr,
							handler.getWorld().tilet + handler.getWorld().tiler });
				}

				stable = (Public.difference(preTicks, prepreTicks)) < 2 && ((Public.difference(preTicks, ticks)) < 2)
						&& Public.difference(ticks, fps) < 5 || ticks < Public.range(0, fps, fps - 20);

				prevCheck = nowSec;

				if (nowSec - prevCheck <= 2
						&& (Public.difference(renTicks, preRenTicks) > 2 || Public.difference(ticks, preTicks) > 2)) {
					log.log("FPS Fluctuating a little... : " + renTicks + " : " + ticks);
				} else if (stable) {
					log.log("FPS: " + renTicks + " : " + ticks);
				} else {
					log.log("FPS Not stable: " + renTicks + " : " + ticks);
				}

				prepreTicks = preTicks;
				preTicks = ticks;
				preRenTicks = renTicks;

				mouse.reset();

				ticks = 0;
				renTicks = 0;
				timer = 0;
			}
		}

		stop(); // X_X

	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public MouseManager getMouse() {
		return mouse;
	}

	public GameCamera getGameCamera() {
		return gameCamera;
	}

	public synchronized void start() {
		if (isRunning()) // Safety
			return; // Returns to loop
		setRunning(true);
		thread = new Thread(this); // Creates thread
		thread.start(); // Starts it

	}

	public synchronized void stop() {
		if (!isRunning()) // Safety
			return; // Returns to loop
		try {
			log.out("Terminated");
			thread.join(); // Stops: requires try/catch
			display.getFrame().dispatchEvent(new WindowEvent(display.getFrame(), WindowEvent.WINDOW_CLOSING));
			display.getFrame().dispose();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static boolean isRunning() {
		return running;
	}

	public static void setRunning(boolean running) {
		Game.running = running;
	}

	public Image makeImage(Shape s) {
		Rectangle r = s.getBounds();
		Image image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D gr = (Graphics2D) image.getGraphics();
		// move the shape in the region of the image
		gr.translate(-r.x, -r.y);
		gr.draw(s);
		gr.dispose();
		return image;
	}

	public Graphics2D get2D() {
		return g2d;
	}

	public JFrame getFrame() {
		return display.getFrame();
	}

}
