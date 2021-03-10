package com.zandgall.arvopia.enviornment.weather;

import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

public class Snow {
	
	public static double wind = 0;
	
	public ArrayList<SnowFlake> snowFlakes;
	public int ammount;
	
	public Snow(Handler game, int ammount) {
		snowFlakes = new ArrayList<SnowFlake>();
		this.ammount = ammount;
		for(int i = 0; i<ammount; i++) {
			snowFlakes.add(new SnowFlake(game, Public.random(-360, 1080), Public.random(-400, 0), Public.random(0.1, 5)));
		}
	}
	
	public void tick(int speed) {
		for(SnowFlake s: snowFlakes) {
			s.tick(wind, speed);
		}
	}
	
	public void melt() {
		if(Math.random()<0.1) {
			snowFlakes.remove(snowFlakes.size()-1);
		}
	}
	
	public void add(Handler game) {
		if(Math.random()<0.1) {
			snowFlakes.add(new SnowFlake(game, Public.random(-360, 1080), Public.random(-400, 0), Public.random(0.1, 5)));
		}
	}
	
	public void render(Graphics g) {
		for(SnowFlake s: snowFlakes) {
			s.render(g);
		}
	}
	
}