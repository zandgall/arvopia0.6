package com.zandgall.arvopia.quests;

import java.io.File;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.entity.statics.House;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Utils;

public class Quest {
	
	public static Quest getWoodForLia = new Quest("Get wood for Lia", "Get Lia her 10 pieces of wood", 100);
	public static Quest stoneForFancier = new Quest("Stone for Fancier", "Get Fancier 50 stone to revamp the houses", 200);
	public static Quest materialsForFrizzy = new Quest("Materials for Frizzy", "Get 50 wood, 50 stone, and 20 flower petals for Frizzy", 400);

	static Handler game;
	
	static QuestManager qm;
	
	static ArrayList<Quest> full;
	
	public String name, description;
	public int value;
	
	public Quest(String name, String description, int value) {
		this.name = name;
		this.description = description;
		this.value = value;
	}
	
	public static void init(Handler game, QuestManager qm) {
		Quest.game=game;
		
		File f = new File("C:\\Arvopia\\02.arv");
		if(!f.exists())
			Utils.fileWriter("0", "C:\\Arvopia\\02.arv");
		
		f = new File("C:\\Arvopia\\03.arv");
		if(!f.exists())
			Utils.fileWriter("", "C:\\Arvopia\\03.arv");
		
		f = new File("C:\\Arvopia\\04.arv");
		if(!f.exists())
			Utils.fileWriter("", "C:\\Arvopia\\04.arv");
		
		Quest.qm = qm;
		
		
		full = new ArrayList<Quest>();
		full.add(getWoodForLia);
		full.add(stoneForFancier);
		full.add(materialsForFrizzy);
		
		String s = LoaderException.readFile("C:\\Arvopia\\03.arv");
		String s2 = LoaderException.readFile("C:\\Arvopia\\03.arv");
		
		for(Quest a: full) {
			
			qm.addForRemoval(a);
			
			if(s.contains(a.name))
				qm.addSilent(a);
			if(s2.contains(a.name))
				qm.finSilent(a);
		}
		
	}
	
	public static Quest getQuest(String name) {
		for(Quest a: full)
			if(name.contains(a.name) || a.name.contains(name))
				return a;
		return null;
	}
	
	public static void begin(Quest a) {
		if(LoaderException.readFile("C:\\Arvopia\\03.arv", false)!=null&&LoaderException.readFile("C:\\Arvopia\\03.arv", false).contains(a.name))
			return;
		
		String bef = LoaderException.readFile("C:\\Arvopia\\03.arv");
		Utils.existWriter(bef+System.lineSeparator()+a.name, "C:\\Arvopia\\03.arv");
		
		Quest.qm.add(a);
	}
	
	public static void finish(Quest a) {
		if(LoaderException.readFile("C:\\Arvopia\\04.arv", false)!=null&&LoaderException.readFile("C:\\Arvopia\\04.arv", false).contains(a.name))
			return;
		
		int pre = Utils.parseInt(LoaderException.readFile("C:\\Arvopia\\02.arv", false));
		Utils.existWriter(""+(pre+a.value), "C:\\Arvopia\\02.arv");
		
		String bef = LoaderException.readFile("C:\\Arvopia\\04.arv");
		Utils.existWriter(bef+System.lineSeparator()+a.name, "C:\\Arvopia\\04.arv");
		
		extrawork(a);
		Quest.qm.fin(a);
	}
	
	public static boolean questcompletable(Quest a) {
		
		Player p = game.getEntityManager().getPlayer();
		
		if(a==stoneForFancier&&p.items[0]>=50)
			return true;
		if(a==materialsForFrizzy&&p.items[0]>=50&&p.items[1]>=50&&p.items[2]>=20)
			return true;
		return false;
			
	}
	
	public static void extrawork(Quest a) {
		if(a==stoneForFancier) {
			game.getEntityManager().getPlayer().items[0]-=50;
			game.getEntityManager().upgradeHouses();
		}
		if(a==materialsForFrizzy) {
			game.getEntityManager().addEntity(new House(game, 2071, 216, 3), true);
		}
	}
	
}
