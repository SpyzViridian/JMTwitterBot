package com.spyzviridian.markovbot.exceptions;

public class NewFileException extends Exception {
	private static final long serialVersionUID = 4076105204001017232L;
	
	private Object obj;
	
	public NewFileException(String msg, Object obj){
		super(msg);
		this.obj = obj;
	}
	
	public Object getObject(){
		return obj;
	}

}
