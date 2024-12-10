package com.zandgall.arvopia.utils;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.zandgall.arvopia.ArvopiaLauncher;


public class LoaderException {
//	@WillNotClose //this is a JSR 305 annotation
	public static InputStream loadResource(final String resourcePath)
		    throws IOException
		{	
		    final URL url = LoaderException.class.getResource(resourcePath);
		    if (url == null)
		        throw new IOException(resourcePath + ": resource not found");
		    return url.openStream();
		}
	
	public static String streamToString(final InputStream is, final int bufferSize) {
	    final char[] buffer = new char[bufferSize];
	    final StringBuilder out = new StringBuilder();
	    try (Reader in = new InputStreamReader(is, "UTF-8")) {
	        for (;;) {
	            int rsz = in.read(buffer, 0, buffer.length);
	            if (rsz < 0)
	                break;
	            out.append(buffer, 0, rsz);
	        }
	    }
	    catch (UnsupportedEncodingException ex) {
	        /* ... */
	    }
	    catch (IOException ex) {
	        /* ... */
	    }
	    return out.toString();
	}
	
	public static String olderreadFile(String path) {
		String output = "";
		
		if(ArvopiaLauncher.game!=null && ArvopiaLauncher.game.handler !=null && ArvopiaLauncher.game.handler.filelogger!=null)
			ArvopiaLauncher.game.handler.logFiles("Reading: " + path);
		
		try {
			output = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(ArvopiaLauncher.game!=null && ArvopiaLauncher.game.handler !=null && ArvopiaLauncher.game.handler.filelogger!=null)
			ArvopiaLauncher.game.handler.logFiles("File read: " + output);
		
		return output;
	}
	
	public static String readFile(String path) {
		String output = null;
		
		try {
			File file = new File(path); 
			System.out.println("File chosen: "+file.getAbsolutePath()+" is "+file.exists());
			Scanner sc = new Scanner(file);	
			
			sc.useDelimiter("//Z");
			
			if(sc.hasNext())
				output = sc.next();
			else output = "couldn't read";
			
			System.out.println("File loaded: "+output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return output;
	}

	public static String readFile(String path, boolean doesntmatter) {
String output = null;
		
		try {
			File file = new File(path); 
			Scanner sc = new Scanner(file);	
			
			sc.useDelimiter("//Z");
			
			if(sc.hasNext())
				output = sc.next();
			else output = "couldn't read";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return output;
	}
}
