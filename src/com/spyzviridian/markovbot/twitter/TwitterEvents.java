package com.spyzviridian.markovbot.twitter;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserMentionEntity;
//import twitter4j.TwitterStream;
//import twitter4j.TwitterStreamFactory;

public class TwitterEvents {
	private static TwitterEvents instance;
	
	private String botName;
	private Config config;
	
	private TwitterEvents(){
		
		// CAMBIADO HASTA QUE NO HAYA NUEVA VERSIÓN
		
		//TwitterStream stream = new TwitterStreamFactory(Control.getInstance().getConfiguration()).getInstance();
		//TwitterStreamingListener listener = new TwitterStreamingListener(this);
		//stream.addListener(listener);
		//stream.user(); // Todo lo que ocurra en nuestro timeline
		config = Config.getInstance();
		botName = config.getProperty(Config.Property.BOT_NAME);
	}
	
	private void likeOrRetweet(Status tweet){
		float likeChance = Float.parseFloat(config.getProperty(Config.Property.LIKE_CHANCE));
		float retweetChance = Float.parseFloat(config.getProperty(Config.Property.RETWEET_CHANCE));
		int minDelay = Integer.parseInt(config.getProperty(Config.Property.MIN_ACTION_DELAY));
		int maxDelay = Integer.parseInt(config.getProperty(Config.Property.MIN_ACTION_DELAY));
		int delay = minDelay + new Random().nextInt(maxDelay-minDelay+1);
		
		if(Math.random() <= likeChance){
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override 
				public void run() {
					Tweeting.getInstance().favorite(tweet);
				}
			}, delay * 1000);
		}
		
		if(Math.random() <= retweetChance){
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override 
				public void run() {
					Tweeting.getInstance().retweet(tweet);
				}
			}, delay * 1000);
		}
		
	}
	
	// Tweets normales, donde el bot NO ha sido mencionado
	protected void onTimeline(Status tweet){
		Tweeting.getInstance().randomTweet(tweet);
		Tweeting.getInstance().learn(tweet);
		likeOrRetweet(tweet);
	}
	
	// Tweets donde el bot SÍ ha sido mencionado
	protected void onMention(Status tweet){
		Output.getInstance().printLine("Mentioned by user @"+tweet.getUser().getScreenName()+": \""+tweet.getText()+"\"", Output.Type.TWITTER);
		Tweeting.getInstance().learn(tweet);
		Tweeting.getInstance().delayedReply(tweet);
	}
	
	// Tweets donde el bot ha sido citado
	protected void onQuote(Status tweet){
		Output.getInstance().printLine("Quoted by user @"+tweet.getUser().getScreenName()+": \""+tweet.getText()+"\"", Output.Type.TWITTER);
	}
	
	// El bot ha recibido un mensaje directo
	@SuppressWarnings("deprecation")
	protected void onDirectMessage(DirectMessage message){
		if(!message.getSender().getScreenName().equalsIgnoreCase(botName)){
			Output.getInstance().printLine("Direct message from @"+message.getSender().getScreenName()+": \""+message.getText()+"\"", Output.Type.TWITTER);
			Tweeting.getInstance().delayedDirectMessage(message);
		}
	}
	
	// El bot ha sido bloqueado por un usuario
	protected void onBlock(User source){
		Output.getInstance().printLine("Blocked by user @"+source.getScreenName(), Output.Type.TWITTER);
	}
	
	// El bot ha sido desbloqueado por un usuario
	protected void onUnblock(User source){
		Output.getInstance().printLine("Unblocked by user @"+source.getScreenName(), Output.Type.TWITTER);
	}
	
	// El bot ha sido seguido por un usuario
	protected void onFollow(User follower, User followed){
		Output.getInstance().printLine("Followed by user @"+follower.getScreenName(), Output.Type.TWITTER);
		Tweeting.getInstance().follow(follower);
	}
	
	// Uno de tus tweets ha recibido me gusta
	protected void onFavorite(User user, Status tweet){
		if(tweet.getMediaEntities().length > 0){
			if(tweet.getText().length() <= 0){
				Output.getInstance().printLine("@"+user.getScreenName()+" liked your photo: \""+tweet.getMediaEntities()[0].getDisplayURL()+"\"", Output.Type.TWITTER);
			}
		} else {
			Output.getInstance().printLine("@"+user.getScreenName()+" liked your tweet: \""+tweet.getText()+"\"", Output.Type.TWITTER);
		}
	}
	
	// Uno de tus tweets ha sido retwitteado
	protected void onRetweet(User user, Status tweet){
		if(tweet.getMediaEntities().length > 0){
			if(tweet.getText().length() <= 0){
				Output.getInstance().printLine("@"+user.getScreenName()+" retweeted your photo: \""+tweet.getMediaEntities()[0].getDisplayURL()+"\"", Output.Type.TWITTER);
			}
		} else {
			Output.getInstance().printLine("@"+user.getScreenName()+" retweeted your tweet: \""+tweet.getText()+"\"", Output.Type.TWITTER);
		}
	}
	
	// El bot ha dejado de ser seguido por un usuario
	protected void onUnfollow(User unfollower, User unfollowed){
		Output.getInstance().printLine("Unfollowed by user @"+unfollower.getScreenName(), Output.Type.TWITTER);
		Tweeting.getInstance().unfollow(unfollower);
	}
	
	// El bot se ha actualizado el perfil
	protected void onProfileUpdate(User user){
		Output.getInstance().printLine("Bot profile updated!", Output.Type.TWITTER);
	}
	
	protected final boolean isThisBot(User user){
		return user.getScreenName().equalsIgnoreCase(botName);
	}
	
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////
	
	public static TwitterEvents getInstance(){
		if(instance == null){
			instance = new TwitterEvents();
		}
		return instance;
	}
	
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////
	
	/*private static class TwitterStreamingListener implements UserStreamListener {
		
		private TwitterEvents events;
		private String botName;

		public TwitterStreamingListener(TwitterEvents events){
			this.events = events;
			botName = Config.getInstance().getProperty(Config.Property.BOT_NAME);
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {}

		@Override
		public void onScrubGeo(long arg0, long arg1) {}

		@Override
		public void onStallWarning(StallWarning arg0) {}

		@Override
		public void onStatus(Status status) {
			// Aparece un tweet en la timeline
			// Sólo pasamos los que no son RTs
			if(!status.isRetweet()){
				// ¿Ha mencionado al bot?
				for(UserMentionEntity mention : status.getUserMentionEntities()){
					if(mention.getScreenName().equalsIgnoreCase(botName)){
						events.onMention(status);
						return;
					}
				}
				// No ha sido mencionado
				events.onTimeline(status);
			} else {
				Status originalTweet = status.getRetweetedStatus();
				if(events.isThisBot(originalTweet.getUser())){
					// Me han hecho retweet
					events.onRetweet(status.getUser(), originalTweet);
				}
			}
		}

		@Override
		public void onTrackLimitationNotice(int arg0) {}

		@Override
		public void onException(Exception arg0) {}

		@Override
		public void onBlock(User source, User blockedUser) {
			if(events.isThisBot(blockedUser)){
				events.onBlock(source);
			}
		}

		@Override
		public void onDeletionNotice(long arg0, long arg1) {}

		@Override
		public void onDirectMessage(DirectMessage directMessage) {
			events.onDirectMessage(directMessage);
		}

		@Override
		public void onFavorite(User source, User favoritedUser, Status tweet) {
			if(events.isThisBot(favoritedUser)){
				events.onFavorite(source, tweet);
			}
		}

		@Override
		public void onFavoritedRetweet(User arg0, User arg1, Status arg2) {}

		@Override
		public void onFollow(User follower, User followed) {
			if(events.isThisBot(followed)){
				events.onFollow(follower, followed);
			}
		}

		@Override
		public void onFriendList(long[] arg0) {}

		@Override
		public void onQuotedTweet(User source, User quotedUser, Status tweet) {
			if(events.isThisBot(quotedUser)){
				events.onQuote(tweet);
			}
		}

		@Override
		public void onRetweetedRetweet(User arg0, User arg1, Status arg2) {}

		@Override
		public void onUnblock(User source, User unblockedUser) {
			if(events.isThisBot(unblockedUser)){
				events.onUnblock(source);
			}
		}

		@Override
		public void onUnfavorite(User arg0, User arg1, Status arg2) {}

		@Override
		public void onUnfollow(User unfollower, User unfollowed) {
			if(events.isThisBot(unfollower)){
				events.onUnfollow(unfollower, unfollowed);
			}
		}

		@Override
		public void onUserDeletion(long arg0) {}

		@Override
		public void onUserListCreation(User arg0, UserList arg1) {}

		@Override
		public void onUserListDeletion(User arg0, UserList arg1) {}

		@Override
		public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {}

		@Override
		public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {}

		@Override
		public void onUserListSubscription(User arg0, User arg1, UserList arg2) {}

		@Override
		public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {}
		@Override
		
		public void onUserListUpdate(User arg0, UserList arg1) {}
		
		@Override
		public void onUserProfileUpdate(User user) {
			if(events.isThisBot(user)){
				events.onProfileUpdate(user);
			}
		}

		@Override 
		public void onUserSuspension(long arg0) {}
		
	}*/
	
}
