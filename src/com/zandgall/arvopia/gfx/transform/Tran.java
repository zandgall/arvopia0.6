package com.zandgall.arvopia.gfx.transform;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tran {
	
	static Map<Map<BufferedImage, Integer>, BufferedImage> quickie = new HashMap<Map<BufferedImage, Integer>, BufferedImage>();
	
	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at, double width, double height) {
		BufferedImage newImage = new BufferedImage(image.getWidth()*(int) Math.ceil(width), image.getHeight()*(int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at, double width, double height, int imageHeight, int imageWidth) {
		BufferedImage newImage = new BufferedImage(imageWidth*(int) Math.ceil(width), imageHeight*(int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
	public static BufferedImage combine(BufferedImage image1, BufferedImage image2) {
		// create the new image, canvas size is the max. of both image sizes
		int w = Math.max(image1.getWidth(), image2.getWidth());
		int h = Math.max(image1.getHeight(), image2.getHeight());
		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(image1, 0, 0, null);
		g.drawImage(image2, 0, 0, null);
		return combined;
	}
	
	public static BufferedImage flip(BufferedImage image, int width, int height, int imageWidth, int imageHeight) {
		
		Map<BufferedImage, Integer> map = new HashMap<BufferedImage, Integer>();
		map.put(image, width);
		
		if(quickie.size()>0 && quickie.containsKey(map)) { 
			return quickie.get(map);
		}
		
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(width, height));
		if(height==1)
			height=0;
		if(width==1)
			width=0;
		at.concatenate(AffineTransform.getTranslateInstance(width*imageWidth, height*imageHeight));
		
		BufferedImage out = createTransformed(image, at, 1, 1, imageWidth, imageHeight);
		
		quickie.put(map, out);
		
		return out;
	}
	
	public static BufferedImage flip(BufferedImage image, int width, int height) {
		
		Map<BufferedImage, Integer> map = new HashMap<BufferedImage, Integer>();
		map.put(image, width);
		
		if(quickie.size()>0 && quickie.containsKey(map)) { 
			return quickie.get(map);
		}
		
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(width, height));
		if(height==1)
			height=0;
		if(width==1)
			width=0;
		at.concatenate(AffineTransform.getTranslateInstance(width*image.getWidth(), height*image.getHeight()));
		
		BufferedImage out = createTransformed(image, at, 1, 1);
		
		quickie.put(map, out);
		
		return out;
	}
	
	public static BufferedImage scale(BufferedImage image, double width, double height) {
		
		AffineTransform at = new AffineTransform();
		at.scale(width, height);
		
		BufferedImage out = createTransformed(image, at, width, height);
		
		return out;
	}
	
	public static Rectangle toRect(BufferedImage image, int xOff, int yOff) {
		return new Rectangle(xOff, yOff, image.getWidth(), image.getHeight());
	}
}
