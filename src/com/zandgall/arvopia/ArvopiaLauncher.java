package com.zandgall.arvopia;

import com.zandgall.arvopia.fileSetter.SetFiles;
import com.zandgall.arvopia.utils.Utils;

public class ArvopiaLauncher {

	public static Log log;
	public static Game game;
	
	public static final int[] width = new int[] {640, 720, 960, 1280, 1600, 1920, 2048, 2560}, height = new int[] {360, 400, 540, 720, 900, 1080, 1080, 1080};

	public static void main(String[] args) {
		
		Utils.createDirectory("C:\\Arvopia");
		Utils.createDirectory("C:\\Arvopia\\logs");
		Utils.createDirectory("C:\\Arvopia\\logs\\World");
		Utils.createDirectory("C:\\Arvopia\\logs\\Player");
		Utils.createDirectory("C:\\Arvopia\\logs\\Key Events");
		Utils.createDirectory("C:\\Arvopia\\logs\\FPSLogs");
		Utils.createDirectory("C:\\Arvopia\\logs\\Enviornment");
		Utils.createDirectory("C:\\Arvopia\\logs\\FileLoading");
		Utils.createDirectory("C:\\Arvopia\\Saves");
		Utils.createDirectory("C:\\Arvopia\\Recordings");
		
		SetFiles.fileSet();
		
		log = new Log("C:\\Arvopia\\logs\\Arvopia0.6 Test", "Main");
		
		game = new Game("Arvopia 0.5", 720, 405, false, log);
		game.start();

		
		
		log.log("Game Launched: Arvopia Alpha test");
	}
}
