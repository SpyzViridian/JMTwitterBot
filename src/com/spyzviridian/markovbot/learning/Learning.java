package com.spyzviridian.markovbot.learning;

import java.util.List;

import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.files.LearningGraph;

public class Learning {
	private static Learning instance;
	
	private LearningGraph learningGraph;
	//private FileSaver<LearningGraph> saver;
	//private String fileName;
	private int minWords;
	
	private Learning(){
		//fileName = Config.getInstance().getProperty(Config.Property.ORIGINAL_USER)+EXTENSION;
		//saver = new FileSaver<LearningGraph>(FOLDER, fileName, true);
		loadGraph();
		//initBannedWords();
		minWords = Integer.parseInt(Config.getInstance().getProperty(Config.Property.MIN_WORDS));
	}
	
	public synchronized boolean hasLearnedString(String str){
		return learningGraph.stringExists(str);
	}
	
	public synchronized String generate(int maxLength){
		String generated = null;
		do {
			generated = learningGraph.generate(maxLength);
		} while(wordCount(generated) < minWords);
		
		return generated;
	}
	
	public synchronized String generate(String withWord, int maxLength){
		String generated = null;
		do {
			generated = learningGraph.generate(withWord, maxLength);
		} while(wordCount(generated) < minWords);
		return generated;
	}
	
	// Se llama después de haber cogido los tweets, claro
	public synchronized void addTweets(List<String> tweets){
		for(String str : tweets){
			learningGraph.addString(str);
		}
	}
	
	public synchronized void addTweet(String tweet){
		learningGraph.addString(tweet);
	}
	
	public void printNodes(String str){
		learningGraph.printNexts(str);
	}
	

	
	public void loadGraph(){
		Output.getInstance().printLine("Generating learning graph...", Output.Type.INFO);
		/*
		try {
			learningGraph = saver.load(() -> new LearningGraph());
		} catch (NewFileException e) {
			// Hemos atrapado una excepción: como se ha creado un árbol, 
			// tenemos que añadirle todos los tuits de la lista de tweets
			learningGraph = (LearningGraph) e.getObject();
			Tweets.getInstance().rebuildGraph(learningGraph);
			saveGraph();
		}*/
		learningGraph = new LearningGraph();
		Tweets.getInstance().buildGraph(learningGraph);
	}
	
	public void saveGraph(){
		//Output.getInstance().printLine("Saving learning tree...", Output.Type.INFO);
		//saver.save(learningGraph);
	}
	
	private int wordCount(String str){
		if(str == null) return 0;
		return str.split("\\s+").length;
	}
	
	
	
	public static Learning getInstance(){
		if(instance == null) instance = new Learning();
		return instance;
	}
}
