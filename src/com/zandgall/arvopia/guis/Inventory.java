package com.zandgall.arvopia.guis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.utils.Public;

public class Inventory extends Gui{
	
	public static final int SPACEWIDTH = 20, SPACEHEIGHT = 20;
	
	private InventoryItem metal, petal, honey, foxFur, butterflyWing, wood, fineMetal, furCloth, charcoal, soil;
	private InventoryItem sword, axe, rockChipper, scythe, torch, umbrella;
	
	public ArrayList<InventoryItem> items;
	public ArrayList<InventoryItem> tools;
	
	private int[] itemNumbers;
	
	public Inventory(Handler game) {
		super(game);
		
		metal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Metal.png"), 1, 1, 0, 0);
		wood = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Wood.png"), 6, 1, 0, 1);
		petal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Petals.png"), 2, 1, 0, 2);
		honey = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Honey.png"), 3, 1, 0, 3);
		foxFur = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FoxFur.png"), 4, 1, 0, 4);
		butterflyWing = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/ButterflyWing.png"), 5, 1, 0, 5);
		fineMetal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FineMetal.png"), 7, 1, 0, 6);
		furCloth = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FurCloth.png"), 8, 1, 0, 7);
		charcoal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Charcoal.png"), 9, 1, 0, 8);
		items = new ArrayList<InventoryItem>();
		items.add(metal);
		items.add(petal);
		items.add(honey);
		items.add(foxFur);
		items.add(butterflyWing);
		items.add(wood);
		items.add(fineMetal);
		items.add(furCloth);
		items.add(charcoal);
		
		
		sword = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordThumbnail.png"), 1, 3, 0, -1);
		axe = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeThumbnail.png"), 2, 3, 0, -1);
		torch = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchThumbnail.png"), 3, 3, 0, -1);
		scythe = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Scythe/ScytheThumbnail.png"), 4, 3, 0, -1);
		rockChipper = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/RockChipper/RockChipperThumbnail.png"), 5, 3, 0, -1);
		umbrella = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Umbrella/UmbrellaThumbnail.png"), 6, 3, 0, -1);
		
		tools = new ArrayList<InventoryItem>();
	}

	@Override
	public void tick() {
		Player p = game.getWorld().getEntityManager().getPlayer();
		
		for(InventoryItem i: items) {
			i.tick(p.items[i.getID()]);
		}
		
		for(int i = 0; i<tools.size(); i++) {
			InventoryItem t = tools.get(i);
			tools.get(i).tick(-1);
			if(t.getX()>10 && t.getX()<210) {
				if(t.getY()>80&&t.getY()<100) {
					int pos = t.getX();
					for(int j = 0; j<game.getWorld().getEntityManager().getPlayer().toolbar.length; j++) {
						if(game.getWorld().getEntityManager().getPlayer().toolbar[j]==t.getID()) {
							game.getWorld().getEntityManager().getPlayer().toolbar[j]=-1;
						}
					}
					
					pos/=20;
					
					game.getWorld().getEntityManager().getPlayer().toolbar[(int) Public.range(0, 9, pos)]=t.getID();
					
				}
			}
		}
	}
	
	public void addTool(int id, int toolId) {
		if(id==-1) {
			sword.setID(toolId);
			sword.setPre((toolId+1)*20, 60);
			sword.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		if(id==-2) {
			torch.setID(toolId);
			torch.setPre((toolId+1)*20, 60);
			torch.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		if(id==-3) {
			axe.setID(toolId);
			axe.setPre((toolId+1)*20, 60);
			axe.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		if(id==-4) {
			rockChipper.setID(toolId);
			rockChipper.setPre((toolId+1)*20, 60);
			rockChipper.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/RockChipper/RockChipperThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		if(id==-5) {
			scythe.setID(toolId);
			scythe.setPre((toolId+1)*20, 60);
			scythe.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Scythe/ScytheThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		if(id==-6) {
			umbrella.setID(toolId);
			umbrella.setPre((toolId+1)*20, 60);
			umbrella.setPos(toolId+1, 3);
			tools.add(new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Tools/Umbrella/UmbrellaThumbnail.png"), toolId+1, 3, -1, toolId));
		}
		
	}

	@Override
	public void render(Graphics g) {
		g.setColor(new Color(0, 0, 200, 100));
		g.fillRect(0, 0, game.getWidth(), game.getHeight());
		
		g.setFont(Public.defaultFont);
		g.setColor(new Color(0, 255, 255));
		g.drawString((game.getWorld().getEnviornment().getHumidity()*100.0)+"% Chance of "+(game.getWorld().getEnviornment().getTemp()>=32 ? "Rain" : "Snow"), 200, 15);
		
		for(InventoryItem i: items) {
			i.render(g);
		}
		
		for(int i = 0; i<tools.size(); i++) {
			InventoryItem t = tools.get(i);
			t.render(g);
		}
		
		Player p = game.getWorld().getEntityManager().getPlayer();
		
		g.drawRect(20, 80, 200, 20);
		g.drawRect(21+p.selectedTool*20, 82, 16, 16);
		for(int i = 0; i<p.toolbar.length; i++) {
			if(p.toolbar[i]!=-1) {
				g.drawImage(p.tools.get(p.toolbar[i]).getThumbnail(), 20+i*20, 81, null);
			}
		}
		
		
	}

	@Override
	public void init() {
		
	}

}
