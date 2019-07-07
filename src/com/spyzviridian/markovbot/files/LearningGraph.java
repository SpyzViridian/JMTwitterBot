package com.spyzviridian.markovbot.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spyzviridian.markovbot.Control;
import com.spyzviridian.markovbot.Output;
import com.spyzviridian.markovbot.config.Config;
import com.spyzviridian.markovbot.learning.IVersionable;
import com.spyzviridian.markovbot.strings.SpecialToken;
import com.spyzviridian.markovbot.strings.StringTokenizer;
import com.spyzviridian.markovbot.strings.Token;

public class LearningGraph implements IVersionable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5846044330722946017L;
	
	private int version;
	
	private Map<String, Node> nodeMap;
	private Node root;
	
	private static final String FOLDER = "config";
	private static final String FILENAME = "banned_words.txt";
	String[] bannedWords;
	
	public LearningGraph(){
		version = Control.VERSION;
		// No sensible a mayúsculas
		nodeMap = new TreeMap<String, Node>(String.CASE_INSENSITIVE_ORDER);
		root = createFirstNode();
		initBannedWords();
	}
	
	private Node createFirstNode(){
		// Por algún sitio se tiene que empezar.
		// Como el punto (.) es el que marca el inicio de la frase, podemos tomarlo como
		// raíz. Al meter varias frases, la estructura es un GRAFO.
		Node root = new Node(this, Token.DOT.getToken());
		nodeMap.put(Token.DOT.getToken(), root);
		return root;
	}
	
	
	// Genera una cadena aleatoria con todo lo aprendido
	public String generate(int maxLength){
		return tokenListToString(generateFromNode(root, maxLength));
	}
	
	public String generate(String withWord, int maxLength){
		// Buscamos si está la palabra por ahí
		Node n = nodeWith(withWord);
		if(n == null) return generate(maxLength); // No está, generamos normal
		// A partir de aquí sí está, generamos:
		List<String> firstPart, secondPart, result = new ArrayList<String>();
		int resultLen = 0;
		int loops = 0;
		do {
			firstPart = generateFromNodeToRoot(n, maxLength);
			secondPart = new ArrayList<String>();
			if(firstPart.size() > 0){
				n = nodeMap.get(firstPart.get(firstPart.size()-1));
				if(n.nextNodes.size() > 1){
					secondPart = generateFromNode(n, maxLength);
				}
			}
			result = new ArrayList<String>();
			result.addAll(firstPart);
			result.addAll(secondPart);
			resultLen = tokenListToString(result).length();
			loops++;
		} while(((resultLen < 2) || (resultLen > maxLength)) && (loops < Control.LOOP_THRESHOLD));
		if(loops >= Control.LOOP_THRESHOLD) return null;
		return tokenListToString(result).replace(SpecialToken.END_TOKEN.getToken(), "");
	}
	
	// Genera una cadena aleatoria con todo lo aprendido, a partir del nodo especificado (sin contarlo)
	private List<String> generateFromNode(Node startingNode, int maxLength){
		float coherence = Float.parseFloat(Config.getInstance().getProperty(Config.Property.COHERENCE));
		Node currentNode = startingNode;
		List<String> words = new ArrayList<String>();
		int length = 0;
		StringTokenizer tokenizer = StringTokenizer.getInstance();
		int loops = 0;
		while(((words.size() < 1) || (length > maxLength)) && (loops < Control.LOOP_THRESHOLD)){
			words.clear();
			currentNode = startingNode;
			length = 0;
			
			while(!currentNode.token.equalsIgnoreCase(SpecialToken.END_TOKEN.getToken()) && (loops < Control.LOOP_THRESHOLD)){
				currentNode = currentNode.getRandomNextNode();
				if(!currentNode.token.equalsIgnoreCase(SpecialToken.END_TOKEN.getToken())){
					words.add(currentNode.token);
					length = tokenListToString(words).length();
					// COHERENCIA
					if((coherence > 0f) && (Math.random() <= (1-coherence))){
						List<String> tokens = tokenizer.tokenizeNoEndToken(currentNode.token);
						if(tokens.size() > 0){
							String last = tokens.get(tokens.size()-1);
							Node n = nodeStartsWith(last);
							if(n != null){
								currentNode = n;
								tokens = tokenizer.tokenizeNoEndToken(n.token);
								if(tokens.size() == 2){
									last = tokens.get(tokens.size()-1);
									words.add(last);
									length = tokenListToString(words).length();
								}
							}
						}
					}
				}
				loops++;
			}
			loops++;
		}
		if(loops >= Control.LOOP_THRESHOLD) return null;
		return words;
	}
	
	// Determina si la cadena está baneada
	public boolean isBanned(String str){
		/*for(String s : str.split("\\s+")){
			for(String bannedWord : bannedWords){
				if(s.equalsIgnoreCase(bannedWord)){
					return true;
				}
			}
		}*/
		// 12/06-19 CAMBIOS
		for(String bannedWord : bannedWords) {
			if(str.toLowerCase().contains(bannedWord.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isValid(String str){
		if(isBanned(str)) return false;
		for(String s : str.split("\\s+")){
			if(s.contains("@") || s.contains("http") || s.contains("#"))
				return false;
		}
		return true;
	}
	
	// Genera una cadena aleatoria con todo lo aprendido, a partir del nodo especificado (contándolo)
	private List<String> generateFromNodeToRoot(Node startingNode, int maxLength){
		float coherence = Float.parseFloat(Config.getInstance().getProperty(Config.Property.COHERENCE));
		Node currentNode = startingNode;
		List<String> words = new ArrayList<String>();
		words.add(startingNode.token);
		int length = 0;
		StringTokenizer tokenizer = StringTokenizer.getInstance();
		int loops = 0;
		while(((words.size() <= 1) || (length > maxLength)) && (loops < Control.LOOP_THRESHOLD)){
			words.clear();
			words.add(startingNode.token);
			currentNode = startingNode;
			length = 0;
			
			while(!currentNode.token.equalsIgnoreCase(Token.DOT.getToken()) && (loops < Control.LOOP_THRESHOLD)){
				currentNode = currentNode.getRandomPreviousNode();
				if(!currentNode.token.equalsIgnoreCase(Token.DOT.getToken())){
					words.add(0, currentNode.token);
					length = tokenListToString(words).length();
					// COHERENCIA
					if((coherence > 0f) && (Math.random() <= (1-coherence))){
						List<String> tokens = tokenizer.tokenizeNoEndToken(currentNode.token);
						if(tokens.size() > 0){
							String start = tokens.get(0);
							Node n = nodeEndsWith(start);
							if(n != null){
								currentNode = n;
								tokens = tokenizer.tokenizeNoEndToken(n.token);
								if(tokens.size() == 2){
									start = tokens.get(0);
									words.add(0, start);
									length = tokenListToString(words).length();
								}
							}
						}
					}
				}
				loops++;
			}
			loops++;
		}
		if(loops >= Control.LOOP_THRESHOLD) return null;
		return words;
	}
	
	public boolean stringExists(String str){
		return (nodeWith(str) != null);
	}
		
	private Node nodeWith(String str){
		List<Node> candidates = new ArrayList<Node>();
		for(String key : nodeMap.keySet()){
			List<String> tokens = StringTokenizer.getInstance().tokenizeNoEndToken(key);
			if(tokens.size() > 0){
				for(String t : tokens){
					if(t.equalsIgnoreCase(str)){
						candidates.add(nodeMap.get(key));
						break;
					}
				}
				
			}
		}
		if(candidates.size() > 0) return candidates.get(new Random().nextInt(candidates.size()));
		return null;
	}	
		
	private Node nodeEndsWith(String str){
		List<Node> candidates = new ArrayList<Node>();
		for(String key : nodeMap.keySet()){
			List<String> tokens = StringTokenizer.getInstance().tokenizeNoEndToken(key);
			if(tokens.size() > 0){
				String last = tokens.get(tokens.size()-1);
				if(last.equalsIgnoreCase(str)){
					candidates.add(nodeMap.get(key));
				}
			}
		}
		if(candidates.size() > 0) return candidates.get(new Random().nextInt(candidates.size()));
		return null;
	}	
	
	private Node nodeStartsWith(String str){
		List<Node> candidates = new ArrayList<Node>();
		for(String key : nodeMap.keySet()){
			List<String> tokens = StringTokenizer.getInstance().tokenizeNoEndToken(key);
			if(tokens.size() > 0){
				String first = tokens.get(0);
				if(first.equalsIgnoreCase(str)){
					candidates.add(nodeMap.get(key));
				}
			}
		}
		if(candidates.size() > 0) return candidates.get(new Random().nextInt(candidates.size()));
		return null;
	}
	
	private String tokenListToString(List<String> tokens){
		if(tokens == null) return null;
		StringBuffer buffer = new StringBuffer();
		if(tokens.size() > 0){
			String current = tokens.get(0);
			buffer.append(current);
			String previous = null;
			for(int i = 1; i < tokens.size(); i++){
				previous = current;
				current = tokens.get(i);
				if(!(!StringTokenizer.getInstance().isToken(previous) && StringTokenizer.getInstance().isToken(current))){
					if(!previous.equalsIgnoreCase(SpecialToken.NEW_LINE.getToken())) buffer.append(" ");
				}
				if(current.equalsIgnoreCase(SpecialToken.NEW_LINE.getToken())){
					buffer.append("\n");
				} else if(!current.equalsIgnoreCase(SpecialToken.END_TOKEN.getToken())){
					buffer.append(current);
				}
			}
		}
		
		return buffer.toString().trim().replace(SpecialToken.NEW_LINE.getToken(), "");
	}
	
	// Añade una cadena al árbol, que será diferente si hay coherencia o no
	public void addString(String str){
		
		// 12/06/19 TRATAMIENTO DE LA CADENA: CADA SALTO DE LINEA ES UN "TWEET"
		
		Pattern pat = Pattern.compile("(\\r?\\n)+");
		Matcher matcher = pat.matcher(str);
		
		str = matcher.replaceAll("\n");
		
		// 12/06/19 TRATAMIENTO DE LA CADENA FIN -------------------------------
		
		float coherence = Float.parseFloat(Config.getInstance().getProperty(Config.Property.COHERENCE));
		
		String[] lineSplit = str.split("\\r?\\n");
		
		for(String line : lineSplit) {
			if(coherence <= 0f){
				addStringNoCoherence(line);
			} else {
				addStringCoherence(line);
			}
		}
	}
	
	private void addStringCoherence(String str){
		// ¿Es aceptable? (No tiene menciones o hashtags)
		if(isValid(str)){
			// La cadena debería tokenizarse y prepararse
			List<String> tokens = StringTokenizer.getInstance().tokenize(str);
			if(tokens.size() > 0){
				List<String> pair = new ArrayList<String>(2);
				Node currentNode = root;
				// Iteramos por los tokens cogiendo pares, con un offset (para coger dos pares diferentes)
				for(int offset = -1; offset < 1; offset++){
					for(int iPar = offset; iPar < tokens.size(); iPar += 2){
						pair.clear(); // Borrar el par actual
						if(iPar >= 0) pair.add(tokens.get(iPar));
						if((iPar+1) < tokens.size()) pair.add(tokens.get(iPar+1));
						// ¿Tenemos un par?
						if(pair.size() == 2) {
							// ¿Los dos token son palabras?
							StringTokenizer tokenizer = StringTokenizer.getInstance();
							if(!tokenizer.isToken(pair.get(0)) && !tokenizer.isToken(pair.get(1))){
								String t = mergeWordPair(pair);
								currentNode = currentNode.addToken(t);
							} else {
								// Se tendrán que meter por separado
								currentNode = currentNode.addToken(pair.get(0));
								currentNode = currentNode.addToken(pair.get(1));
							}
						} else if(pair.size() == 1) {
							// Sólo ha quedado algo de por medio, que lo añadimos y ya está
							currentNode = currentNode.addToken(pair.get(0));
						}
					}
				}
 			}
		}
	}
	
	private String mergeWordPair(List<String> pair){
		return pair.get(0) + " " + pair.get(1);
	}
	
	private void addStringNoCoherence(String str){
		// ¿Es aceptable? (No tiene menciones o hashtags)
		if(isValid(str)){
			// La cadena debería tokenizarse y prepararse
			List<String> tokens = StringTokenizer.getInstance().tokenize(str);
			// Por cada token...
			if(tokens.size() > 0){
				Node next = root.addToken(tokens.get(0));
				for(int i = 1; i < tokens.size(); i++){
					next = next.addToken(tokens.get(i));
				}
			}
		}
	}
	
	public void printNexts(String str){
		Node node = nodeMap.get(str);
		if(node != null){
			System.out.println("NODES FOR "+str);
			for(int i = 0; i < node.nextNodes.size(); i++){
				System.out.println("\t-> "+node.nextNodes.get(i).token + " ("+node.nextWeights.get(i)+")");
			}
		}
	}
	
	private void initBannedWords(){
		bannedWords = new String[0];
		File folder = new File(FOLDER);
		if(!folder.exists()) folder.mkdir();
		File file = new File(FOLDER + File.separator + FILENAME);
		if(!file.exists()){
			try {
				file.createNewFile();
				Output.getInstance().printLine("Couldn't find "+FILENAME+". A new one has been created.", Output.Type.WARNING);
			} catch (IOException e) {
				Output.getInstance().printLine("Couldn't create "+FILENAME+": "+e.getMessage(), Output.Type.ERROR);
			}
		} else {
			// leer
			try {
				Scanner scanner = new Scanner(file);
				String content = null;
				try {
				content = scanner.useDelimiter("\\Z").next();
				bannedWords = content.split("\\s+");
				scanner.close();
				Output.getInstance().printLine("Loaded "+bannedWords.length+" banned words.", Output.Type.INFO);
				} catch (NoSuchElementException ex){
					Output.getInstance().printLine("File "+FILENAME+" is empty or contains invalid data.", Output.Type.WARNING);
				}
			} catch (FileNotFoundException e) {
				// No se ha encontrado
			}
		}
	}
	
	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public boolean isUpToDate() {
		return version == Control.VERSION;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	private static class Node implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 6371085728920149304L;
		private LearningGraph graph;
		private String token;
		private List<Node> nextNodes;
		private List<Node> previousNodes;
		private List<Integer> nextWeights;
		private List<Integer> previousWeights;
		private int nextWeightSum, previousWeightSum;
		private Random random;
		
		public Node(LearningGraph graph, String token){
			this.graph = graph;
			this.token = token;
			nextNodes = new ArrayList<Node>();
			previousNodes = new ArrayList<Node>();
			nextWeights = new ArrayList<Integer>();
			previousWeights = new ArrayList<Integer>();
			nextWeightSum = 0;
			previousWeightSum = 0;
			random = new Random();
		}
		
		// Búsqueda del índice de un token en una de las listas (siguiente o anterior)
		private int searchInList(List<Node> list, String word) {
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).token.equalsIgnoreCase(word))
					return i;
			}
			return -1;
		}
		
		// Búsqueda en la lista de siguiente
		private int searchNext(String word) {
			return searchInList(nextNodes, word);
		}
		
		// Búsqueda en la lista de anterior
		private int searchPrevious(String word) {
			return searchInList(previousNodes, word);
		}
		
		// Añadir un nodo siguiente
		// Si siempre incrementamos las dos listas a la vez
		// la relación de nodos con pesos se mantiene
		private void addNode(Node nextNode) {
			this.nextNodes.add(nextNode);
			this.nextWeights.add(1);
			this.nextWeightSum++;
			// Bidireccional
			nextNode.previousNodes.add(this);
			nextNode.previousWeights.add(1);
			nextNode.previousWeightSum++;
		}
		
		// Obtener un nodo siguiente aleatorio, según los pesos
		private Node getRandomNextNode(){
			int cumsum = this.nextWeightSum, index = random.nextInt(cumsum), sum = 0, i = 0;
			while( sum < index ) sum = sum + this.nextWeights.get(i++);
			return this.nextNodes.get(Math.max(0, i-1));
		}
		
		// Obtener un nodo anterior aleatorio, según los pesos
		private Node getRandomPreviousNode(){
			int cumsum = this.previousWeightSum, index = random.nextInt(cumsum), sum = 0, i = 0;
			while( sum < index ) sum = sum + this.previousWeights.get(i++);
			return this.previousNodes.get(Math.max(0, i-1));
		}
		
		
		// Añade un token (o lo incrementa) y devuelve el siguiente nodo
		public Node addToken(String word) {
			// Buscamos primero el token o palabra
			int index = searchNext(word);
			// Si existe incrementamos el peso
			if (index >= 0){
				// A VER QUE NO NOS LIEMOS. Buscar en la lista de anteriores del siguiente nodo
				// el que le corresponde a éste (porque es bidireccional)
				Node next = nextNodes.get(index);
				int nextIndex = next.searchPrevious(this.token);
				// Incrementamos el peso hacia el siguiente nodo
				nextWeights.set(index, nextWeights.get(index)+1);
				nextWeightSum++;
				// Incrementamos el peso desde el siguiente nodo hacia el actual
				next.previousWeights.set(nextIndex, next.previousWeights.get(nextIndex)+1);
				next.previousWeightSum++;
				return next;
			} else {
				// No existe
				// ¿Pero podría existir ya ese token en la lista global?
				Node globalNode = graph.nodeMap.get(word);
				if(globalNode == null) {
					// No existe, así que vamos a crear un nuevo nodo
					globalNode = new Node(graph, word);
					// Se añade a la lista global de nodos
					graph.nodeMap.put(word, globalNode);
				}
				// Una vez que tenemos el nodo (creado o cogido de la lista global), se añade
				addNode(globalNode);
				return globalNode;
			}
		}
	}

}
