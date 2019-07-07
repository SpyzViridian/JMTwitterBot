package com.spyzviridian.markovbot.twitter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.learning.Learning;
import com.spyzviridian.markovbot.learning.Tweets;

public class Tweeting {
	private static Tweeting instance;
	private static final String MEDIA_FOLDER = "media";
	
	private Learning learning;
	private Tweets tweets;
	private Twitter twitter;
	private Output output;
	private Config config;
	private String botName;
	private String originalUser;
	
	private int minDelay;
	private int maxDelay;
	private int maxLength;
	
	private File folder;
	private File[] media;
	
	private Tweeting(){
		learning = Learning.getInstance();
		tweets = Tweets.getInstance();
		twitter = Control.getInstance().getTwitter();
		output = Output.getInstance();
		config = Config.getInstance();
		botName = config.getProperty(Config.Property.BOT_NAME);
		originalUser = config.getProperty(Config.Property.ORIGINAL_USER);
		
		minDelay = Integer.parseInt(config.getProperty(Config.Property.MIN_ACTION_DELAY));
		maxDelay = Integer.parseInt(config.getProperty(Config.Property.MIN_ACTION_DELAY));
		maxLength = Integer.parseInt(config.getProperty(Config.Property.MAX_LENGTH));
		
		initMediaFolder();
	}
	
	// Aprende un tuit
	public synchronized void learn(Status tweet){
		if(Math.random() <= Float.parseFloat(config.getProperty(Config.Property.LEARN_FROM_OTHERS_CHANCE))){
			if(tweet.getText().length() > 1){
				tweets.addTweet(tweet.getText().trim());
				output.printToLog("Learning tweet: "+tweet.getText(), Output.Type.TWITTER);
			}
		}
	}
	
	// Publica algo deliberadamente
	public synchronized void tweet(String tweet){
		if(tweet.length() > maxLength){
			output.printLine("That tweet is too long. Max length is "+maxLength+".", Output.Type.ERROR);
		} else {
			StatusUpdate status = new StatusUpdate(tweet);
			try {
				send(status);
				output.printLine("Tweeting: \""+tweet+"\"", Output.Type.TWEETING);
			} catch (TwitterException e) {
				output.printLine("Couldn't send tweet \""+tweet+"\": "+e.getMessage(), Output.Type.ERROR);
			}
		}
	}
	
	// Publica algo aleatorio
	public synchronized void tweet(){
		float pictureChance = Float.parseFloat(config.getProperty(Config.Property.PICTURE_CHANCE));
		boolean withMedia = (Math.random() <= pictureChance) && (media.length > 0);
		boolean withText = true;
		
		int offset = 0;
		
		if(withMedia) {
			withText = Math.random() <= 0.5f;
			offset += 40;
		}
		
		StatusUpdate update = null;
		File randomMedia = getRandomMedia();
		// ¿Es con texto?
		if(withText){
			// ¿Tiene media?
			update = generateStatus(offset);
			if(withMedia) addMedia(update, randomMedia);
		} else {
			update = generateMedia(randomMedia);
		}
		// Tuitear
		try {
			send(update);
			if(withText){
				output.printLine("Tweeting: \""+update.getStatus()+"\"", Output.Type.TWEETING);
			} else {
				output.printLine("Tweeting media: \""+randomMedia.getName()+"\"", Output.Type.TWEETING);
			}
		} catch (TwitterException e) {
			if(withText){
				output.printLine("Couldn't send tweet \""+update.getStatus()+"\": "+e.getMessage(), Output.Type.ERROR);
			} else {
				output.printLine("Couldn't send media \""+randomMedia.getName()+"\": "+e.getMessage(), Output.Type.ERROR);
			}
			e.printStackTrace();
		}
	}
	
	// Responde a alguien desde el timeline
	public synchronized void randomTweet(Status tweet){
		if(canInteractWithUser(tweet.getUser())){
			float replyChance = Float.parseFloat(config.getProperty(Config.Property.REPLY_CHANCE));
			if(Math.random() <= replyChance){
				// Puede responder
				delayedReply(tweet);
			}
		}
	}
	
