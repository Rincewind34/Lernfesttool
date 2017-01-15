package de.rincewind.gui.util;

public class FeatureTask {
	
	private boolean executed;
	
	private int threshold;
	private int current;
	
	private Runnable action;
	
	public FeatureTask(Runnable action) {
		this(1, action);
	}
	
	public FeatureTask(int threshold, Runnable action) {
		this.threshold = threshold;
		this.action = action;
		this.current = 0;
	}
	
	public synchronized void accept() {
		if (this.executed) {
			return;
		}
		
		this.current = this.current + 1;
		
		if (this.current >= this.threshold) {
			this.action.run();
			this.executed = true;
		}
	}
	
	public boolean isExecuted() {
		return this.executed;
	}
	
	public int getThreshold() {
		return this.threshold;
	}
	
	public int getCurrentCounter() {
		return this.current;
	}
	
}
