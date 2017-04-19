package de.rincewind.gui.util;

public class Callback {
	
	private int threshold;
	
	private Runnable action;
	
	public Callback(int threshold, Runnable action) {
		this.threshold = threshold;
		this.action = action;
	}
	
	public synchronized void accept() {
		this.threshold = this.threshold - 1;
		
		if (this.threshold <= 0) {
			this.action.run();
		}
	}
	
}
