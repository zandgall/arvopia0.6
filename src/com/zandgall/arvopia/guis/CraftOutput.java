package com.zandgall.arvopia.guis;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import com.zandgall.arvopia.Handler;

public class CraftOutput {
	
	BufferedImage image;
	
	int x, y, ox;
	
	Handler game;
	
	boolean clicked;
	
	Recipe recipe;
	int id;
	
	public String name = "unset", description = "unset";
	
	public CraftOutput(Handler handler, BufferedImage image, int x, int y, int id, Recipe recipe) {
		this.image= image;
		
		this.id = id;
		this.recipe = recipe;
		
		this.x = x;
		this.y = y;
		ox = x;
		
		
		game = handler;
	}
	
	public void setValues(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	
	
	public void tick() {
		clicked = false;
		if(game.getMouse().isLeft()) {
			if(game.getMouse().rMouseX() > x && game.getMouse().rMouseX() < x+image.getWidth() && game.getMouse().rMouseY() > y && game.getMouse().rMouseY()<y+image.getHeight()) {
				clicked = true;
			}
		}
	}
	
	public boolean craftable(Map<InventoryItem, Integer> list) {
		return recipe.craftable(id, game, list);
	}
	
	public void render(Graphics g) {
		g.drawImage(image, x, y, null);
	}
	
	public void doSubtract() { 
		for(InventoryItem i : recipe.recipe.keySet()) {
			game.getWorld().getEntityManager().getPlayer().items[i.getID()]-=recipe.recipe.get(i);
		}
	}
	
	public class Recipe {
		
		Map<InventoryItem, Integer> recipe;
		
		public Recipe(Map<InventoryItem, Integer> recipe) {
			this.recipe = recipe;
		}
		
		public boolean craftable(int id, Handler game, Map<InventoryItem, Integer> contents) {
			
			boolean out = false;
			boolean thereishope = false;
			
			for(InventoryItem o: recipe.keySet()) {
				thereishope = false;
				for(InventoryItem i: contents.keySet()) {
					if(i.getID()!=o.getID())
						continue;
					else thereishope = true;
					
//					if(game.getKeyManager().b) {
//						game.log(id + " " + (i.getID() == o.getID()) + " " + (contents.get(i)>=recipe.get(o)));
//						game.log(id+" " + i.getID() + " " + o.getID());
//					}
					
					if(contents.get(i)>=recipe.get(o)) {
						out = true;
					}
					else return false;
				}
				if(thereishope==false) {
					return false;
				}
			}
			return out;
		}
	}
	
}
