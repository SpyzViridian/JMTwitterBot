package com.spyzviridian.markovbot.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.spyzviridian.markovbot.Output;

public class Config extends PropertyFile {
	
	private static Config instance;
	private boolean checked;
	
	// File file;
	
	public Config(String subdirectory, String fileName){
		super(subdirectory, fileName);
		checked = false;
	}
	
	public static enum Property {
		BOT_NAME("botName", "The bot's Twitter account, without @"), 
		ORIGINAL_USER("originalUser", "The Twitter account you want to learn tweets from, without @"), 
		SAVE_RATE("saveRate", "How often the bot will save new learned tweets, in seconds"), 
		TWEET_RATE("tweetRate", "How often the bot will attempt to tweet something, in seconds."), 
		TWEET_CHANCE("tweetChance", "Chance of tweeting something. Leave this to 1 if you want a fixed tweet rate. [0.0, ..., 1.0]"), 
		LIKE_CHANCE("likeChance", "Chance of faving a tweet in the bot's timeline."), 
		RETWEET_CHANCE("retweetChance", "Chance of retweeting a tweet in the bot's timeline."), 
		REPLY_CHANCE("replyChance", "Chance of replying a tweet on its TIMELINE. [0.0, ..., 1.0]"), 
		LEARN_FROM_OTHERS_CHANCE("learnFromOthersChance", "Chance of learning each tweet on its TIMELINE. Leave this to 0 if you want to learn from only one user. [0.0, ..., 1.0]"),
		PICTURE_CHANCE("pictureChance", "Chance of tweeting media instead of text. Media (JPG, PNG, GIF, ...) can be stored at folder 'media'. [0.0, ..., 1.0]"), 
		MIN_ACTION_DELAY("minActionDelay", "Min delay between like, retweet and reply, in seconds."), 
		MAX_ACTION_DELAY("maxActionDelay", "Max delay between like, retweet and reply, in seconds."), 
		CAN_FOLLOWBACK("canFollowback", "Whether the bot can followback a user or not. [true/false]"), 
		CAN_FOLLOW_ANYONE("canFollowAnyone", "If this is false, the bot can only follow users that the original user follows. [true/false]"), 
		CAN_SEND_DIRECT_MESSAGES("canSendDirectMessages", "If this is true, the bot can reply a direct message. [true/false]"),
		COHERENCE("coherence", "Bot coherence. 0 means the bot will be more random, 1 means total coherence and the bot will moderate. [0.0, ..., 1.0]"), 
		MIN_WORDS("minWords", "Minimum number of words required when generating tweets. [1, 2, ...]"), 
		MAX_LENGTH("maxLength", "Maximum text length allowed when generating tweets. Low numbers may crash the bot. A higher number than 280 may cause the bot to not tweet anything. [100, 200, ...]"),
		OFFLINE_MODE("offlineMode", "If this is true, the bot won't try to connect to Internet.")
		;
		
		private String name;
		private String description;
		
		private Property(String name, String description){
			this.name = name;
			this.description = description;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getConfigDescription() {
			return "# " + name + ": " + description; 
		}
		
		@Override
		public String toString(){
			return getName();
		}
	}
	
