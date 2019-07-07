package com.spyzviridian.markovbot.threads;

import java.util.TimerTask;

import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.twitter.Tweeting;

public class TweetingThread extends TimerTask {

	@Override
	public void run() {
		// Tuitea
		float chance = Float.parseFloat(Config.getInstance().getProperty(Config.Property.TWEET_CHANCE));
		if(Math.random() <= chance){
			Tweeting.getInstance().tweet();
		}
	}

}
