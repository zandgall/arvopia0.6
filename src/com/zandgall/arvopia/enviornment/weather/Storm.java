package com.zandgall.arvopia.enviornment.weather;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.moveableStatics.MoveableStatic;
import com.zandgall.arvopia.enviornment.weather.Rain.Droplet;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.gfx.transform.Tran;
import com.zandgall.arvopia.items.tools.Umbrella;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.tiles.Tile;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.worlds.World;

public class Storm {
	public static double wind = 0;

	Handler game;

	ArrayList<Droplet> Droplets;
	ArrayList<Cloud> clouds;

	Lightning l;

	public Sound rain;

	long lTimer = 0;
	long lTimed = (long) Public.random(100, 1000);
	
	boolean stop = false;
	
	public Storm(Handler game, int ammount) {

		rain = new Sound("Sounds/Rain.wav");
		rain.setVolume(-80, false);

		this.game = game;
		Droplets = new ArrayList<Droplet>();
		for (int i = 0; i < ammount; i++) {
			Droplets.add(new Droplet(game, (int) Public.random(-360, 1080), (int) Public.random(-400, 0),
					Public.random(2, 10)));
		}
		clouds = new ArrayList<Cloud>();
		for (int i = 0; i < ammount; i++) {
			clouds.add(new Cloud(game, (int) Public.random(-360, 1080), (int) Public.random(-400, 0),
					(int) Public.random(0, 3), Public.random(0.5, 2)));
		}

	}

	public void tick(int speed) {
		
		
		
		if (rain.hasEnded())
			rain.Start(-1, true);

		rain.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
		rain.tick(false);
		if(OptionState.fxVolume==0)
			rain.setVolume(-80, false);
		
		
		if (lTimer >= lTimed)
			if (l == null) {
				l = new Lightning(game, (int) Public.random(0, World.getWidth() * Tile.TILEWIDTH));
				lTimed = (long) Public.random(100, 1000);
				lTimer = 0;
			}

		lTimer++;

		for (Droplet s : Droplets) {
			s.tick(wind, (double) speed, stop);
		}
		for (Cloud c : clouds)
			c.tick();
		if (l != null) {
			l.tick();
			if (l.alpha == 0)
				l = null;
		}
	}

	public void stop() {
		rain.Stop(true);
		stop = true;
	}
	
	public boolean done() {
		boolean out = false;
		
		for(Droplet s: Droplets) {
			if(s.start||s.done)
				out=true;
			else out=false;
		}
		
		return out;
	}
	
	public void start() {
		stop = false;
	}

	public void render(Graphics g, Graphics2D g2d) {
		for (Droplet s : Droplets) {
			s.render(g);
		}
		for (Cloud c : clouds)
			c.render(g);
		g.drawImage(ImageLoader.loadImage("/textures/StormOverlay.png"), 0, 0, null);
		if (l != null)
			l.render(g, g2d);
	}

	public class Droplet {

		Handler game;

		double x, y;
		double size;
		Color c;
		
		boolean done = false;
		
		double gravity = 16;

		boolean start = true;

		public Droplet(Handler game, int x, int y, double size) {

			this.game = game;

			this.x = x;
			this.y = y;
			this.size = size;

			gravity += size / 5;
			gravity += Public.debugRandom(-1, 1);

			c = new Color((int) Public.random(0, 5), (int) Public.random(0, 20), (int) Public.random(20, 100),
					(int) Public.random(50, 200));
		}

		public void tick(double wind, double speed, boolean stop) {

			Player p = game.getEntityManager().getPlayer();

			if (y - size / 2 > World.getHeight() * 18 - 1 || x > (World.getWidth() + 10) * 18 + 10 && x < -10
					|| (World.getTile(Public.grid(x, 18, 0), Public.grid(y + size, 18, 0)).isTop()
							|| World.getTile(Public.grid(x, 18, 0), Public.grid(y + size, 18, 0)).isSolid())
					|| (p.getCurrentTool() != null && p.getCurrentTool().getClass() == Umbrella.class
							&& x >= p.getX() - 10 && x <= p.getX() + p.getWidth() + 10 && y >= p.getY() - 30)) {
				if(!stop) {
					y = game.yOffset() - size;
					x = (Public.random(-360, 1080) + game.xOffset());
					start = false;
				} else {
					done = true;
				}
			}

			x += (wind / 2.00) * speed;
			y += (gravity / 2.00) * speed;
		}

