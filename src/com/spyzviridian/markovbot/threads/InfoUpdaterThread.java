package com.spyzviridian.markovbot.threads;

import java.util.TimerTask;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.gui.GUIController;

public class InfoUpdaterThread extends TimerTask {
	
	@Override
	public void run() {
		// Obtener información del usuario de Twitter
		Twitter twitter = Control.getInstance().getTwitter();
		String username = Config.getInstance().getProperty(Config.Property.BOT_NAME);
		try {
			User bot = twitter.showUser(username);
			GUIController.getInstance().updateInfo(bot.getStatusesCount(), bot.getFriendsCount(), bot.getFollowersCount());
		} catch (TwitterException e) {
			Output.getInstance().printLine("Couldn't get info for user @"+username+": "+e.getMessage(), Output.Type.ERROR);
		}
	}

}