	@Override
	protected void createFile() {
		PrintWriter logWriter = null;
		try {
			@SuppressWarnings("resource")
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			logWriter = new PrintWriter(bw);
		} catch (IOException e) {
			// Error fatal
			Output.getInstance().printLine("Couldn't create file '"+file.getName()+"'.", Output.Type.ERROR);
			System.exit(-1);
		}
		logWriter.println("# =================================================================================");
		logWriter.println("# JMTwitterBot Configuration File. Deleting this file will generate a new one.");
		logWriter.println("# =================================================================================");
		logWriter.println();
		logWriter.println("# [USER CONFIGURATION]");
		printDefaultProperty(logWriter, Config.Property.BOT_NAME);
		printDefaultProperty(logWriter, Config.Property.ORIGINAL_USER);
		logWriter.println("# =================================================================================");
		logWriter.println("# [BOT CONFIGURATION]");
		printDefaultProperty(logWriter, Config.Property.SAVE_RATE);
		printDefaultProperty(logWriter, Config.Property.TWEET_RATE);
		printDefaultProperty(logWriter, Config.Property.TWEET_CHANCE);
		printDefaultProperty(logWriter, Config.Property.LIKE_CHANCE);
		printDefaultProperty(logWriter, Config.Property.RETWEET_CHANCE);
		printDefaultProperty(logWriter, Config.Property.REPLY_CHANCE);
		printDefaultProperty(logWriter, Config.Property.LEARN_FROM_OTHERS_CHANCE);
		printDefaultProperty(logWriter, Config.Property.PICTURE_CHANCE);
		printDefaultProperty(logWriter, Config.Property.MIN_ACTION_DELAY);
		printDefaultProperty(logWriter, Config.Property.MAX_ACTION_DELAY);
		printDefaultProperty(logWriter, Config.Property.CAN_FOLLOWBACK);
		printDefaultProperty(logWriter, Config.Property.CAN_FOLLOW_ANYONE);
		printDefaultProperty(logWriter, Config.Property.CAN_SEND_DIRECT_MESSAGES);
		printDefaultProperty(logWriter, Config.Property.COHERENCE);
		printDefaultProperty(logWriter, Config.Property.MIN_WORDS);
		printDefaultProperty(logWriter, Config.Property.MAX_LENGTH);
		printDefaultProperty(logWriter, Config.Property.OFFLINE_MODE);
		logWriter.flush();
		logWriter.close();
	}

	@Override
	protected String getDefaultProperty(String property) {
		switch(property){
		case "botName": return "...";
		case "originalUser": return "...";
		case "tweetRate": return "5";
		case "tweetChance": return "0.015";
		case "canFollowback": return "true";
		case "canFollowAnyone": return "false";
		case "canSendDirectMessages": return "true";
		case "learnFromOthersChance": return "0";
		case "saveRate": return "300";
		case "likeChance": return "0.005";
		case "retweetChance": return "0.005";
		case "replyChance": return "0.005";
		case "minActionDelay": return "5";
		case "maxActionDelay": return "10";
		case "pictureChance": return "0.05";
		case "coherence": return "0.75";
		case "minWords": return "2";
		case "maxLength": return "280";
		case "offlineMode": return "false";
		}
		return null;
	}
	
	protected void addPropertyToFile(Config.Property property) {
		PrintWriter logWriter = null;
		try {
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			logWriter = new PrintWriter(bw);
		} catch (IOException e) {
			// Error fatal
			Output.getInstance().printLine("Couldn't edit file '"+file.getName()+"'.", Output.Type.ERROR);
			System.exit(-1);
		}
		
		printDefaultProperty(logWriter, property);
		configFile.put(property.getName(), getDefaultProperty(property.getName()));
		
		logWriter.flush();
		logWriter.close();
		
		Output.getInstance().printLine("Added new option ("+property.getName()+") to '"+file.getName()+"'.", Output.Type.INFO);
	}
	
	public String getProperty(Config.Property property){
		// Si no existe, se crea
		if(!configFile.containsKey(property.getName())) {
			addPropertyToFile(property);
		}
		return this.getProperty(property.getName());
	}
	
	public void setProperty(Config.Property property, String value){
		this.setProperty(property.getName(), value);
	}
	
	protected String getDefaultPair(Config.Property property) {
		return property.getName() + "=" + getDefaultProperty(property.getName());
	}
	
	private void printDefaultProperty(PrintWriter logWriter, Config.Property property) {
		if(!checked) {
			logWriter.println();
			checked = true;
		}
		logWriter.println(property.getConfigDescription());
		logWriter.println(getDefaultPair(property));
	}
	
	public static Config getInstance(){
		if(instance == null){
			instance = new Config("config", "bot.cfg");
		}
		return instance;
	}

	@Override
	// Comprueba que existen todos en el fichero de configuración
	protected void check() {
		for(Config.Property prop : Config.Property.values()) {
			getProperty(prop);
		}
	}
	
}
