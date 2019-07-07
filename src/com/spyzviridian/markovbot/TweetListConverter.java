package com.spyzviridian.markovbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.spyzviridian.markovbot.files.FileSaver;
import com.spyzviridian.markovbot.files.TweetList;

public class TweetListConverter {
	public static void main(String[] args) {
		String originalBank = args[0];
		String newBank = args[1];
		
		long lastID = 1;
		TweetList list = new TweetList();
		
		System.out.println("Updating model...");
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File("converted"+File.separator+originalBank)));
			String line = "";
			while((line = reader.readLine()) != null){
				if(line.length() > 0){
					list.addTweet(line);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		list.setLastUserTweetID(lastID);
		
		FileSaver<TweetList> saver = new FileSaver<TweetList>("converted",newBank, false);
		saver.save(list);
		System.out.println("Model saved. You can close now.");
		
	}
}
