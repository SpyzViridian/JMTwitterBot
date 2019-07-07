package com.spyzviridian.markovbot.strings;

import java.util.ArrayList;
import java.util.List;

public class StringTokenizer {
	private static StringTokenizer instance;
	
	private StringTokenizer(){
		
	}
	
	public List<String> tokenize(String str){
		// La lista donde pondremos al final todo
		List<String> tokenList = new ArrayList<String>();
		// Queremos conservar los saltos de línea, si se puede
		String[] lineSplit = str.split("\\r?\\n");
		for(int i = 0; i < lineSplit.length; i++) {
			// Verificamos si cada "palabra" termina en un
			// token de signo de puntuación, para que lo
			// podamos separar.
			String[] tokens = lineSplit[i].split(" ");
			List<String> split = null;
			for(String t : tokens){
				// Quitamos todas las URL
				split = splitToken(t);
				tokenList.addAll(split);
			}
			/* Cambio de versión: no contar nuevas líneas (REVERTIDO)
			if(i < (lineSplit.length - 1)){
				// Mientras no sea el último, significa que hay una línea nueva
				// entre medias
				tokenList.add(SpecialToken.NEW_LINE.getToken());
			} */
		}
		// Añadimos un token extra al final para indicar que es el fin de frase
		tokenList.add(SpecialToken.END_TOKEN.getToken());
		return tokenList;
	}
	
	public List<String> tokenizeNoEndToken(String str){
		// La lista donde pondremos al final todo
		List<String> tokenList = new ArrayList<String>();
		// Queremos conservar los saltos de línea, si se puede
		String[] lineSplit = str.split("\\r?\\n");
		for(int i = 0; i < lineSplit.length; i++) {
			// Verificamos si cada "palabra" termina en un
			// token de signo de puntuación, para que lo
			// podamos separar.
			String[] tokens = lineSplit[i].split(" ");
			List<String> split = null;
			for(String t : tokens){
				split = splitToken(t);
				tokenList.addAll(split);
			}
			/* Cambio de versión: no contar nuevas líneas 
			if(i < (lineSplit.length - 1)){
				// Mientras no sea el último, significa que hay una línea nueva
				// entre medias
				tokenList.add(SpecialToken.NEW_LINE.getToken());
			} */
		}
		return tokenList;
	}
	
	public boolean isToken(String str){
		if(str.equalsIgnoreCase(SpecialToken.END_TOKEN.getToken())) return true;
		if(str.equalsIgnoreCase(SpecialToken.NEW_LINE.getToken())) return true;
		for(Token t : Token.values()){
			if(t.getToken().equalsIgnoreCase(str))
				return true;
		}
		return false;
	}
	
	
	// Intenta buscar un signo de puntuación y separarlo de la cadena
	// haciendo así dos tokens (texto normal + signo de puntuación)
	private List<String> splitToken(String str){
		List<String> tokenList = new ArrayList<String>();
		for(Token t : Token.values()){
			String token = t.getToken();
			if(str.equalsIgnoreCase(token)){
				// Es un token únicamente, no tenemos nada que hacer
				tokenList.add(token);
				return tokenList;
			} else if(str.endsWith(token)){
				// Cogemos todo lo que no es el token del final
				tokenList.add(str.substring(0, str.length() - token.length()));
				// También ese mismo token
				tokenList.add(token);
				return tokenList;
			}
		}
		// Ningún símbolo de puntuación encontrado
		tokenList.add(str);
		return tokenList;
	}
	
	public static StringTokenizer getInstance(){
		if(instance == null){
			instance = new StringTokenizer();
		}
		return instance;
	}
}
