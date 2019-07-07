package com.spyzviridian.markovbot.threads;

import java.util.TimerTask;

import com.spyzviridian.markovbot.gui.GUIController;

public class StatusThread extends TimerTask {

	@Override
	public void run() {
		// Obtener la información del sistema
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		
		// En MB
		String status = (float)allocatedMemory/1048576f + " MB / " + (float)maxMemory/1048576f+" MB";
		GUIController.getInstance().updateStatus(status);
	}

}
