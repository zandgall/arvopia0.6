package com.zandgall.arvopia.quests;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.utils.Public;

public class QuestManager {
	
	public ArrayList<Quest> ach = new ArrayList<Quest>();
	public ArrayList<Quest> fin = new ArrayList<Quest>();
	public static ArrayList<Quest> al = new ArrayList<Quest>();
	public static ArrayList<Quest> alfin = new ArrayList<Quest>();
	public static ArrayList<Quest> left = new ArrayList<Quest>();
	
	long timer = 250;
	long finTimer=  250;
	
	public void Render(Graphics g) {
		for(Quest a: ach) {
			g.setColor(Color.black);
			g.drawRect(500, ach.indexOf(a)*55+10, 218, 50);
			g.setColor(Color.lightGray);
			g.fillRect(501, ach.indexOf(a)*55+11, 217, 49);
			g.setColor(Color.gray);
			g.fillRect(502, ach.indexOf(a)*55+12, 216, 48);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Quest Started:", 505, ach.indexOf(a)*55+22);
			g.drawString(a.name, 505, ach.indexOf(a)*55+32);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, 505, ach.indexOf(a)*55+42);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", 505, ach.indexOf(a)*55+52);
		}
		
		for(Quest a: fin) {
			g.setColor(Color.black);
			g.drawRect(500, fin.indexOf(a)*55+10, 218, 50);
			g.setColor(Color.lightGray);
			g.fillRect(501, fin.indexOf(a)*55+11, 217, 49);
			g.setColor(Color.gray);
			g.fillRect(502, fin.indexOf(a)*55+12, 216, 48);
			
			g.setColor(Color.black);
			g.setFont(Public.defaultFont);
			g.drawString("Quest Finished:", 505, fin.indexOf(a)*55+22);
			g.drawString(a.name, 505, fin.indexOf(a)*55+32);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(a.description, 505, fin.indexOf(a)*55+42);
			g.setFont(new Font("Arial", Font.BOLD, 10));
			g.drawString(a.value+"", 505, fin.indexOf(a)*55+52);
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
		
		if(fin.size()>0) {
			finTimer--;
			if(finTimer<=0) {
				fin.remove(0);
				finTimer = 250;
			}
		}
	}
	
	public void addForRemoval(Quest a) {
		left.add(a);
	}
	
	public void add(Quest a) {
		ach.add(a);
		al.add(a);
	}
	
	public void addSilent(Quest a) {
		al.add(a);
	}
	
	public void fin(Quest a) {
		fin.add(a);
		alfin.add(a);
	}
	
	public void finSilent(Quest a) {
		alfin.add(a);
	}
}
