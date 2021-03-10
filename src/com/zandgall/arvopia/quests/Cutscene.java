package com.zandgall.arvopia.quests;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.Public;

public class Cutscene {
	
	public static Cutscene practice = new Cutscene("practice", 100, 100, 6);
	
	static Handler game;
	
	private String name;
	private double x, y, zoom;
	
	public Cutscene(String name, double x, double y, double zoom) {
		this.name = name;
		this.x=x;
		this.y=y;
		this.zoom=zoom;
	}
	
	public static void init(Handler game) {
		Cutscene.game=game;
	}
	
	public static boolean run(Cutscene c) {
		if(Public.difference(Game.scale, c.zoom)>2)
			
		if(Public.dist(game.xOffset(), game.yOffset(), c.x, c.y)<=2) {
			if(game.getMouse().isRight())
				return false;
		} else {
			if(game.xOffset()<c.x)
				game.getGameCamera().setxOffset((float) (((c.x+1)-game.xOffset())/10));
			if(game.xOffset()>c.x)
				game.getGameCamera().setxOffset((float) (((c.x-1)-game.xOffset())/10));
			
			if(game.yOffset()<c.y)
				game.getGameCamera().setxOffset((float) (((c.y+1)-game.yOffset())/10));
			if(game.yOffset()>c.y)
				game.getGameCamera().setxOffset((float) (((c.y-1)-game.yOffset())/10));
			
		}
		
		return true;
	}
	
}
