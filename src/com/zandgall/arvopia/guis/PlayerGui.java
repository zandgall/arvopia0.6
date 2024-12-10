package com.zandgall.arvopia.guis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.entity.creatures.Player;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.state.TitleState;

public class PlayerGui extends Gui{
	
	Player p;
	
	private BufferedImage player, sword, torch, fists, axe, crafting, inventory;
	
	public boolean activeSword, activeTorch, activeAxe;
	
	public PlayerGui(Handler game) {
		super(game);
		p = game.getWorld().getEntityManager().getPlayer();
		
		player = ImageLoader.loadImage("/textures/Player/"+TitleState.appearance+"/Player.png").getSubimage(9, 7, 18, 18);
		
		sword = ImageLoader.loadImage("/textures/Inventory/Tools/Sword/SwordStab.png");
		torch = ImageLoader.loadImage("/textures/Inventory/Tools/Torch/TorchStab.png").getSubimage(0, 0, 36, 21);
		fists = ImageLoader.loadImage("/textures/Player/"+TitleState.appearance+"/PlayerPunch.png").getSubimage(49, 19, 18, 18);
		axe = ImageLoader.loadImage("/textures/Inventory/Tools/Axe/AxeSmash.png").getSubimage(0, 36, 36, 13);
		crafting = ImageLoader.loadImage("/textures/CraftingIcon.png");
		inventory = ImageLoader.loadImage("/textures/InventoryIcon.png");
	}

	@Override
	public void tick() {
		if(p != game.getWorld().getEntityManager().getPlayer()) {
			p = game.getWorld().getEntityManager().getPlayer();
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, 18, 18);
		g.drawImage(player, 0, 0, null);
		g.setColor(Color.black);
		g.drawRect(0, 0, 18, 18);
		g.setFont(new Font("Dialog", Font.BOLD, 12));
		g.drawString("Lives: "+p.lives, 20, 18);
		
		g.drawRect(1, 20, 100, 10);
		g.setColor(Color.red);
		g.fillRect(1, 20, 100, 10);
		g.setColor(Color.green);
		g.fillRect(1, 20, (int) (100*(p.health/p.MAX_HEALTH)), 10);
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.drawString("Health: "+(int) p.health, 3, 29);
		
		if(p.breath<20) {
			g.drawRect(1, 35, 100, 10);
			g.setColor(Color.white);
			g.fillRect(1, 35, 100, 10);
			g.setColor(Color.blue);
			g.fillRect(1, 35, (int) (100*(p.breath/20)), 10);
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString("Breath: "+(int) p.breath, 3, 44);
		}
		
		g.drawRect(100, 0, 200, 20);
		g.drawRect(101+p.selectedTool*20, 2, 16, 16);
		for(int i = 0; i<p.toolbar.length; i++) {
			if(p.toolbar[i]!=-1) {
				g.drawImage(p.tools.get(p.toolbar[i]).getThumbnail(), 100+i*20, 1, null);
				g.drawString(""+(i+1), 100+i*20, 20);
			}
		}
		
		
		
		g.drawImage(inventory, 100, game.getHeight()-20, null);
		g.drawImage(crafting, 190, game.getHeight()-20, null);
		
		
	}

	@Override
	public void init() {
		
	}
	
}
