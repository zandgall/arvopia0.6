package com.zandgall.arvopia.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.zandgall.arvopia.ArvopiaLauncher;
import com.zandgall.arvopia.Game;

public class KeyManager implements KeyListener {
	
	public boolean[] keys;
	public boolean[] num = new boolean[11];
	public boolean up, down, left, right, esc, invtry, crft;
	public boolean b, prej;
	public boolean typed, preTyped;
	private Character typedKey;
	public boolean c;
	public boolean m;
	public boolean f1;
	
	public KeyManager() {
		keys = new boolean[524];
	}
	
	public Character getNameOfKey() {
		return typedKey;
	}
	
	public int keyCode() {
		return KeyEvent.getExtendedKeyCodeForChar(typedKey);
	}
	
	public void tick(){
		up = (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_SPACE]);
		down = keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
		invtry = (keys[KeyEvent.VK_Z] && typed);
		crft = (keys[KeyEvent.VK_X] && typed);
		b = keys[KeyEvent.VK_B] && typed;
		c = keys[KeyEvent.VK_C] && typed;
		m = keys[KeyEvent.VK_M];
		
		num[0] = keys[KeyEvent.VK_1];
		num[1] = keys[KeyEvent.VK_2];
		num[2] = keys[KeyEvent.VK_3];
		num[3] = keys[KeyEvent.VK_4];
		num[4] = keys[KeyEvent.VK_5];
		num[5] = keys[KeyEvent.VK_6];
		num[6] = keys[KeyEvent.VK_7];
		num[7] = keys[KeyEvent.VK_8];
		num[8] = keys[KeyEvent.VK_9];
		num[9] = keys[KeyEvent.VK_0];
		
		f1 = keys[KeyEvent.VK_F1];
		
		if(keys[KeyEvent.VK_K] && keys[KeyEvent.CTRL_DOWN_MASK] && ArvopiaLauncher.game.recorder.recording) {
			ArvopiaLauncher.game.recorder.record();
		} else if(keys[KeyEvent.VK_J] && keys[KeyEvent.CTRL_DOWN_MASK] && ArvopiaLauncher.game.recorder.recording){
			System.out.println("STOP");
			ArvopiaLauncher.game.recorder.stop();
			System.out.println("DONE"+System.lineSeparator()+System.lineSeparator()+System.lineSeparator());
		}
		
		prej = keys[KeyEvent.VK_J];
		
		
		esc = keys[KeyEvent.VK_ESCAPE] && typed;
		
		preTyped = typed;
		
		typed = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!keys[e.getKeyCode()]) { 
			Game.log.log("Key Code pressed: "+e.getKeyCode() + " Name: "+e.getKeyChar());
		}
		keys[e.getKeyCode()]=true;
		typed = false;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()]=false;
		typed = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keys[e.getKeyCode()]=true;
		typedKey = e.getKeyChar();
		typed = true;
	}

}