	// Responde un tweet (al tiempo)
	public synchronized void delayedReply(Status tweet){
		final Status finalStatus = tweet;
		int delay = getRandomDelay();
		output.printLine("Delaying next action for "+delay+" seconds.", Output.Type.INFO);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override 
			public void run() {
				reply(finalStatus);
			}
		}, delay * 1000);
	}
	
	// Responde un tweet (no necesita que le siga)
	public synchronized void reply(Status tweet){
		float pictureChance = Float.parseFloat(config.getProperty(Config.Property.PICTURE_CHANCE));
		boolean withMedia = (Math.random() <= pictureChance) && (media.length > 0);
		boolean withText = true;
		
		String mentionString = getMentionString(tweet);
		int offset = mentionString.length()+1;
		int mediaOffset = 0;
		
		if(withMedia) {
			withText = Math.random() <= 0.5f;
			offset += 40;
			mediaOffset += 40;
		}
		
		StatusUpdate update = null;
		File randomMedia = getRandomMedia();
		
		// ¿Es con texto?
		if(withText && ((maxLength-offset) > 100)){
			update = generateBasedStatus(tweet, mediaOffset);
			// ¿Tiene media?
			if(withMedia) addMedia(update, randomMedia);
		} else {
			withText = false;
			update = generateBasedMedia(randomMedia, tweet);
		}
		
		// Poner como respuesta
		addAsReply(update,tweet);
		
		// Tuitear
		try {
			send(update);
			if(withText){
				output.printLine("Replying to @"+tweet.getUser().getScreenName()+": \""+update.getStatus()+"\"", Output.Type.TWEETING);
			} else {
				output.printLine("Replying with media to @"+tweet.getUser().getScreenName()+": \""+randomMedia.getName()+"\"", Output.Type.TWEETING);
			}
		} catch (TwitterException e) {
			if(withText){
				output.printLine("(Replying to "+tweet.getUser().getScreenName()+") Couldn't send tweet \""+update.getStatus()+"\": "+e.getMessage(), Output.Type.ERROR);
			} else {
				output.printLine("(Replying to "+tweet.getUser().getScreenName()+") Couldn't send media \""+randomMedia.getName()+"\": "+e.getMessage(), Output.Type.ERROR);
			}
			e.printStackTrace();
		}

	}
	
	// Envía mensaje directo
	public synchronized void replyDirectMessage(DirectMessage dm){
		String reply = generateBasedString(dm.getText(), 280);
		try {
			twitter.sendDirectMessage(dm.getSenderScreenName(), reply);
			output.printLine("Sending direct message to @"+dm.getSenderScreenName()+": "+reply, Output.Type.TWEETING);
		} catch (TwitterException e) {
			output.printLine("Couldn't send direct message to @"+dm.getSenderScreenName()+": "+e.getMessage(), Output.Type.ERROR);
		}
	}
	
	// Envía mensaje directo (al tiempo)
	public synchronized void delayedDirectMessage(DirectMessage dm){
		final DirectMessage finalDM = dm;
		int delay = getRandomDelay();
		output.printLine("Delaying next action for "+delay+" seconds.", Output.Type.INFO);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override 
			public void run() {
				replyDirectMessage(finalDM);
			}
		}, delay * 1000);
	}
	
	// Marca un tweet como me gusta
	public synchronized void favorite(Status tweet){
		if(canInteractWithUser(tweet.getUser())){
			try {
				twitter.createFavorite(tweet.getId());
			} catch (TwitterException e) {
				output.printLine("Couldn't favorite tweet \""+tweet.getText()+"\": "+e.getMessage(), Output.Type.ERROR);
			}
		}
	}
	
	// Hace retweet a un tweet
	public synchronized void retweet(Status tweet){
		if(canInteractWithUser(tweet.getUser())){
			try {
				twitter.retweetStatus(tweet.getId());
			} catch (TwitterException e) {
				output.printLine("Couldn't retweet \""+tweet.getText()+"\": "+e.getMessage(), Output.Type.ERROR);
			}
		}
	}
	
	// Bloquea a un usuario
	public synchronized void block(String user){
		try {
			twitter.createBlock(user);
			output.printLine("Blocking user @"+user, Output.Type.TWITTER);
		} catch (TwitterException e) {
			output.printLine("Couldn't block user @"+user+": "+e.getMessage(), Output.Type.ERROR);
		}
	}
	
	// Desbloquea a un usuario
	public synchronized void unblock(String user){
		try {
			twitter.destroyBlock(user);
			output.printLine("Unblocking user @"+user, Output.Type.TWITTER);
		} catch (TwitterException e) {
			output.printLine("Couldn't block user @"+user+": "+e.getMessage(), Output.Type.ERROR);
		}
	}
	
	// Sigue al usuario
	public synchronized void follow(String userName){
		boolean canFollowback = config.getProperty(Config.Property.CAN_FOLLOWBACK).equalsIgnoreCase("true");
		// Sólo seguimos si se puede hacer follow
		if(canFollowback) {
			boolean canFollowAnyone = config.getProperty(Config.Property.CAN_FOLLOW_ANYONE).equalsIgnoreCase("true");
			try {
				boolean originalUserFollowsUser = twitter.showFriendship(originalUser, userName).isSourceFollowingTarget();
				// Lo seguirá si puede seguir a cualquiera o si al menos el usuario original sigue a este usuario
				if(canFollowAnyone || originalUserFollowsUser){
					twitter.createFriendship(userName);
					output.printLine("Following user @"+userName, Output.Type.TWITTER);
				}
			} catch (TwitterException e) {
				output.printLine("Couldn't follow user @"+userName+": "+e.getMessage(), Output.Type.ERROR);
				e.printStackTrace();
			}
		}
	}
	
	// Sigue al usuario
	public synchronized void follow(User user){
		follow(user.getScreenName());
	}
	
	// Deja de seguir al usuario
	public synchronized void unfollow(String userName){
		try {
			if(userName.equalsIgnoreCase(botName)) return;
			twitter.destroyFriendship(userName);
			output.printLine("Unfollowing user @"+userName, Output.Type.TWITTER);
		} catch (TwitterException e) {}
	}
	
	// Deja de seguir al usuario
	public synchronized void unfollow(User user){
		unfollow(user.getScreenName());
	}
	
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////
	
	private boolean canInteractWithUser(User user){
		return canInteractWithUser(user.getScreenName());
	}
	
	private boolean canInteractWithUser(String userName){
		if(userName.equalsIgnoreCase(botName)) return false;
		boolean can = false;
		try {
			can = twitter.showFriendship(userName, botName).isSourceFollowingTarget();
			if(!can){unfollow(userName);}
			return can;
		} catch (TwitterException e) {
			return false;
		}
	}
	
	private String generateBasedString(String text, int maxLength){
		List<String> candidates = new ArrayList<String>();
		String[] split = text.split("\\s+");
		for(String s : split){
			if(s.length() > 1){
				if(learning.hasLearnedString(s)){
					candidates.add(s);
				}
			}
		}
		if(candidates.size() > 0){
			return learning.generate(candidates.get(new Random().nextInt(candidates.size())), maxLength);
		};
		return learning.generate(maxLength);
	}
	
	private StatusUpdate generateBasedStatus(Status status, int lengthOffset){
		String mentionString = getMentionString(status);
		int mentionOffset = mentionString.length() + 1;
		String text = mentionString + " " + generateBasedString(removeMentions(status.getText()), maxLength-mentionOffset-lengthOffset);
		StatusUpdate update = new StatusUpdate(text);
		return update;
	}
	
	private StatusUpdate generateBasedMedia(File media, Status status){
		String mentionString = getMentionString(status);
		StatusUpdate update = new StatusUpdate(mentionString);
		addMedia(update, media);
		return update;
	}
	
	private StatusUpdate generateMedia(File media){
		StatusUpdate update = new StatusUpdate("");
		addMedia(update, media);
		return update;
	}
	
	private StatusUpdate generateStatus(int lengthOffset){
		return generateStatus(null, lengthOffset);
	}
	
	private StatusUpdate generateStatus(String withWord, int lengthOffset){
		String tweet = null;
		if(withWord != null){
			tweet = learning.generate(withWord, maxLength - lengthOffset);
		} else {
			tweet = learning.generate(maxLength - lengthOffset);
		}
		
		StatusUpdate update = new StatusUpdate(tweet);
		return update;
	}
	
	// Obtiene la cadena de replies
	private String getMentionString(Status status){
		StringBuffer buffer = new StringBuffer();
		
		// Poner todos menos el mío
		for(UserMentionEntity e : status.getUserMentionEntities()){
			if(!e.getScreenName().equalsIgnoreCase(botName)){
				buffer.append("@");
				buffer.append(e.getScreenName());
				buffer.append(" ");
			}
		}
		
		// Si no está, poner también a quien voy a responder
		String mentions = buffer.toString();
		if(!mentions.contains(status.getUser().getScreenName())){
			StringBuffer buffer2 = new StringBuffer();
			buffer2.append("@"+status.getUser().getScreenName());
			buffer2.append(" ");
			buffer2.append(mentions);
			mentions = buffer2.toString();
		}
		
		return mentions.trim();
	}
	
	private void addAsReply(StatusUpdate update, Status tweet){
		update.setInReplyToStatusId(tweet.getId());
	}
	
	private void addMedia(StatusUpdate update, File media){
		update.setMedia(media);
	}
	
	private File getRandomMedia(){
		if(media.length > 0){
			return media[new Random().nextInt(media.length)];
		}
		return null;
	}
	
	private void initMediaFolder(){
		folder = new File(MEDIA_FOLDER);
		if(!folder.exists()){
			folder.mkdir();
		}
		media = folder.listFiles();
		output.printLine("[MEDIA] Loaded "+media.length+" files.", Output.Type.INFO);
	}
	
	private int getRandomDelay(){
		return minDelay + new Random().nextInt(maxDelay-minDelay+1);
	}
	
	private void send(StatusUpdate update) throws TwitterException{
		twitter.updateStatus(update);
	}
	
	private String removeMentions(String str){
		String[] split = str.split("\\s+");
		StringBuffer buffer = new StringBuffer();
		for(String s : split){
			if(!s.contains("@") && !s.contains("#")){
				buffer.append(s+" ");
			}
		}
		return str.trim();
	}
	
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////
	
	public static Tweeting getInstance(){
		if(instance == null){
			instance = new Tweeting();
		}
		return instance;
	}
}
