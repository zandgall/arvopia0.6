package com.zandgall.arvopia.enviornment.weather;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.items.tools.Umbrella;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.worlds.World;

public class Rain {

	public static double wind = 0;

	ArrayList<Droplet> Droplets;

	public Sound rain;
	
	public boolean stop;

	public Rain(Handler game, int ammount) {
		Droplets = new ArrayList<Droplet>();
		for (int i = 0; i < ammount; i++) {
			Droplets.add(new Droplet(game, (int) Public.random(-360, 1080), (int) Public.random(-400, 0),
					Public.random(2, 10)));
		}

		rain = new Sound("Sounds/Rain.wav");
		rain.setVolume(-80, false);	
	}

	public void tick(int speed) {
		
		if (rain.hasEnded()) {
			rain.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
			rain.Start(0, true);
		}

		rain.setVolume((int) Public.Map(OptionState.fxVolume, 100, 0, 6, -40), false);
		rain.tick(false);
		if (OptionState.fxVolume == 0)
			rain.setVolume(-80, false);
		
		for (Droplet s : Droplets) {
			s.tick(wind, (double) speed, stop);
		}
	}
	
	public void stop() {
		stop = true;
		rain.Stop(false);
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
		rain.Start(0, false);
	}

	public void render(Graphics g) {
		for (Droplet s : Droplets) {
			s.render(g);
		}
	}

	public class Droplet {

		Handler game;

		double x, y;
		double size;
		Color c;

		double gravity = 9;

		boolean start = true;

		boolean done = false;
		
		public Droplet(Handler game, int x, int y, double size) {

			this.game = game;

			this.x = x;
			this.y = y;
			this.size = size;

			gravity += size / 5;
			gravity += Public.debugRandom(-1, 1);

			c = new Color((int) Public.random(0, 10), (int) Public.random(0, 50), (int) Public.random(50, 255),
					(int) Public.random(50, 200));
		}

		public void tick(double wind, double speed, boolean stop) {

			Player p = game.getEntityManager().getPlayer();

			if (y - size / 2 > World.getHeight() * 18 - 1 || x > (World.getWidth() + 10) * 18 + 10 && x < -10
					|| (World.getTile(Public.grid(x, 18, 0), Public.grid(y + size, 18, 0)).isTop()
							|| World.getTile(Public.grid(x, 18, 0), Public.grid(y + size, 18, 0)).isSolid())
					|| (p.getCurrentTool() != null && p.getCurrentTool().getClass() == Umbrella.class
							&& x >= p.getX() - 10 && x <= p.getX() + p.getWidth() + 40 && y >= p.getY() - 40)) {
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

}
