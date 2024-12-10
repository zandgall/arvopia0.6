package com.zandgall.arvopia.guis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.items.tools.*;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.Public;

public class ForgeGui extends Gui {

	private Map<InventoryItem, Integer> items;
	private ArrayList<CraftOutput> outputs;
	int selected = 0;
	Button craft;

	public ForgeGui(Handler game) {
		super(game);
		items = new HashMap<InventoryItem, Integer>();
		
		ForgeHandler.init(game);
		outputs = ForgeHandler.getOutputs();
		
		craft = new Button(game, 185, 140, 55, 20, "Crafts the selected item IF you are able to", "Craft");
		
		
	}

	@Override
	public void tick() {
		craft.on=false;
		craft.tick();
		items.clear();
//		if(game.getEntityManager().getPlayer().tools.size()==0)
//			ForgeHandler.crafted(game, ForgeHandler.umbrella);
		for(InventoryItem i: game.getWorld().getEntityManager().getPlayer().inventory.items) {
			if(game.getEntityManager().getPlayer().items[i.getID()]>0)
				items.put(i, game.getWorld().getEntityManager().getPlayer().items[i.getID()]);
		}
		
		if(craft.on)
			if(outputs.get(selected).craftable(items))
				ForgeHandler.crafted(game, outputs.get(selected));
		
		int y = 50;
		for(int i = 0; i < outputs.size(); i++) {
			if(outputs.get(i).craftable(items)) {
				outputs.get(i).x = 30;
				outputs.get(i).y = y;
				outputs.get(i).tick();
				y += 20;
				
				if(outputs.get(i).clicked) {
					selected = i;
				}
				
			}
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(new Color(0, 0, 200, 100));
		g.fillRect(0, 0, game.getWidth(), game.getHeight());
		
		int y = 50;
		for (InventoryItem i : items.keySet()) {
			if (i.amount > 0) {
				i.render(g, 1, y, 18, 18);
				y += 20;
			}
		}
		for(CraftOutput c: outputs) {
			if(c.craftable(items)) {
				c.render(g);
			}
		}
		
		if(!craft.on)
			if(!outputs.get(selected).craftable(items))
				craft.on=true;
		craft.render(g);
		int x = 61;
		g.drawImage(outputs.get(selected).image, 60, 60, 100, 100, null);
		g.setColor(Color.black);
		g.drawRect(60, 60, 100, 100);
		
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString(outputs.get(selected).name, 185, 80);
		g.setFont(Public.defaultFont);
		g.drawString(outputs.get(selected).description, 185, 90);
		
		for(InventoryItem i: outputs.get(selected).recipe.recipe.keySet()) {
			i.amount = outputs.get(selected).recipe.recipe.get(i);
			i.render(g, 161, x, 18, 18);
			x+=20;
		}
	}

	@Override
	public void init() {

	}

	public static class ForgeHandler {

		// Raw Materials
		public static InventoryItem metal, petal, honey, foxFur, butterflyWing, wood;

		// Materials
		public static CraftOutput fineMetal, furCloth, charcoal;
		public static InventoryItem ifineMetal, ifurCloth, icharcoal;

		// Clothing
		public static CraftOutput furCap, furCoat, furPants, furBoots;

		// Weapons
		public static CraftOutput sword, torch, axe, rockChipper, scythe, umbrella;

		public static void init(Handler game) {

			metal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Metal.png"), 1, 1, 0, 0);
			wood = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Wood.png"), 6, 1, 0, 1);
			petal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Petals.png"), 2, 1, 0, 2);
			honey = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Honey.png"), 3, 1, 0, 3);
			foxFur = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FoxFur.png"), 4, 1, 0, 4);
			butterflyWing = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/ButterflyWing.png"), 5,
					1, 0, 5);
			ifineMetal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FineMetal.png"), 7, 1, 0,
					6);
			ifurCloth = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/FurCloth.png"), 8, 1, 0, 7);
			icharcoal = new InventoryItem(game, ImageLoader.loadImage("/textures/Inventory/Charcoal.png"), 9, 1, 0, 8);

			CraftOutput empty = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Wood.png"), 6, 1, 0,
					null);

