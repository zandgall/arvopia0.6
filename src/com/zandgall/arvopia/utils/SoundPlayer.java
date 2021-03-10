package com.zandgall.arvopia.utils;

public class SoundPlayer{
	
	Sound sound;
	private double timePerTick = 1000000000 /60;
	
	boolean fadeOut, running = false;
	
	public SoundPlayer(Sound sound) {
		this.sound = sound;
	}
	
	public void setSong(Sound sound) {
		if(this.sound!=null)
			this.sound.Stop(false);
		this.sound = sound;
		this.sound.Start(0, true);
	}
	
	public Sound getSong() { 
		return sound;
	}
	
	public void play() {
		if(!running) {
			sound.Start(0, false);
			sound.fade=false;
			running = true;
		}
	}
	
	public boolean done() {
		if(sound!=null) {
			return sound.hasEnded();
		}
		return true;
	}
	
	public void fadeOut() {
		if(sound!=null)
			sound.fade();
	}
	
	public void volume(double volume) {
		sound.setVolume((int) volume, true);
	}
	
	public void stop(boolean remove) {
		sound.Stop(true);
		if(remove)
		sound = null;
		running = false;
	}
	
	public void tick() {
		if(sound!=null) {
			sound.tick(false);
		}
	}
	
}
