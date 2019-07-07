package com.spyzviridian.markovbot.gui;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.learning.Learning;
import com.spyzviridian.markovbot.twitter.Tweeting;

public class GUIController {
	private static GUIController instance;
	
	private ConsoleFrame window;
	private Style style;
	private int maxLength;
	
	private GUIController(){
		
	}
	
	public void start(){
		// Iniciar interfaz gráfica
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
		}
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		
		// Crear y mostrar ventana
		window = new ConsoleFrame();
		window.setVisible(true);
		
		// Crear estilo HTML
		style = window.addStyle("infoStyle");
		StyleConstants.setForeground(style, new Color(Output.Type.INFO.getColor()));
		
		window.setBotName("@"+Config.getInstance().getProperty(Config.Property.BOT_NAME));
		maxLength = Integer.parseInt(Config.getInstance().getProperty(Config.Property.MAX_LENGTH));
		System.out.println("Max length: "+maxLength);
	}
	
	public synchronized void printLine(String str, Output.Type type){
		if(window != null){
			StyledDocument doc = window.getStyledDocument();
			StyleConstants.setForeground(style, new Color(type.getColor()));
			try {doc.insertString(doc.getLength(), (doc.getLength()>0?"\n":"")+str, style);} catch (BadLocationException e) {}
		}
	}
	
	public void blockUser(String str){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) return;
		Tweeting.getInstance().block(str);
	}
	
	public void unblockUser(String str){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) return;
		Tweeting.getInstance().unblock(str);
	}
	
	public void followUser(String str){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) return;
		Tweeting.getInstance().follow(str);
	}
	
	public void unfollowUser(String str){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) return;
		Tweeting.getInstance().unfollow(str);
	}
	
	public void tweet(String str){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) return;
		Tweeting.getInstance().tweet(str);
	}
	
	public void updateAvatar(){
		Twitter twitter = Control.getInstance().getTwitter();
		String username = Config.getInstance().getProperty(Config.Property.BOT_NAME);
		
		try {
			Output.getInstance().printToLog("Attempting to update picture...", Output.Type.INFO);
			User user = twitter.showUser(username);
			String strUrl = user.getOriginalProfileImageURL();
			Image image = null;
			URL url = new URL(strUrl);
			image = ImageIO.read(url);
			window.updateImage(image);
			Output.getInstance().printToLog("Picture updated.", Output.Type.INFO);
		} catch (TwitterException | IOException e) {
			Output.getInstance().printLine("Couldn't get avatar for user @"+username+": "+e.getMessage(), Output.Type.WARNING);
		}
	}
	
	public void testTweet(){
		String tweet = Learning.getInstance().generate(maxLength);
		Output.getInstance().printLine(tweet, Output.Type.TESTING);
	}
	
	public void updateInfo(int tweets, int following, int followers){
		window.updateInfo(tweets, following, followers);
	}
	
	public void updateStatus(String status){
		window.updateLabel(status);
	}
	
	public static GUIController getInstance(){
		if(instance == null){
			instance = new GUIController();
		}
		return instance;
	}
}
