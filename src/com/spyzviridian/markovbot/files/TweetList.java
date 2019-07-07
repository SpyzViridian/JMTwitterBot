package com.spyzviridian.markovbot.files;

import java.util.ArrayList;
import java.util.List;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.learning.IVersionable;

public class TweetList implements IVersionable {
	
	private static final long serialVersionUID = -6891678963105637649L;
	private int version;
	private long lastUserTweetID;
	private List<String> tweets;
	
	public TweetList() {
		version = Control.VERSION;
		lastUserTweetID = 1;
		tweets = new ArrayList<String>();
	}
	
	public void addTweet(String tweet){
		tweets.add(tweet);
	}
	
	public List<String> getTweets(){
		return tweets;
	}
	
	public long getLastUserTweetID(){
		return lastUserTweetID;
	}
	
	public void setLastUserTweetID(long value){
		lastUserTweetID = value;
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public boolean isUpToDate() {
		return version == Control.VERSION;
	}
}
