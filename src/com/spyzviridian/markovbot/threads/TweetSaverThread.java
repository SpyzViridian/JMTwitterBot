package com.spyzviridian.markovbot.threads;

import java.util.TimerTask;

import com.spyzviridian.markovbot.learning.Tweets;

public class TweetSaverThread extends TimerTask {
	
	@Override
	public void run() {
		// Esta función ya hace todo lo necesario para guardar los tuits
		Tweets.getInstance().searchAndAddLatestTweets();
	}

}
