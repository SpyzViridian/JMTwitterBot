package com.spyzviridian.markovbot.strings;

public enum Token {
	
	ELLIPSIS("..."), DOT("."), COLON(":"), SEMICOLON(";"), COMMA(","), QUESTION_MARK("?"), EXCLAMATION_MARK("!");
	
	private String token;
	private Token(String token){
		this.token = token;
	}
	
	public String getToken(){
		return token;
	}
}
