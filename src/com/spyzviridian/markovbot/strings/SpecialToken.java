package com.spyzviridian.markovbot.strings;

public enum SpecialToken {
	
	END_TOKEN("$$$END_TOKEN$$$"), NEW_LINE("$$$NEW_LINE$$$");
	
	private String token;
	private SpecialToken(String token){
		this.token = token;
	}
	
	public String getToken(){
		return token;
	}
}
