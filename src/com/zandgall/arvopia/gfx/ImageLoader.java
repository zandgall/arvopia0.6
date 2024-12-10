package com.zandgall.arvopia.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;

public class ImageLoader {
	
	static Handler game = ArvopiaLauncher.game.handler;
	
	static Map<String, BufferedImage> mem = new HashMap<String, BufferedImage>();
	
	public static BufferedImage loadImage(String path) {
		
		if(mem.containsKey(path))
			return mem.get(path);
		
		try {
			System.out.println("		"+path+" loaded");
			BufferedImage out = ImageIO.read(ImageLoader.class.getResource(path));
			mem.put(path, out);
			return out;
		} catch (Exception e) {
			game.log(""+e.getMessage());
			e.printStackTrace();
			Reporter.quick("Could not load "+path+", this could be a big problem");
			return new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		}
		
	}
	
}
