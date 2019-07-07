package com.spyzviridian.markovbot.learning;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.exceptions.NewFileException;
import com.spyzviridian.markovbot.files.FileSaver;
import com.spyzviridian.markovbot.files.LearningGraph;
import com.spyzviridian.markovbot.files.TweetList;

public class Tweets {
	
	private static Tweets instance;
	private static final String FOLDER = "model";
	private static final String EXTENSION = ".tweets";
	private static final int STARTING_RETRY_TIME = 500;
	private static final int MAX_RETRY_TIME = 16000;
	
	private TweetList tweetList;
	private FileSaver<TweetList> saver;
	private String fileName;
	
	private Tweets(){
		fileName = Config.getInstance().getProperty(Config.Property.ORIGINAL_USER)+EXTENSION;
		saver = new FileSaver<TweetList>(FOLDER, fileName, false);
	}
	
	private ResponseList<Status> getStatuses(String user, int paging){
		ResponseList<Status> statuses = null;
		int retryMillis = STARTING_RETRY_TIME;
		try {statuses = Control.getInstance().getTwitter().timelines().getUserTimeline(user, new Paging(paging));} catch (TwitterException e) {}
		while(statuses == null){
			final int millis = retryMillis;
			Thread t = new Thread(){
				@Override
				public void run(){
					Output.getInstance().printLine("Couldn't connect to Twitter. Waiting for "+millis+" milliseconds.", Output.Type.ERROR);
					try{Thread.sleep(millis);}catch(InterruptedException e){};
				}
			};
			t.start();
			try {t.join();} catch (InterruptedException e1) {e1.printStackTrace();}
			retryMillis = Math.min(MAX_RETRY_TIME, retryMillis*2);
			try {statuses = Control.getInstance().getTwitter().timelines().getUserTimeline(user, new Paging(paging));} catch (TwitterException e) {}
			
		}
		return statuses;
	}
	
	public synchronized void searchAndAddLatestTweets(){
		// Si no existe la lista de tweets, crearla.
		if(tweetList == null) loadTweetList();
		// Buscar nuevos tweets del usuario
		ResponseList<Status> statuses = null;
		List<Status> tweets = new ArrayList<Status>();
		
		String user = Config.getInstance().getProperty(Config.Property.ORIGINAL_USER);
		int i = 1;
		int lastCount = 0;
		long lastID = 0;
		
		Output.getInstance().printLine("Currently "+tweetList.getTweets().size()+" tweets from @"+user+".", Output.Type.INFO);
		Output.getInstance().printLine("Searching new tweets from @"+user+"...", Output.Type.INFO);
		
		while(true){
			// Obtener tweets de la página i
			statuses = getStatuses(user, i);
			// Si no quedan tweets por recoger, salimos
			//System.out.println("Statuses: "+statuses.size()+", tweets = "+tweets.size()+", lastID = "+lastID+", lastUserTweetID = "+tweetList.getLastUserTweetID()+", paging = "+i);
			if(statuses.isEmpty() || (lastID == -1) || (lastID == tweetList.getLastUserTweetID())) break;
			for(Status status : statuses){
				// Por cada tweet
				// No contamos los retweets
				if(!status.isRetweet()){
					if(status.getId() > tweetList.getLastUserTweetID()){
						tweets.add(status);
						lastID = status.getId();
					} else {
						lastID = -1;
					}
				}
			}
			
			// Esto es puramente por razones estéticas
			if((tweets.size() - lastCount) >= 100){
				Output.getInstance().printLine("Found "+tweets.size()+" new tweets from @"+user+".", Output.Type.INFO);
				lastCount = tweets.size();
			}
			
			i++;
		}
		
		if(tweets.size() > 0){
			// Añadir tweets
			int added = 0;
			for(Status tweet : tweets){
				if(tweet.getId() > tweetList.getLastUserTweetID()){
					tweetList.getTweets().add(tweet.getText());
					Learning.getInstance().addTweet(tweet.getText());
					added++;
				}
			}
			if(added > 0){
				Output.getInstance().printLine("Added "+added+" new tweets from @"+user+".", Output.Type.INFO);
				tweetList.setLastUserTweetID(tweets.get(0).getId());
				// Los guardamos
				saveTweetList();
				Learning.getInstance().saveGraph();
			} else {
				Output.getInstance().printLine("No new tweets from @"+user+" were found.", Output.Type.INFO);
			}
		} else {
			Output.getInstance().printLine("No new tweets from @"+user+" were found.", Output.Type.INFO);
		}
		
	}
	
	public synchronized void addTweet(String str){
		tweetList.addTweet(str);
	}
	
	public synchronized void buildGraph(LearningGraph lg) {
		Output.getInstance().printToLog("Building graph...", Output.Type.WARNING);
		List<String> tweets = tweetList.getTweets();
		float percent = 0.0f;
		float lastPercent = 0.0f;
		int count = 0, length = tweets.size();
		for(String tweet : tweets) {
			lg.addString(tweet);
			count++;
			percent = ((float)count/(float)length)*100f;
			if(percent - lastPercent >= 10f) {
				Output.getInstance().printLine("Building learning graph ... "+percent+"%", Output.Type.INFO);
				lastPercent = percent;
			}
		}
		Output.getInstance().printLine("Building learning graph ... "+percent+"%", Output.Type.INFO);
		Output.getInstance().printLine("Learning graph built.", Output.Type.INFO);
	}
	
	public synchronized void loadTweetList(){
		Output.getInstance().printLine("Loading tweet list...", Output.Type.INFO);
		try {
			tweetList = saver.load(() -> new TweetList());
		} catch (NewFileException e) {
			tweetList = (TweetList) e.getObject();
		}
	}
	
	public synchronized void saveTweetList(){
		if(tweetList != null) {
			Output.getInstance().printLine("Saving tweet list...", Output.Type.INFO);
			saver.save(tweetList);
		}
	}
	
	public static Tweets getInstance(){
		if(instance == null) instance = new Tweets();
		return instance;
	}

}
