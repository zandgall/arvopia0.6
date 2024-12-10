package com.zandgall.arvopia.quests;

import java.io.File;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Utils;

public class Achievement {
	
	public static Handler game;
	
	public static Achievement noname = new Achievement("No Name", "Name thyself!", "What is your name?", 50);
	public static Achievement firsttool = new Achievement("First Tool!", "Get your first tool", "Do you have a tool?", 100);
	public static Achievement somethingswrong = new Achievement("Something's wrong...", "Report a bug", "Is something wrong?", 100);
	public static Achievement hoarder = new Achievement("Hoarder", "Get 100 of any item", "Do you need more items?", 150);
	public static Achievement alltools = new Achievement("Go Getter", "Get one of every tool", "You need more tools", 200);
	public static Achievement firstquest = new Achievement("First Quest", "Finish your first quest", "Give to get", 100);
	public static Achievement shieldethfairmaiden = new Achievement("Shieldeth Fair Maiden", "Be the nice guy", "You aren't nice enough", 350);
	public static Achievement disrespectful = new Achievement("Disrespectful!", "Kill an NPC", "You are too nice", 200);
	public static Achievement allachievements = new Achievement("Is that all?", "Get all achievements", "You aren't finished", 500);
	
	static AchievementManager am;
	
	public String name, description, hint;
	public int value;
	
	static ArrayList<Achievement> full;
	
	public Achievement(String name, String description, String hint, int value) {
		this.name = name;
		this.hint = hint;
		this.description = description;
		this.value = value;
	}
	
	public static void init(Handler game, AchievementManager am) {
		Achievement.game=game;
		
		File f = new File("C:\\Arvopia\\00.arv");
		if(!f.exists())
			Utils.fileWriter("0", "C:\\Arvopia\\00.arv");
		
		f = new File("C:\\Arvopia\\01.arv");
		if(!f.exists())
			Utils.fileWriter("", "C:\\Arvopia\\01.arv");
		
		Achievement.am = am;
		
		
		full = new ArrayList<Achievement>();
		full.add(noname);
		full.add(hoarder);
		full.add(firsttool);
		full.add(alltools);
		full.add(somethingswrong);
		full.add(firstquest);
		full.add(shieldethfairmaiden);
		full.add(disrespectful);
		full.add(allachievements);
	
		
		String s = LoaderException.readFile("C:\\Arvopia\\01.arv");
		
		for(Achievement a: full) {
			
			am.addForRemoval(a);
			
			if(s.contains(a.name))
				am.addSilent(a);
		}
		
	}	
	
	public static void award(Achievement a) {
		if(LoaderException.readFile("C:\\Arvopia\\01.arv", false)!=null&&LoaderException.readFile("C:\\Arvopia\\01.arv", false).contains(a.name))
			return;
		
		Achievement.game.log("CONGRATULATION! YOU WERE JUST AWARDED "+a.name+": " + a.description);
		Achievement.game.log("You earned: "+a.value);
		
		int prev = Utils.parseInt(LoaderException.readFile("C:\\Arvopia\\00.arv", false));
		Utils.fileWriter(""+(prev+a.value), "C:\\Arvopia\\00.arv");
		
		String bef = LoaderException.readFile("C:\\Arvopia\\01.arv");
		Utils.existWriter(bef+System.lineSeparator()+a.name, "C:\\Arvopia\\01.arv");
		
		Achievement.am.add(a);
	}
	
}
