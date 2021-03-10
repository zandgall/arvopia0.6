package com.zandgall.arvopia.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.zandgall.arvopia.Handler;
import com.zandgall.arvopia.Reporter;
import com.zandgall.arvopia.gfx.ImageLoader;
import com.zandgall.arvopia.gfx.PublicAssets;
import com.zandgall.arvopia.quests.Achievement;
import com.zandgall.arvopia.utils.ArraySlider;
import com.zandgall.arvopia.utils.Button;
import com.zandgall.arvopia.utils.LoaderException;
import com.zandgall.arvopia.utils.Public;
import com.zandgall.arvopia.utils.Sound;
import com.zandgall.arvopia.utils.TextEditor;
import com.zandgall.arvopia.utils.Utils;

public class TitleState extends State {

//	private int mouseX;
//	private int mouseY;

	public static String appearance = "Male";
	
	
	public Sound titleSong;
	
	
	private Button start, quit, options, changelog, report, howToPlay, achievements;
	private ArraySlider playerAppearance;
	
	
	private TextEditor user;
	
//	private BufferedImage[] info;
//	private String[][] infoDesc;
	
	private BufferedImage title;
	
	public TitleState(Handler handler) {
		super(handler);
		
		titleSong = new Sound("Songs/Title.wav");
		
		PublicAssets.init();
		
//		info = new BufferedImage[] {PublicAssets.grass};  s
//		infoDesc = new String[][] {{"Grass is the most common ", "tile in the game. There is ", "nothing interesting ", "about Grass"}};
		
		user = new TextEditor(handler, 5, 173, 150, 1, Reporter.user); 
		
		title = ImageLoader.loadImage("/textures/Title.png");
		
		options = new Button(handler, handler.getWidth()/2 - 30, handler.getHeight()/2, 80, 25, "Takes you to the options menu", "Options");
		achievements = new Button(handler, handler.getWidth()/2 - 55, handler.getHeight()/2 + 30, 130, 25, "Takes you to the achievements menu", "Achievements");
		start = new Button(handler, handler.getWidth()/2-16, handler.getHeight()/2-30, 54, 25, "Starts the game by loading in the world", "Start");
		quit = new Button(handler, handler.getWidth()/2 - 10, handler.getHeight()/2 + 150, 45, 25, "Quits the application", "Quit");
		changelog = new Button(handler, handler.getWidth()/2 - 40, handler.getHeight()/2 + 90, 100, 25, "View the changelog", "Changelog");
		report = new Button(handler, handler.getWidth()/2 - 50, handler.getHeight()/2  + 120, 120, 25, "Opens a bug report", "Report a bug");
		howToPlay = new Button(handler, handler.getWidth()/2 - 50, handler.getHeight()/2 + 60, 120, 25, "View the instructions", "How to play");
		
		playerAppearance = new ArraySlider(handler, new String[] {"Male", "Female"}, 0, true, "Appearance");
		
		titleSong.Start(-1, true);
		setSong(titleSong);
		
		handler.setVolume();
		
		handler.getMouse().setHandler(handler);
	}

	@Override
	public void tick() {
		
		options.tick();
		achievements.tick();
		start.tick();
		quit.tick();
		changelog.tick();
		report.tick();
		howToPlay.tick();
		user.tick();
		playerAppearance.tick(10, 200);
		
//		player.
		
		appearance = playerAppearance.getValue();
		
		if(report.on)
			State.setState(handler.getGame().reportingState);
		
		if(howToPlay.on) {
			State.setState(handler.getGame().instructionsState);
			player.fadeOut();
		}
		
		if(changelog.on) {
			State.setState(handler.getGame().changelogState);
			player.fadeOut();
		}
		
		if(options.on) {
			State.setState(handler.getGame().optionState);
			player.fadeOut();
		}
			
		if(achievements.on) {
			State.setState(handler.getGame().achievementsState);
		}
		if(quit.on)
			handler.getGame().stop();
		
		if (start.on) {
			State.setState(handler.getGame().worldState); 
			player.fadeOut();
		}
		
		if(start.hovered || quit.hovered || options.hovered || changelog.hovered || howToPlay.hovered || report.hovered || achievements.hovered)
			handler.getMouse().changeCursor("HAND");
		else if(!user.isSelected()) handler.getMouse().changeCursor("");
		
		if(!user.isSelected() && user.getContent() != Reporter.user) {
			Utils.fileWriter(user.getContent(), "C:\\Arvopia\\username.txt");
			Reporter.user = user.getContent();
			if(!LoaderException.readFile("C:\\Arvopia\\01.arv").contains("No Name"))
				Reporter.addUser();
			Achievement.award(Achievement.noname);
		}
		
		
		
	}

	@Override
	public void render(Graphics g, Graphics2D g2d) {
		
		g2d.setTransform(handler.getGame().getDefaultTransform());
		
		g.setColor(new Color(120, 225, 255));
		
		g.fillRect(0, 0, handler.getWidth(), handler.getHeight());
		
		g.drawImage(title, handler.getWidth()/2-title.getWidth()/2, 10, null);
		
		options.render(g);
		achievements.render(g);
		start.render(g);
		quit.render(g);
		changelog.render(g);
		howToPlay.render(g);
		report.render(g);
		user.render(g);
		playerAppearance.render(g);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString("Username", 5, 172);
	}

	@Override
	public void init() {

	}

}
