package com.zandgall.arvopia.quests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.utils.Public;

public class AchievementManager {
	
	public ArrayList<Achievement> ach = new ArrayList<Achievement>();
	public static ArrayList<Achievement> al = new ArrayList<Achievement>();
	public static ArrayList<Achievement> left = new ArrayList<Achievement>();
	
	long timer = 250;
	
	public void Render(Graphics g) {
		for(Achievement a: ach) {
			g.setColor(Color.black);
			g.drawRect(600, -ach.indexOf(a)*55+350, 118, 50);
			g.setColor(Color.gray);
			g.fillRect(601, -ach.indexOf(a)*55+351, 117, 49);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Achievement:", 605, -ach.indexOf(a)*55+362);
			g.drawString(a.name, 605, -ach.indexOf(a)*55+372);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, 605, -ach.indexOf(a)*55+382);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", 605, -ach.indexOf(a)*55+392);
		}
	}
	
	
	public void tick() {
		if(ach.size()>0) {
			timer--;
			if(timer<=0) {
				ach.remove(0);
				timer = 250;
			}
		}
	}
	
	public void addForRemoval(Achievement a) {
		left.add(a); 
	}
	
	public void add(Achievement a) {
		ach.add(a);
		al.add(a);
		if(left.contains(a))
			left.remove(a);
		if(al.size()==Achievement.full.size()-1) {
			Achievement.award(Achievement.allachievements);
		}
	}
	
	public void addSilent(Achievement a) {
		al.add(a);
		if(left.contains(a))
			left.remove(a);
		if(al.size()==Achievement.full.size()-1) {
			Achievement.award(Achievement.allachievements);
		}
	}
	
}
