package com.spyzviridian.markovbot;

/*import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;*/
import java.util.Timer;

import com.spyzviridian.markovbot.config.Auth;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.gui.GUIController;
import com.spyzviridian.markovbot.learning.Learning;
import com.spyzviridian.markovbot.learning.Tweets;
import com.spyzviridian.markovbot.threads.*;
import com.spyzviridian.markovbot.twitter.TwitterEvents;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


// Singleton class
public class Control {
	private static Control instance;
	public static final int VERSION = 2;
	public static final int MAX_LENGTH = 280;
	public static final int LOOP_THRESHOLD = 10000;
	
	private Twitter twitter;
	private Configuration configuration;
	
	private Timer tweetSaverTimer, tweetingTimer, statusTimer, infoUpdaterTimer;
	private TweetSaverThread tweetSaverThread;
	private TweetingThread tweetingThread;
	private StatusThread statusThread;
	private InfoUpdaterThread infoUpdaterThread;
	
	private Control(){
		// Obtener las instancias de archivos de configuración
		Auth auth = Auth.getInstance();
		Config config = Config.getInstance();
		
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(auth.getProperty(Auth.Property.CONSUMER_KEY));
		builder.setOAuthConsumerSecret(auth.getProperty(Auth.Property.CONSUMER_SECRET));
		builder.setOAuthAccessToken(auth.getProperty(Auth.Property.ACCESS_TOKEN));
		builder.setOAuthAccessTokenSecret(auth.getProperty(Auth.Property.ACCESS_SECRET));
		builder.setUser(config.getProperty(Config.Property.BOT_NAME));
		
		configuration = builder.build();
		twitter = new TwitterFactory(configuration).getInstance();
	}
	
	public void init(){
		startBot();
		//
		
		//test_reader();
	}
	
	public void startBot(){
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			Output.getInstance().printLine("OFFLINE MODE - ALL ONLINE FEATURES ARE DISABLED.", Output.Type.WARNING);
			Tweets.getInstance().loadTweetList();
			Learning.getInstance();
			Output.getInstance().printLine("Remember - Offline mode is enabled.", Output.Type.WARNING);
		} else {
			initThreads();
			Output.getInstance().printLine("Starting bot...", Output.Type.INFO);
			GUIController.getInstance().updateAvatar();
			Tweets.getInstance().searchAndAddLatestTweets();
			startThreads();
			Learning.getInstance();
			TwitterEvents.getInstance();
			Output.getInstance().printLine("All threads started! :)", Output.Type.INFO_OK);
		}
		
	}
	
	public void stopBot(){
		stopThreads();
		Output.getInstance().printLine("Bot stopped.", Output.Type.INFO);
	}
	
	private void startThreads(){
		int tweetSaverPeriod = Integer.parseInt(Config.getInstance().getProperty(Config.Property.SAVE_RATE));
		int tweetRate = Integer.parseInt(Config.getInstance().getProperty(Config.Property.TWEET_RATE));
		
		// Recogida de tuits y guardado
		tweetSaverTimer = new Timer("TweetSaverThreadTimer", true);
		tweetSaverTimer.schedule(tweetSaverThread, tweetSaverPeriod * 1000, tweetSaverPeriod * 1000);
		
		// Timer para tuitear
		tweetingTimer = new Timer("TweetingThreadTimer", true);
		tweetingTimer.schedule(tweetingThread, tweetRate * 1000, tweetRate * 1000);
		
		// Este siempre estará activo
		statusTimer = new Timer("StatusThreadTimer", true);
		statusTimer.schedule(statusThread, 0, 1000); // Cada segundo
		
		// Timer para recoger la info de usuario
		infoUpdaterTimer = new Timer("InfoUpdaterTimer", true);
		infoUpdaterTimer.schedule(infoUpdaterThread, 0, 1000*60); //Cada minuto
	}
	
	private void stopThreads(){
		if(tweetSaverTimer != null){
			tweetSaverTimer.cancel();
		}
		if(tweetingTimer != null) {
			tweetingTimer.cancel();
		}
		if(tweetSaverThread != null){
			tweetSaverThread.cancel();
		}
		if(infoUpdaterTimer != null){
			infoUpdaterTimer.cancel();
		}
		tweetSaverThread = null;
		if(tweetingThread != null){
			tweetingThread.cancel();
		}
		tweetingThread = null;
		infoUpdaterThread = null;
	}
	
	private void initThreads(){
		tweetSaverThread = new TweetSaverThread();
		tweetingThread = new TweetingThread();
		statusThread = new StatusThread();
		infoUpdaterThread = new InfoUpdaterThread();
	}
	
	/*private void test_reader(){
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true){
			try {
				line = reader.readLine();
				System.out.println("--- GENERANDO CON PALABRA: "+line);
				System.out.println(Learning.getInstance().generate(line, 280));
				System.out.println("-------------------------------------");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	public Twitter getTwitter(){
		return twitter;
	}
	
	public Configuration getConfiguration(){
		return configuration;
	}
	
	public static Control getInstance(){
		if(instance == null) instance = new Control();
		return instance;
	}
	
	
}