		public void render(Graphics g) {

			int x = (int) (this.x - game.xOffset());
			int y = (int) (this.y - game.yOffset());

			g.setColor(c);
			g.fillRect(x, y, (int) Math.max(1, size / 4), (int) size);
		}

	}

	public class Cloud {

		public double speed;
		public int type;
		double x, y;
		int width, height;

		private BufferedImage cloud;

		int widthflip = 1, heightflip;

		public Cloud(Handler handler, double x, double y, int type, double speed) {
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.type = type;

			if (Math.random() < 0.5) {
				widthflip = -1;
			}

			if (Math.random() < 0.25 || Math.random() > 0.75) {
				heightflip = -1;
			}

			cloud = ImageLoader.loadImage("/textures/Statics/DarkCloud.png").getSubimage(0, type * 36, 54, 36);
			double d = Public.debugRandom(1, 2);
			width = (int) (54 * (d + Public.debugRandom(0, 0.5)));
			height = (int) (36 * (d + Public.debugRandom(0, 0.5)));
		}

		public void tick() {
			x += speed + game.getWorld().getEnviornment().getWind();
			if (x > World.getWidth() * Tile.TILEWIDTH + game.getWidth() / 2) {
				x = -54;
			}
		}

		public void render(Graphics g) {
			g.drawImage(Tran.flip(cloud, widthflip, heightflip), (int) (x - game.getGameCamera().getxOffset()),
					(int) (y - game.getGameCamera().getyOffset()), width, height, null);
		}

	}

	public class Lightning {

		Handler game;

		Sound crash;

		int x;
		int[] xs;
		int[] ys;
		int touchPoint;

		double alpha = 255;

		Polygon p;

		public Lightning(Handler game, int x) {
			this.x = x;

			this.game = game;

			int bends = 1;

			crash = new Sound("Sounds/Lightning.wav");

			// xs = new int[bends*2+4];
			// ys = new int[bends*2+4];
			//
			// xs[0]=x;
			// xs[1]=x-4;
			// xs[xs.length-2]=x;
			// xs[xs.length-1]=x-4;
			//
			// ys[0]=0;
			// ys[1]=0;
			// ys[ys.length-2]=game.getWorld().getHighest(x)*Tile.TILEHEIGHT;
			touchPoint = game.getWorld().getHighest(x) * Tile.TILEHEIGHT;
			//
			// for(int i=2; i<bends+2; i++) {
			// int nx = (int) Public.random(x-10, x+10);
			// xs[i]=nx;
			// xs[xs.length-i]=nx-4;
			// ys[i] = ((game.getWorld().getHighest(x)/bends)*(i-1))*Tile.TILEHEIGHT;
			// ys[ys.length-i] =
			// ((game.getWorld().getHighest(x)/bends)*(i-1))*Tile.TILEHEIGHT;
			// }

			// p[] = new Polygon(xs, ys, ys.length);

			if (bends == 1)
				create1();

			crash.Start(0, false);

		}

		private void create1() {
			int bendOff = (int) Public.random(x - 20, x + 20);

			int yOff = game.getHeight();

			int[] xp = new int[] { x, x - 4, bendOff - 4, x - 4, x, bendOff };
			int[] yp = new int[] { -yOff, -yOff, touchPoint / 2 - yOff / 2, touchPoint, touchPoint,
					touchPoint / 2 - yOff / 2 };

			p = new Polygon(xp, yp, 6);

		}

		public void tick() {
			alpha -= 5;
			alpha = Public.range(0, 255, alpha);
			
			crash.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
			crash.tick(false);
			if(OptionState.fxVolume==0)
				crash.setVolume(-80, false);
		}

		public void render(Graphics g, Graphics2D g2d) {
			g.setColor(new Color(200, 255, 255, (int) alpha));

			g2d.translate(-game.xOffset(), -game.yOffset());

			g.fillPolygon(p);

			g2d.setTransform(game.getGame().getDefaultTransform());

		}

	}

}
