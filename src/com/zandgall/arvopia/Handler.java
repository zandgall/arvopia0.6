package com.zandgall.arvopia;

import java.awt.Cursor;

import com.zandgall.arvopia.display.Display;
import com.zandgall.arvopia.entity.EntityManager;
import com.zandgall.arvopia.enviornment.Enviornment;
import com.zandgall.arvopia.gfx.GameCamera;
import com.zandgall.arvopia.input.KeyManager;
import com.zandgall.arvopia.input.MouseManager;
import com.zandgall.arvopia.state.OptionState;
import com.zandgall.arvopia.state.State;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.worlds.World;

public class Handler {

	public Log player, worldl, keyEvent, fpsLogger, enviornmentl, filelogger;

	private Game game;
	private World world;

	public Game getGame() {
		return game;
	}
	
	public void setCursor(Cursor cursor) {
		game.getDisplay().getFrame().setCursor(cursor);
	}
	
	public OptionState options() {
		return (OptionState) game.optionState;
	}
	
	public void setVolume() {
		State.setVolume(Public.Map(getVolume(), 100, 0, 6, -80));
	}

	public int getWidth() {
		return game.getWidth();
	}

	public int getHeight() {
		return game.getHeight();
	}

	public GameCamera getGameCamera() {
		return game.getGameCamera();
	}
	
	public float xOffset() {
		return (float) game.getGameCamera().getxOffset();
	}
	
	public float yOffset() {
		return (float) game.getGameCamera().getyOffset();
	}

	public KeyManager getKeyManager() {
		return game.getKeyManager();
	}

	public MouseManager getMouse() {
		return game.getMouse();
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public World getWorld() {
		return world;
	}
	
	public Enviornment getEnviornment() {
		return world.getEnviornment();
	}
	
	public EntityManager getEntityManager() {
		return world.getEntityManager();
	}
	
	public double getWind() {
		return world.getEnviornment().getWind();
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Handler(Game game) {
		this.game = game;
	}

	public void log(String string) {
		Game.log.log(string);
	}
	
	public Display display() {
		return game.getDisplay();
	}

	public double getVolume() {
		OptionState o = (OptionState) game.optionState;
		
		return o.volume.getValue();
	}
	
	public void init() {
//		if(!player.exists())
			player = new Log("C:\\Arvopia\\logs\\Player\\player.txt", "Player");
//		if(!worldl.exists())
			worldl = new Log("C:\\Arvopia\\logs\\World\\world.txt", "World");
//		if(!keyEvent.exists())
			keyEvent = new Log("C:\\Arvopia\\logs\\Key Events\\keyEvent.txt", "Keys");
			
		fpsLogger = new Log("C:\\Arvopia\\logs\\FPSLogs\\Fps.txt", "Fps");
		
		enviornmentl = new Log("C:\\Arvopia\\logs\\Enviornment\\Enviornment.txt", "Enviornment");
		
		filelogger = new Log("C:\\Arvopia\\logs\\FileLoading\\Files.txt", "Files");
	}

	public void logPlayer(String string) {
		player.log(string);
	}

	public void logWorld(String string) {
		worldl.logSilent(string);
	}

	public void logKeys(String string) {
		keyEvent.log(string);
	}

	public void logWorldSilent(String string) {
		worldl.logSilent(string);
	}
	
	public void logEnviornment(String string) {
		enviornmentl.log(string);
	}
	
	public void logEnviornmentSilent(String string) {
		enviornmentl.logSilent(string);
	}

	public void logSilent(String message) {
		Game.log.logSilent(message);
	}
	
	public void saveFps(int fps) {
		fpsLogger.logSilent("FPS: "+fps);
	}
	
	public void logFiles(String string) {
		filelogger.logSilent(string);
	}
	
	public void logFilesSilent(String string) {
		filelogger.logSilent(string);
	}
}