			sword = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordThumbnail.png"), 120,
					30, -1, empty.new Recipe(getMap(new InventoryItem[] { wood, ifineMetal }, new int[] { 2, 4 })));
			sword.setValues("Sword", "A nice basic weapon with 3 damage and 15 attack delay");
			torch = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchThumbnail.png"),
					120, 30, -2, empty.new Recipe(getMap(new InventoryItem[] { wood, icharcoal }, new int[] { 3, 1 })));
			torch.setValues("Torch", "A flaming stick that guides your path and has 2 damage and 15 attack delay");
			axe = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeThumbnail.png"), 120,
					30, -3, empty.new Recipe(getMap(new InventoryItem[] { wood, ifineMetal }, new int[] { 3, 7 })));
			axe.setValues("Axe", "A good sturdy tool, useful for chopping down trees,\nand for cutting down enimies with 4 damage and 20 attack delay");
			rockChipper = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/RockChipper/RockChipperThumbnail.png"), 120,
					30, -4, empty.new Recipe(getMap(new InventoryItem[] { wood, ifineMetal }, new int[] { 3, 5 })));
			rockChipper.setValues("Rock Chipper", "A pickaxe-like tool used for smashing and cutting rocks");
			scythe = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/Scythe/ScytheThumbnail.png"), 120,
					30, -5, empty.new Recipe(getMap(new InventoryItem[] { wood, ifineMetal }, new int[] { 3, 10 })));
			scythe.setValues("Scythe", "A weapon of death.\n\n And for cropping flowers :)");
			umbrella = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Tools/Umbrella/UmbrellaThumbnail.png"), 120,
					30, -6, empty.new Recipe(getMap(new InventoryItem[] { wood, ifurCloth }, new int[] { 3, 4 })));
			umbrella.setValues("Umbrella", "Keeps you dry");
			
			
			fineMetal = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/FineMetal.png"), 120, 30, 6,
					empty.new Recipe(getMap(new InventoryItem[] { metal }, new int[] { 4 })));
			fineMetal.setValues("Fine Metal", "A material made entirely out of raw metal. This is used to make any item or tool made out of metal");
			furCloth = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/FurCloth.png"), 120, 30, 7,
					empty.new Recipe(getMap(new InventoryItem[] { foxFur }, new int[] { 4 })));
			furCloth.setValues("Fur Cloth", "Made out of fox fur, used to make clothing and umbrellas.");
			charcoal = new CraftOutput(game, ImageLoader.loadImage("/textures/Inventory/Charcoal.png"), 120, 30, 8,
					empty.new Recipe(getMap(new InventoryItem[] { metal, wood }, new int[] { 1, 1 })));
			charcoal.setValues("Charcoal", "A  burning material useful for making torches or burning other materials");

		}

		public static void crafted(Handler game, CraftOutput c) {
			int id = c.id;
			Player p = game.getWorld().getEntityManager().getPlayer();
			if (id >= 0) {
				p.items[id]++;
			} else {
				
				p.inventory.addTool(id, p.tools.size());
				
				switch (id) {
				case -1:
					p.tools.add(new Sword(game));
					break;
				case -2:
					p.tools.add(new Torch(game));
					break;
				case -3:
					p.tools.add(new Axe(game));
					break;
				case -4:
					p.tools.add(new RockChipper(game));
					break;
				case -5:
					p.tools.add(new Scythe(game));
					break;
				case -6:
					p.tools.add(new Umbrella(game));
					break;
				}
				
				p.has+=""+id;
				
				for(int i = 0; i < p.toolbar.length; i++) {
					if(p.toolbar[i]==-1) {
						p.toolbar[i]=p.tools.size()-1;
						break;
					}
				}
			}
			
			c.doSubtract();

		}

		private static Map<InventoryItem, Integer> getMap(InventoryItem[] keys, int[] values) {
			Map<InventoryItem, Integer> out = new HashMap<InventoryItem, Integer>();
			for (int i = 0; i < keys.length; i++) {
				out.put(keys[i], values[i]);
			}
			return out;
		}

		public static ArrayList<CraftOutput> getOutputs() {
			ArrayList<CraftOutput> out = new ArrayList<CraftOutput>();
			out.add(fineMetal);
			out.add(furCloth);
			out.add(charcoal);
			out.add(sword);
			out.add(torch);
			out.add(axe);
			out.add(rockChipper);
			out.add(scythe);
			out.add(umbrella);
			return out;
		}

	}

}
