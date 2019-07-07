package com.spyzviridian.markovbot.learning;

import java.io.Serializable;

public interface IVersionable extends Serializable{
	public int getVersion();
	public boolean isUpToDate();
}
