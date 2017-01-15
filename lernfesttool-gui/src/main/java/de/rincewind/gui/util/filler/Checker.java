package de.rincewind.gui.util.filler;

public abstract class Checker<T> {
	
	private boolean autoUpdate;
	
	private Runnable onChange;
	
	public Checker() {
		this.autoUpdate = true;
	}
	
	public void setAutoUpdating(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	public boolean isAutoUpdating() {
		return this.autoUpdate;
	}
	
	protected void valueChanged() {
		if (this.isAutoUpdating()) {
			this.onChange.run();
		}
	}
	
	protected void setOnChange(Runnable runnable) {
		this.onChange = runnable;
	}
	
	public abstract boolean check(T value);
	
}
