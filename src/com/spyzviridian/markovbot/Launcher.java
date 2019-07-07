package com.spyzviridian.markovbot;

import com.spyzviridian.markovbot.gui.GUIController;

public class Launcher {
	
	public static void main(String[] args) {
		GUIController.getInstance().start();
		
		Control.getInstance().init();
	}
}
