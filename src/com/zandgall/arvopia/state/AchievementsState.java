package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.zandgall.arvopia.Game;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.quests.AchievementManager;
import com.zandgall.arvopia.quests.Quest;
import com.zandgall.arvopia.quests.QuestManager;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Utils;

public class AchievementsState extends State {
	
	
	double scroll;

	Button back;
	
	double scrollMax = 100;
	
	public AchievementsState(Handler game) {
		super(game);
		
		scroll = 0;
		back = new Button(handler, handler.getWidth()-60, handler.getHeight()-30, 55, 20, "Brings you back to the main menu", "Back");
	}

	
	
	@Override
	public void tick() {
		
		scrollMax = Math.max((AchievementManager.al.size()+AchievementManager.left.size())*120-handler.getWidth()+20, (QuestManager.al.size()+QuestManager.alfin.size())*220-handler.getWidth()+10);
		
		back.tick();
		
		if(back.on || handler.getKeyManager().esc) {
			State.setState(State.getPrev());
		}
		
		if(handler.getKeyManager().up && scroll>0)
			scroll-=5;
		if(handler.getKeyManager().down && scroll < scrollMax)
			scroll+=5;
		
		if(handler.getMouse().getMouseScroll()>0 && scroll>0 || (handler.getMouse().getMouseScroll()<0 && scroll<scrollMax))
			scroll-=handler.getMouse().getMouseScroll()*10;
		
		if(scroll< 0)
			scroll = 0;
		if(scroll > scrollMax)
			scroll = scrollMax;
	}

	@Override
	public void render(Graphics g, Graphics2D g2d) {
		g2d.setTransform(handler.getGame().getDefaultTransform());

		g.setColor(new Color(134, 200, 255));
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
		
		g2d.translate(-scroll, 0);
		
		int leftOff = 10;
		
		for(Achievement a: AchievementManager.al) {
			g.setColor(Color.black);
			g.drawRect(AchievementManager.al.indexOf(a)*120+10, 10, 118, 50);
			g.setColor(Color.gray);
			g.fillRect(AchievementManager.al.indexOf(a)*120+11, 11, 117, 49);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Achievement:", AchievementManager.al.indexOf(a)*120+10, 22);
			g.drawString(a.name, AchievementManager.al.indexOf(a)*120+10, 32);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, AchievementManager.al.indexOf(a)*120+10, 42);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", AchievementManager.al.indexOf(a)*120+10, 52);
			
			leftOff+=120;
		}
		
		int leftOff2 = 0;
		
		for(Quest a: QuestManager.al) {
			if(QuestManager.alfin.contains(a))
				continue;
			
			g.setColor(Color.black);
			g.drawRect(QuestManager.al.indexOf(a)*220+10, 100, 218, 50);
			g.setColor(Color.lightGray);
			g.fillRect(QuestManager.al.indexOf(a)*220+11, 101, 217, 49);
			g.setColor(Color.gray);
			g.fillRect(QuestManager.al.indexOf(a)*220+12, 102, 216, 48);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Started:", QuestManager.al.indexOf(a)*220+10, 112);
			g.drawString(a.name, QuestManager.al.indexOf(a)*220+10, 122);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, QuestManager.al.indexOf(a)*220+10, 132);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", QuestManager.al.indexOf(a)*220+10, 142);
			
			leftOff2+=220;
		}
		
		for(Achievement a: AchievementManager.left) {
			g.setColor(Color.black);
			g.drawRect(AchievementManager.left.indexOf(a)*120+leftOff, 10, 118, 50);
			g.setColor(Color.darkGray);
			g.fillRect(AchievementManager.left.indexOf(a)*120+leftOff+1, 11, 117, 49);
			
			g.setColor(Color.white);
			g.setFont(Public.defaultFont);
			g.drawString("Achievement:", AchievementManager.left.indexOf(a)*120+leftOff, 22);
			g.drawString("????", AchievementManager.left.indexOf(a)*120+leftOff, 32);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.hint, AchievementManager.left.indexOf(a)*120+leftOff, 42);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString("??", AchievementManager.left.indexOf(a)*120+leftOff, 52);
		}
		
		for(Quest a: QuestManager.alfin) {
			g.setColor(Color.black);
			g.drawRect(QuestManager.alfin.indexOf(a)*220+10+leftOff2, 100, 218, 50);
			g.setColor(Color.lightGray);
			g.fillRect(QuestManager.alfin.indexOf(a)*220+11+leftOff2, 101, 217, 49);
			g.setColor(Color.gray);
			g.fillRect(QuestManager.alfin.indexOf(a)*220+12+leftOff2, 102, 216, 48);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Finished:", QuestManager.alfin.indexOf(a)*220+12+leftOff2, 112);
			g.drawString(a.name, QuestManager.alfin.indexOf(a)*220+12+leftOff2, 122);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, QuestManager.alfin.indexOf(a)*220+12+leftOff2, 132);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", QuestManager.alfin.indexOf(a)*220+12+leftOff2, 142);
		}
		
		
		g2d.setTransform(handler.getGame().getDefaultTransform());
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("Game points: "+(Utils.parseInt(LoaderException.readFile("C:\\Arvopia\\00.arv", false))+Utils.parseInt(LoaderException.readFile("C:\\Arvopia\\02.arv", false))), 10, handler.getHeight()-10);
		g.drawString("Quests: ", 10, 80);
		
		back.render(g);
	}

	@Override
	public void init() {

	}

}
